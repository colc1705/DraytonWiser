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
        
        standardTile("test", "device.test", decoration: "flat", height: 2, width: 2, inactiveLabel: false) {
            state "default", label:"Test", action:"test", icon:"", backgroundColor:"#FFFFFF"
        }
        
       
		main(["mode"])
        details(["mode","eco"])//,"test", "refresh"])
        
        //Uncomment below for V1 tile layout
		//details(["thermostat", "mode_auto", "mode_manual", "mode_off", "heatingSetpoint", "heatSliderControl", "boost", "boostSliderControl", "refresh"])
	}
}



def parse(description) {
    log.debug "parse()"


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
   
}

def updated() {
	log.debug "Executing updated()"
    
	
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
