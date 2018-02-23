/**
 *  Temp
 *
 *  Copyright 2018 Colin Chapman
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
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
		// TODO: define your main and details tiles here
        standardTile("switch", "device.switch", width: 3, height: 3, canChangeIcon: true) {
        	state "off", label: 'Off', action: "switch.on", icon: "st.Weather.weather12", backgroundColor: "#ffffff", nextState: "on"
            state "on", label: 'On', action: "switch.off", icon: "st.Weather.weather12", backgroundColor: "#00A0DC", nextState: "off"
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

def setState(state) {
	log.debug "setState($state)"
    sendEvent(name: "switch", value: state)
    }

def setMode(mode) {
	log.debug "setMode(${mode})"
    sendEvent(name: "mode", value: mode)

}

def setBoost(boost) {
	log.debug "setBoost()"
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
