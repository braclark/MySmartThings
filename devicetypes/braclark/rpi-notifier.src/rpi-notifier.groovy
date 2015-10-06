/**
 *  RPi Notifier
 *
 *  Copyright 2015 Brandon Clark
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

preferences {
    input("ip", "text", title: "IP Address", description: "Enter IP Address", required: true, defaultvalue: "192.168.1.1", displayDuringSetup: true)
    input("port", "text", title: "Port", description: "", required: true, defaultvalue: 80, displayDuringSetup: true)
    input("DefaultVolume", "number", title: "Default Volume", description: "0-100 (Optional)", required: false, defaultvalue: 100, displayDuringSetup: true)
    }

metadata {
	definition (name: "RPi Notifier", namespace: "braclark", author: "Brandon Clark") {
		capability "Alarm"
		capability "Music Player"
		capability "Speech Synthesis"
		capability "Tone"
        
        attribute "DefaultVolume", "number"
	}

	simulator {
		// TODO: define status and reply messages here
	}

tiles {
    standardTile("switchTile", "device.switch", width: 2, height: 2, canChangeIcon: true, label: 'label', icon: "st.switches.switch.off")
    standardTile("refreshTile", "device.power", decoration: "ring") {
        state "default", label:'label2', icon:"st.secondary.refresh"
        }
    main "switchTile"
    details(["switchTile","refreshTile"])
}

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'alarm' attribute
	// TODO: handle 'status' attribute
	// TODO: handle 'level' attribute
	// TODO: handle 'trackDescription' attribute
	// TODO: handle 'trackData' attribute
	// TODO: handle 'mute' attribute

}

// Definitions
def playTrackAndResume(SoundURI, duration, volume=null) {
    log.debug "playTrackAndResume($SoundURI, $duration, $volume)"
    PostToDevice(SoundURI,volume)
}

def playTrackAndRestore(SoundURI, duration, volume=null) {
	log.debug "playTrackAndRestore($SoundURI, $duration, $volume)"
    PostToDevice(SoundURI,volume)
}

def playSoundAndTrack(SoundURI, duration, trackData, volume=null) {
	log.debug "playSoundAndTrack($SoundURI, $duration, $trackUri, $volume)"
    PostToDevice(SoundURI,volume)
}

def playTrack(String SoundURI, metaData="") {
	log.debug "Executing 'playTrack' " + SoundURI
    PostToDevice(SoundURI,null)
}

def beep() {
	log.debug "Executing 'beep'"
    PostToDevice("bell2.mp3",null)
}

//the following defs are empty
//TODO edit defs below
def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def strobe() {
	log.debug "Executing 'strobe'"
	// TODO: handle 'strobe' command
}

def siren() {
	log.debug "Executing 'siren'"
	// TODO: handle 'siren' command
}

def both() {
	log.debug "Executing 'both'"
	// TODO: handle 'both' command
}

def play() {
	log.debug "Executing 'play'"
	// TODO: handle 'play' command
}

def pause() {
	log.debug "Executing 'pause'"
	// TODO: handle 'pause' command
}

def stop() {
	log.debug "Executing 'stop'"
	// TODO: handle 'stop' command
}

def nextTrack() {
	log.debug "Executing 'nextTrack'"
	// TODO: handle 'nextTrack' command
}

def setLevel() {
	log.debug "Executing 'setLevel'"
	// TODO: handle 'setLevel' command
}

def playText() {
	log.debug "Executing 'playText'"
	// TODO: handle 'playText' command
}

def mute() {
	log.debug "Executing 'mute'"
	// TODO: handle 'mute' command
}

def previousTrack() {
	log.debug "Executing 'previousTrack'"
	// TODO: handle 'previousTrack' command
}

def unmute() {
	log.debug "Executing 'unmute'"
	// TODO: handle 'unmute' command
}

def setTrack() {
	log.debug "Executing 'setTrack'"
	// TODO: handle 'setTrack' command
}

def resumeTrack() {
	log.debug "Executing 'resumeTrack'"
	// TODO: handle 'resumeTrack' command
}

def restoreTrack() {
	log.debug "Executing 'restoreTrack'"
	// TODO: handle 'restoreTrack' command
}

def speak() {
	log.debug "Executing 'speak'"
	// TODO: handle 'speak' command
}

//following "borrowed" from obything code
private PostToDevice(SoundURI,volume=null) {
	log.trace "Post to device"
	log.debug "176 PostToDevice with SoundURI: $SoundURI and volume: $volume"
    device.deviceNetworkId = getHostAddress()
    def path = "/play.php"
    log.debug "path: $path"
    def headers = [:] 
    def body = "body dummy"
    headers.put("HOST", "${ip}:${port}")
    headers.put("Content-Type", "application/x-www-form-urlencoded")
    log.debug "headers: $headers"
    def method = "POST"
    log.debug "method: $method"
    def result = new physicalgraph.device.HubAction(
        method: method,
        path: path,
        body: body,
        headers: headers
	)
    result
}


// following "borrowed" from
// https://github.com/nicholaswilde/smartthings/blob/master/device-types/raspberry-pi/raspberry-pi.device.groovy
private PostToDeviceOLD(SoundURI,volume=null){
	log.trace "176 PostToDevice with SoundURI: $SoundURI and volume: $volume"
    device.deviceNetworkId = getHostAddress()
    if (volume==null) volume = DefaultVolume
	def headers = [HOST: "${ip}:${port}"]
    log.debug "180 headers are $headers"
	def uri = "/play.php"
	log.debug "182 uri is $uri"
	def result = new physicalgraph.device.HubAction(
  		method: "GET",
  		path: uri,
  		headers: headers
		) //, query: [SoundURI: "$SoundURI", volume: "$volume"]
	log.trace "188\n${result.action.encodeAsHTML()}"
	return result    
}

// gets the address of the device
private getHostAddress() {
	log.debug "212 Using IP: $ip and port: $port for device: ${device.id}"
	return convertIPtoHex(ip) + ":" + convertPortToHex(port)
	}

private String convertIPtoHex(ipAddress) { 
	String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
	return hex
	}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
	}