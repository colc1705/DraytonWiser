metadata {
	definition (name: "Drayton Wiser TRV", namespace: "colc1705", author: "Colin Chapman", ocfDeviceType: "oic.d.thermostat", mnmn: "SmartThings", vid: "SmartThings-smartthings-Z-Wave_Thermostat") {
		capability "Thermostat"
        capability "Sensor"
        capability "Refresh"
        capability "Health Check"
        
        attribute "mode", "string"
        attribute "boost", "string"
        attribute "demand", "number"
        attribute "windowState", "string"
        
        command "heatingSetpointup" //"spDown"
        command "heatingSetpointDown" //"spUp"
        command "manualMode"
        command "autoMode"
        command "test"
        command "boostOn"
        command "boostOff"
        
        
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
        multiAttributeTile(name:"thermostatMulti", type:"thermostat", width:6, height:4) {
        	tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
            	attributeState("default", label:'${currentValue}°C', unit:"C", backgroundColors:[
                    [value:  0, color: "#153591"],
                	[value:  7, color: "#1E9CBB"],
                	[value: 15, color: "#90D2A7"],
                	[value: 23, color: "#44B621"],
                	[value: 29, color: "#F1D801"],
                	[value: 33, color: "#D04E00"],
                	[value: 36, color: "#BC2323"]
                    ], icon: "st.Weather.weather2")
            }
            tileAttribute("device.heatingSetpoint", key: "VALUE_CONTROL") {
            	attributeState("VALUE_UP", action: "heatingSetpointUp")
                attributeState("VALUE_DOWN", action: "heatingSetpointDown")
            }
            
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
            	attributeState("default", label: '${currentValue}', unit: "°C")
            }
            
        
        }
        
        standardTile("mode", "device.mode", decoration: "flat", width: 2, height: 2) {
        	state "manual", label: '${currentValue}', action:"autoMode"
            state "auto", label: '${currentValue}', action:"manualMode"
        }
        
        standardTile("boost", "device.boost", decoration: "flat", width: 2, height: 2) {
        	state "off", label: 'Boost ${currentValue}', action: "boostOn"
            state "on", label: 'Boost ${currentValue}', action: "boostOff", backgroundColor: "#00A042"
        }
        
        valueTile("demand", "device.demand", decoration: "flat", width: 2, height: 2) {
        	state "default", label: '${currentValue}%', unit: "%", backgroundColors: [[value:0, color: '#ffffff'],[value:10, color: '#153591'], [value: 30, color: '#1e9cbb'], [value: 50, color: '#90d2a7'], [value: 60, color: '#44b621'], [value: 80, color: '#f1d801'], [value: 90, color: '#D04E00'], [value:100, color: '#bc2323']]
            
        }
        
        standardTile("windowState", "device.windowState", decortation: "flat", width: 2, height: 2) {
        	state "Open", label: 'Window ${currentValue}', backgroundColor: "#BC2323"
            state "Closed", label: 'Window ${currentValue}'
        }
        
        standardTile("test", "device.getHubConfig", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Test", action:"test", icon:"", backgroundColor:"#FFFFFF"
        }
        
        main(["thermostatMulti"])
        details(["thermostatMulti","mode","boost","demand","windowState"])
	}
}

def updated() {
	logEvent("updated()")
   
}

def parse(String description) {
	logEvent("parse()")
    
    
    

}

def test() {
	logEvent("test()")
    parent.test(device.deviceNetworkId)
}

def heatingSetpointUp() {
	logEvent("heatingSetpointUp()")
    def currentSP = device.currentState("heatingSetpoint").getDoubleValue()
    def newSP = currentSP + 0.5
    logEvent("Current setting: " + currentSP)
    sendEvent(name: "heatingSetpoint", value: newSP, unit: "C", state: "heat")
    sendEvene(name: "thermostatSetpoint", value: newSP, unit: "C", state: "heat")
    parent.setPoint(device.deviceNetworkId, newSP)
    
}

def heatingSetpointDown() {
	logEvent("heatingSetpointDown()")
    def currentSP = device.currentState("heatingSetpoint").getDoubleValue()
    def newSP = currentSP - 0.5
    logEvent("Current setting: " + currentSP)
    sendEvent(name: "heatingSetpoint", value: newSP, unit: "C", state: "heat")
    sendEvent(name: "thermostatSetpoint", value: newSP, unit: "C", state: "heat")
    parent.setPoint(device.deviceNetworkId, newSP)
}

def setHeatingSetpoint(setpoint) {
	logEvent("setHeatingSetpoint($setpoint)")
    sendEvent(name: "heatingSetpoint", value: setpoint, unit: "C", state: "heat")
    sendEvent(name: "thermostatSetpoint", value: setpoint, unit: "C", state: "heat")
    parent.setPoint(device.deviceNetworkId, setpoint)
}

def setTemp(temp, setPoint) {
 	logEvent(device.name + " is " + temp + "°C")
    sendEvent(name: "temperature", value: temp, unit: "C", state: "heat")
    sendEvent(name: "heatingSetpoint", value: setPoint, unit: "C", state: "heat")
    sendEvent(name: "thermostatSetpoint", value: setpoint, unit: "C", state: "heat")
}

def setMode(mode) {
	logEvent("setMode($mode)")
    sendEvent(name: "mode", value: mode)
}

def setBoost(boost) {
	logEvent("setBoost($boost)")
    sendEvent(name: "boost", value: boost)
}

def setWindowState(wState) {
	logEvent("setWindowState($wState)")
    sendEvent(name: "windowState", value: wState)
}
    
def autoMode() {
	logEvent("autoMode()")
    parent.setRoomManualMode(device.deviceNetworkId, false)
}

def manualMode() {
	logEvent("manualMode()")
    parent.setRoomManualMode(device.deviceNetworkId, true)
}

def boostOn() {
	logEvent("boostOn()")
    def currentTemp = device.currentState("temperature").getDoubleValue()
    currentTemp = 0.5*(Math.round(currentTemp/0.5))
    def setPoint = currentTemp + 2
	parent.setRoomBoost(device.deviceNetworkId,30,setPoint)
}

def boostOff() {
	logEvent("boostOff()")
	parent.setRoomBoost(device.deviceNetworkId,0,0)
}

def setOutputState(outputState) {
	logEvent("setOutputState($outputState)")
    sendEvent(name: "outputState", value: outputState)
}

def setDemand(demand) {
	logEvent("setDemand($demand)")
    sendEvent(name: "demand", value: demand)
}

def logEvent(event) {
	if (parent.showDebugInfo()) {
    	log.debug event
    } else {
    	//log.debug "Logging disabled"
    }
}