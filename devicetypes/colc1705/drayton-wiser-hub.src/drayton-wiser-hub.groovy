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
        attribute "comfort", "string"
        
        command "test"
        command "ecoOn"
        command "ecoOff"
        command "homeMode"
        command "awayMode"
        command "comfortOn"
        command "comfortOff"
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    

	tiles(scale: 2) {
  
       
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state("default", label:'refresh', action:"refresh", icon:"st.secondary.refresh-icon")
		}
        
        standardTile("eco", "device.eco", inactiveLabel: false, decoration: "flat", width: 3, height: 3) {
        	state "on", label: "On", action: "ecoOff", icon:"st.Outdoor.outdoor3", backgroundColor:"#00A042"
            state "off", label: "Eco Off", action: "ecoOn", icon: ""
        }
        
        standardTile("mode", "device.mode", inactiveLabel: false, decoration: "flat", width: 3, height: 3) {
        	state "away", label: "Away", action: "homeMode", icon: "st.Transportation.transportation2"
            state "home", label: "Home", action: "awayMode", icon: "st.Home.home2"
        }
        
        standardTile("comfort", "device.comfort", inactiveLavel: false, decoration: "flat", width: 3, height: 3) {
        	state "true", label: "On", action: "comfortOff", icon:"st.Home.home22", backgroundColor:"#00A042"
            state "false", label: "Comfort Off", action: "comfortOn", icon: ""
        }
        
        standardTile("test", "device.test", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Test", action:"test", icon:"", backgroundColor:"#FFFFFF"
        }
        
       
		main(["mode"])
        details(["mode","eco","comfort"])//,"test", "refresh"])
        
        //Uncomment below for V1 tile layout
		//details(["thermostat", "mode_auto", "mode_manual", "mode_off", "heatingSetpoint", "heatSliderControl", "boost", "boostSliderControl", "refresh"])
	}
}



def parse(description) {
    logEvent("parse()")


}

def initialize() {
	logEvent("Initializing")
	//state.json
    //state.action
}


def installed() {
	logEvent("Executing installed()")
    //createChildDevices()
    //response(refresh() + configure())
}

def configure() {
	logEvent("Executing configure()")
    
}
    
    
def refresh() {
	logEvent("Executing refresh()")
   
}

def updated() {
	logEvent("Executing updated()")
    
	
}

def test() {
	logEvent("test()")
    parent.test(device.deviceNetworkId)
    
}

def setEco(ecoMode) {
	logEvent("setEco($ecoMode)")
    if (ecoMode) {
    	sendEvent(name: "eco", value: "on")
    } else {
    	sendEvent(name: "eco", value: "off")	
    }
}

def setMode(mode) {
	logEvent("setMode($mode)")
    if (mode == "Away") {
    	sendEvent(name: "mode", value: "away")
    } else {
    	sendEvent(name: "mode", value: "home")
    }
    
}

def setComfort(mode) {
	logEvent("setComfort($mode)")
    if (mode) {
    	sendEvent(name: "comfort", value: "true")
    } else {
    	sendEvent(name: "comfort", value: "false")
    }

}

def ecoOn() {
	logEvent("ecoOn()")
    parent.setEcoMode(true)
}
    
def ecoOff() {
	logEvent("ecoOff()")
    parent.setEcoMode(false)
}

def comfortOn() {
	logEvent("comfortOn()")
    parent.setComfort(true)
}

def comfortOff() {
	logEvent("comfortOff()")
    parent.setComfort(false)
}

def homeMode() {
	logEvent("homeMode()")
    parent.setAwayMode(false)
}

def awayMode() {
	logEvent("awayMode()")
    parent.setAwayMode(true)
}

def logEvent(event) {
	if (parent.showDebugInfo()) {
    	log.debug event
    } else {
    	//log.debug "Logging disabled"
    }
}
