/**
 *  Energy Saver
 *
 *  Copyright 2014 SmartThings
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
 * Original version: https://github.com/SmartThingsCommunity/SmartThingsPublic/blob/master/smartapps/smartthings/energy-alerts.src/energy-alerts.groovy
 */
definition(
    name: "Power Meter Notifications",
    namespace: "braclark",
    author: "Brandon Clark",
    description: "Get notified when a power meter sees a use start or stop.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/text.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/text@2x.png"
)

preferences {
	section("When...") {
		input(name: "meter", type: "capability.powerMeter", title: "This Power Meter...", required: true, multiple: false, description: null)
        input(name: "aboveThreshold", type: "number", title: "Reports Above...", required: false, description: "in watts")
        input(name: "belowThreshold", type: "number", title: "Or Reports Below...", required: false, description: "in watts")
	}
    section("Notify via...") {
        input(name: "sms", type: "phone", title: "Text message", description: "10 digit phone number", required: false)
        input(name: "pushNotification", type: "bool", title: "Push notification", description: null, defaultValue: false)
        input(name: "sonos", type: "capability.musicPlayer", title: "Message on this player", required: false)
        input(name: "message", type: "text", title:"Play this custom message", required:false, multiple: false, description: "Overrides default message")
        input(name: "volume", type: "number", title: "Sound volume", description: "0-100", required: false)
    }
	section("Other Options") {
        input(name: "aboveText", type: "text", title: "Above limit custom message text (after sensor name)", required: false)
        input(name: "belowText", type: "text", title: "Below limit custom message text (after sensor name)", required: false)
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"
	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(meter, "power", meterHandler)
}

def meterHandler(evt) {

    def meterValue = evt.value as double

    if (!atomicState.lastValue) {
    	atomicState.lastValue = meterValue
    }

    def lastValue = atomicState.lastValue as double
    atomicState.lastValue = meterValue

    def dUnit = evt.unit ?: "Watts"

    if (aboveThreshold) {
        def aboveThresholdValue = aboveThreshold as int
        if (meterValue > aboveThresholdValue) {
            if (lastValue < aboveThresholdValue) { // only send notifications when crossing the threshold
                if (aboveText) {
                    def msg = "${meter} ${aboveText}"
                } else {
                    def msg = "${meter} is now on."
                }
                sendMessage(msg)
            } 
        }
    }

    if (belowThreshold) {
        def belowThresholdValue = belowThreshold as int
        if (meterValue < belowThresholdValue) {
            if (lastValue > belowThresholdValue) { // only send notifications when crossing the threshold
                if (belowText) {
                    def msg = "${meter} ${belowText}"
                } else {
                    def msg = "${meter} is now off."
                }
                sendMessage(msg)
            } 
        }
    }
}

def sendMessage(msg) {
  if (sms) {
    sendSms(sms, msg)
  }
  if (pushNotification) {
    sendPush(msg)
  }
  if (sonos) {
    if (message) {
      state.sound = textToSpeech(message instanceof List ? message[0] : message)
    } else {
      state.sound = textToSpeech(msg instanceof List ? msg[0] : msg)
    }
    sonos.playTrackAndRestore(state.sound.uri, state.sound.duration, volume)
  }
}