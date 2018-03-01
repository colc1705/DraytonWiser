metadata {
	definition (name: "Drayton Wiser Hot Water", namespace: "colc1705", author: "Colin Chapman") {
		capability "Switch"
        capability "Actuator"
        capability "Sensor"
        
        attribute "mode", "string"
        attribute "boost", "string"
        
        command "manualMode"
        command "autoMode"
        command "boostOn"
        command "boostOff"
        
	}


	simulator {
		// TODO: define status and reply messages here
	}
    
    

	tiles {
        standardTile("switch", "device.switch", width: 3, height: 3, canChangeIcon: true) {
        	state "off", label: 'Off', action: "switch.on", icon: "st.Weather.weather12", backgroundColor: "#ffffff"
            state "on", label: 'On', action: "switch.off", icon: "st.Weather.weather12", backgroundColor: "#00A0DC"
        }
        standardTile("mode", "device.mode", width: 1, height:1, decoration: "flat") {
        	state "manual", label: '${currentValue}', action: "autoMode"
            state "auto", label: '${currentValue}', action: "manualMode"
        }
       standardTile("boost", "device.boost", width: 1, height: 1, decoration: "flat") {
       		state "off", label: 'Boost ${currentValue}', action: "boostOn"
            state "on", label: 'Boost ${currentValue}', action: "boostOff", backgroundColor: "#00A042"
       }
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"

}

// handle commands

def on() {
	log.debug "on()"
    parent.turnHotWaterOn()
    
}

def off() {
	log.debug "off()"
	parent.turnHotWaterOff()
}


def setState(state) {
	log.debug "setState($state)"
    sendEvent(name: "switch", value: state)
    }

def setMode(mode) {
	log.debug "setMode(${mode})"
    sendEvent(name: "mode", value: mode)

}

def setBoost(boost) {
	log.debug "setBoost($boost)"
    sendEvent(name: "boost", value: boost)
}

def manualMode() {
	log.debug "manualMode()"
    parent.setHotWaterManualMode(true)
}

def autoMode() {
	log.debug "autoMode()"
    parent.setHotWaterManualMode(false)
}

def boostOn() {
	log.debug "boostOn()"
	parent.setHotWaterBoost(30)
}

def boostOff() {
	log.debug "boostOff()"
	parent.setHotWaterBoost(0)
}