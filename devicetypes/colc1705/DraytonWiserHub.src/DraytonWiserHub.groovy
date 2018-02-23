
metadata {
	definition (name: "Drayton Wiser Hub", namespace: "colc1705", author: "Colin Chapman") {
		capability "Actuator"
		capability "Polling"
		capability "Refresh"
		capability "Thermostat Mode"
        capability "Bridge"
        capability "Switch"
        
        attribute "eco", "string"
        attribute "mode", "string"
        
        command "test"
        command "ecoOn"
        command "ecoOff"
        command "homeMode"
        command "awayMode"
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    preferences {
    	input name: "hubIP", type: "text", title: "HeatHub IP Address", description: "Enter the IP address of your HeatHub", required: true, displayDuringSetup: true
        input name: "systemSecret", type: "text", title: "System secret", description: "Enter the secret token for your system", required: true, displayDuringSetup: true
        
    
    }

	tiles(scale: 2) {
  
       
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state("default", label:'refresh', action:"refresh", icon:"st.secondary.refresh-icon")
		}
        
        standardTile("eco", "device.eco", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
        	state "on", label: "On", action: "ecoOff", icon:"st.Outdoor.outdoor3", backgroundColor:"#00A042"
            state "off", label: "Eco Off", action: "ecoOn", icon: ""
        }
        
        standardTile("mode", "device.mode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
        	state "away", label: "Away", action: "homeMode", icon: "st.Transportation.transportation2"
            state "home", label: "Home", action: "awayMode", icon: "st.Home.home1"
        }
        
        standardTile("test", "device.test", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Test", action:"test", icon:"", backgroundColor:"#FFFFFF"
        }
        
       
		main(["mode"])
        details(["mode","eco","test", "refresh"])
        
        //Uncomment below for V1 tile layout
		//details(["thermostat", "mode_auto", "mode_manual", "mode_off", "heatingSetpoint", "heatSliderControl", "boost", "boostSliderControl", "refresh"])
	}
}



def parse(description) {
    log.debug "parse()"
    def msg = parseLanMessage(description)
    def events = []

    //def headersAsString = msg.header // => headers as a string
    //def headerMap = msg.headers      // => headers as a Map
    //def body = msg.body              // => request body as a string
    //def status = msg.status          // => http status code of the response
    //def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    //def xml = msg.xml                // => any XML included in response body, as a document tree structure
    //def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
   
	log.debug "Action: " + state.action   
	log.debug "HTTP Status: " + msg.status   
	log.debug msg.json   
    
    if (state.action == "Hub Config" | state.action == "Installation") state.json = msg.json
    
    if (msg.status==200 | msg.status==403) {
    	log.debug "Action successful"
        if (state.action == "ecoOn") events << createEvent(name: "eco", value: "on")
        if (state.action == "ecoOff") events << createEvent(name: "eco", value: "off")
        if (state.action == "homeMode") state.action = "homeModeChange"
        if (state.action == "awayMode") state.action = "awayModeChange"
        if (state.action == "homeModeChange") events << createEvent(name: "mode", value: "home")
        if (state.action == "awayModeChange") events << createEvent(name: "mode", value: "away")
        
    }
    
    
	if (state.action == "Installation") {
    log.debug ("Performing Initial Setup")
	def rooms = state.json.Room   
   
    if (state.json.HotWater) {
    	log.debug "Got hot water"
        createChildDevices(rooms, true)
    } else {
    	log.debug "No hot water"
    	createChildDevices(rooms, false)
    }
    
	def children = getChildDevices()
    def dni
    def roomId
    
    children.each { child ->
        dni = child.deviceNetworkId
        roomId = dni.drop(14)
        if (roomId == "HW" ) {
        	log.debug "This is the hotwater"
            child.setState(state.json.HotWater.WaterHeatingState)
        } else {
        	log.debug "Retrieving room info"
            rooms.each { room ->
            	if (roomId == room.id.toString()) {
                	
                    child.setTemp(room.CalculatedTemperature/10, room.CurrentSetPoint/10)
                }
            }
        }
	}
    }
    return events

}

def initialize() {
	log.debug "Initializing"
	//state.json
    //state.action
}


def installed() {
	log.debug "Executing installed()"
    //createChildDevices()
    //response(refresh() + configure())
}

def configure() {
	log.debug "Executing configure()"
    
}
    
    
def refresh() {
	log.debug "Executing refresh()"
    state.action = "Hub Config"
	getHubConfig()
}

def updated() {
	log.debug "Executing updated()"
    log.debug state.action
    if (state.action != "Installation") {
    	device.setDeviceNetworkId(setDNI(hubIP))
    	state.action = "Installation"
    	getHubConfig()
    }
	
}

def test() {
	log.debug "test()"
    parent.test(device.deviceNetworkId)
    
}

def setEco(ecoMode) {
	log.debug "setEco($ecoMode)"
    if (ecoMode) {
    	sendEvent(name: "eco", value: "on")
    } else {
    	sendEvent(name: "eco", value: "off")	
    }
}

def setMode(mode) {
	log.debug "setMode($mode)"
    if (mode == "Away") {
    	sendEvent(name: "mode", value: "away")
    } else {
    	sendEvent(name: "mode", value: "home")
    }
    
}

def ecoOn() {
	log.debug "ecoOn()"
    parent.setEcoMode(true)
}
    
def ecoOff() {
	log.debug "ecoOff()"
    parent.setEcoMode(false)
}

def homeMode() {
	log.debug "homeMode()"
    parent.setAwayMode(false)
}

def awayMode() {
	log.debug "awayMode()"
    parent.setAwayMode(true)
}

def hotWaterManual() {
	log.debug "hotWaterManual()"
    state.action = "hotWaterManual"
    setHotWaterManualMode(true)
}

def hotWaterAuto() {
	log.debug "hotWaterAuto()"
    state.action = "hotWaterAuto"
    setHotWaterManualMode(false)
}

private void createChildDevices(rooms, hotwater) {
	log.debug "createChildDevices()"
	state.oldLabel = device.label
    def children = getChildDevices().deviceNetworkId
    def child
    def dni
    //for each room
    for (HashMap room : rooms) {
    	dni = device.deviceNetworkId + "-" + room.id
        if (children.contains(dni)) {
        	log.debug "Device ${dni} already exists"
        } else {
        	try {
        		child = addChildDevice("Drayton Wiser Room", "${device.deviceNetworkId}-${room.id}", null, [completedSetup: true, label: "${device.displayName} (${room.Name})", isComponent: false, componentName: "${room.Name}", componentLabel: "${room.Name}"])
                child.updateDataValue("systemSecret", systemSecret)
                child.updateDataValue("hubIP", hubIP)
        	} catch(e) {
        		log.debug "Error creating child device ${e}"
        	}
        }
    }
    
    //then add hot water
    if (hotwater) {
    	dni = device.deviceNetworkId + "-HW"
        if (children.contains(dni)) {
        	log.debug "Device ${dni} already exists"
        } else {
    		try {
    			child = addChildDevice("colc1705","Drayton Wiser Hot Water", "${device.deviceNetworkId}-HW", null,[completedSetup: true, label: "${device.displayName} Hot Water", isComponent: false, componentName: "HW", componentLabel: "HW"])
                child.updateDataValue("systemSecret", systemSecret)
                child.updateDataValue("hubIP", hubIP)
			} catch(e) {
    			log.debug "Error creating child device ${e}"
    		}
        }
    }
}

void waterOn() {
	log.debug "Turning Hot Water on"
    
}

void waterOff() {
	log.debug "Turning Hot Water off"
    
}

void setPoint(dni, setPoint) {
	log.debug "setPoint($dni, $setPoint)"
	def roomId = dni.drop(14)
    
    if (roomId == "HW" ) {
      	log.debug "This is the hotwater"
        
    } else {
    	state.action = "setPoint" + roomId
        def payload
        payload = "{\"RequestOverride\":{\"Type\":\"Manual\", \"SetPoint\":" + setPoint + "}}"
        sendMessageToHeatHub(getRoomsEndpoint() + roomId.toString(), "PATCH", payload)
    }
    
}


def getHubConfig() {
	log.debug "getHubConfig()"
    
    def result = new physicalgraph.device.HubAction(
    	method: "GET",
        path: "/data/domain/",
        headers: [
        	HOST: getHostAddress(),
            SECRET: systemSecret
        ]
        )
    //state.action = "Hub Config"
    return result
}

def getHubUrl(path) {
	log.debug "getHubConfig()"
    
    def result = new physicalgraph.device.HubAction(
    	method: "GET",
        path: path,
        headers: [
        	HOST: getHostAddress(),
            SECRET: systemSecret
        ]
        )
    state.action = "Hub Config"
    log.debug result
    return result
}

def sendMessageToHeatHub(path, method, content) {
	log.debug "sendMessageToHeatHub($path, $method, $content)"
    def headers = [:]
    headers.put("HOST", getHostAddress())
    headers.put("SECRET", systemSecret)
    headers.put("Content-Type", "application/json")
 	
    def result = new physicalgraph.device.HubAction(
    		method: method,
	        path: path,
    	    body: content,
        	headers: headers
            )
        
   	//state.action = "Message to HH"
    //log.debug state.action
    return result
}

def setAwayMode(awayMode) {
	log.debug "setAwayMode($awayMode)"
	def payload
    def payload2
	payload = "{\"Type\":" + (awayMode ? "2" : "0") + ", \"setPoint\":" + (awayMode ? "50" : "0") + "}"
	payload2 = "{\"Type\":" + (awayMode ? "2" : "0") + ", \"setPoint\":" + (awayMode ? "-200" : "0") + "}"
    return [sendMessageToHeatHub(getSystemEndpoint() + "RequestOverride", "PATCH", payload), delayAction(1000), sendMessageToHeatHub(getHotwaterEndpoint() + "2/RequestOverride", "PATCH", payload2)]
}


private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

def setHotWaterManualMode(manualMode) {
	log.debug "setHotWaterManualMode($manualMode)"
	def payload
    def payload2
    payload = "{\"Mode\":\"" + (manualMode ? "Manual" : "Auto") + "\"}"
    payload2 = "{\"RequestOverride\":{\"Type\":\"None\",\"Originator\" :\"App\",\"DurationMinutes\":0,\"SetPoint\":0}}"
    return [sendMessageToHeatHub(getHotWaterEndpoint() + "2", "PATCH", payload), delayAction(1000), sendMessageToHeatHub(getHotwaterEndpoint() + "2", "PATCH", payload2)]
}

def setEcoMode(ecoMode) {
		log.debug "setEcoMode($ecoMode)"
		def payload
        payload = "{\"EcoModeEnabled\":" + ecoMode + "}";
        sendMessageToHeatHub(getSystemEndpoint(), "PATCH", payload);
        //refresh();
    }
    
def setRoomSetPoint(roomName, setPoint) {
		log.debug "setRoomSetPoint($roomName, $setPoint)"
        room = getRoom(roomName)
        if (room == null) {
            return
        }
		def payload
        payload = "{\"RequestOverride\":{\"Type\":\"Manual\", \"SetPoint\":" + setPoint + "}}"
        sendMessageToHeatHub(getRoomsEndpoint() + room.id.toString(), "PATCH", payload)
        
    }
    
def setRoomSetPointId(roomId, setPoint) {
		log.debug "setRoomSetPointId($roomId, $setPoint)"
        def payload
        payload = "{\"RequestOverride\":{\"Type\":\"Manual\", \"SetPoint\":" + setPoint + "}}"
        sendMessageToHeatHub(getRoomsEndpoint() + roomId.toString(), "PATCH", payload)
        
    }

def setRoomManualMode(roomName, manualMode) {
		log.debug "setRoomManualMode($roomName, $manualMode)"
        room = getRoom(roomName)
        if (room == null) {
            return
        }
		def payload
        def payload2
        payload = "{\"Mode\":\"" + (manualMode ? "Manual" : "Auto") + "\"}"
        payload2 = "{\"RequestOverride\":{\"Type\":\"None\",\"Originator\" :\"App\",\"DurationMinutes\":0,\"SetPoint\":0}}"       
        return [sendMessageToHeatHub(getRoomsEndpoint() + room.id.toString(), "PATCH", payload), delayAction(1000), sendMessageToHeatHub(getRoomsEndpoint() + room.id.toString(), "PATCH", payload2)]
}

def setRoomManualModeId(roomId, manualMode) {
		log.debug "setRoomManualModeId($roomId, $manualMode)"
        def payload
        def payload2
        payload = "{\"Mode\":\"" + (manualMode ? "Manual" : "Auto") + "\"}"
        payload2 = "{\"RequestOverride\":{\"Type\":\"None\",\"Originator\" :\"App\",\"DurationMinutes\":0,\"SetPoint\":0}}"       
        return [sendMessageToHeatHub(getRoomsEndpoint() + roomId, "PATCH", payload), delayAction(1000), sendMessageToHeatHub(getRoomsEndpoint() + roomId, "PATCH", payload2)]
}


private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

def getRoom(name) {
	for (HashMap room : state.json.Room) {
    	if (room.Name.toLowerCase().equals(name.toLowerCase())) {
        	return room
        }
    }
    return null
}

private getHostAddress() {
		log.debug "getHostAddress()"
        def parts = device.deviceNetworkId.split(":")
        def ip
        def port
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            log.warn "Can't figure out ip and port for device: ${device.id}"
            return "192.168.100.26:80"
        }
    

    //log.debug "Using IP: $ip and port: $port for device: ${device.id}"
    return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
    return Integer.parseInt(hex,16)
}

private setDNI(ip, port = 80) {
	def dni
    def iphex = convertIPtoHex(ip).toUpperCase()
    def porthex = convertPortToHex(port).toUpperCase()
    dni = "$iphex:$porthex"
    return dni

}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex
}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}

private String convertHexToIP(hex) {
    return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

def getDeviceEndpoint() {
	return "/data/domain/Device/"
}

def getRoomstatsEndpoint() {
    return "/data/domain/RoomStat/"
    }
    
def getTRVsEndpoint() {
    return "/data/domain/SmartValve/"
    }
    
def getRoomsEndpoint() {
    return "/data/domain/Room/"
    }
    
def getSchedulesEndpoint() {
    return "/data/domain/Schedule/";
    }
    
def getHeatChannelsEndpoint() { 
    return "/data/domain/HeatingChannel/"
    }
    
def getSystemEndpoint() {
    return "/data/domain/System/"
    }
    
def getStationEndpoint() {
    return "/data/network/Station/"
    }
    
def getDomainEndpoint() {
    return "/data/domain/"
    }
    
def getHotwaterEndpoint() {
    return "/data/domain/HotWater/"
    }
