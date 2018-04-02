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
	logEvent("Parsing '${description}'")

}

// handle commands

def on() {
	logEvent("on()")
    parent.turnHotWaterOn()
    
}

def off() {
	logEvent("off()")
	parent.turnHotWaterOff()
}


def setState(state) {
	logEvent("setState($state)")
    sendEvent(name: "switch", value: state)
    }

def setMode(mode) {
	logEvent("setMode(${mode})")
    sendEvent(name: "mode", value: mode)

}

def setBoost(boost) {
	logEvent("setBoost($boost)")
    sendEvent(name: "boost", value: boost)
}

def manualMode() {
	logEvent("manualMode()")
    parent.setHotWaterManualMode(true)
}

def autoMode() {
	logEvent("autoMode()")
    parent.setHotWaterManualMode(false)
}

def boostOn() {
	logEvent("boostOn()")
	parent.setHotWaterBoost(30)
}

def boostOff() {
	logEvent("boostOff()")
	parent.setHotWaterBoost(0)
}

def logEvent(event) {
	if (parent.showDebugInfo()) {
    	log.debug event
    } else {
    	//log.debug "Logging disabled"
    }
}