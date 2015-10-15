/**
 *  RaspyThing player
 *  Modified from ObyThing Music
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
    input("port", "text", title: "Port", description: "Port", required: true, defaultvalue: 80, displayDuringSetup: true)
    }
    
metadata {
	definition (name: "RaspbyThing", namespace: "braclark", author: "Brandon Clark") {
	capability "Music Player"
        capability "Refresh"
        
	command "playTrackAtVolume", ["string","number"]
        command "playTrackAndResume", ["string","number","number"]
        command "playTextAndResume", ["string","number"]
        command "playTrackAndRestore", ["string","number","number"]
        command "playTextAndRestore", ["string","number"]
        command "playSoundAndTrack", ["string","number","json_object","number"]
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// Main
		standardTile("main", "device.status", width: 1, height: 1, canChangeIcon: true) {
			state "paused", label:'Paused', action:"music Player.play", icon:"st.Electronics.electronics19", nextState:"playing", backgroundColor:"#ffffff"
			state "playing", label:'Playing', action:"music Player.pause", icon:"st.Electronics.electronics19", nextState:"paused", backgroundColor:"#79b821"
		}

		// Row 1
		standardTile("nextTrack", "device.status", width: 1, height: 1, decoration: "flat") {
			state "next", label:'', action:"music Player.nextTrack", icon:"st.sonos.next-btn", backgroundColor:"#ffffff"
		}
		standardTile("playpause", "device.status", width: 1, height: 1, decoration: "flat") {
			state "default", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"playing", backgroundColor:"#ffffff"
			state "playing", label:'', action:"music Player.pause", icon:"st.sonos.pause-btn", nextState:"paused", backgroundColor:"#ffffff"
            state "paused", label:'', action:"music Player.play", icon:"st.sonos.play-btn", nextState:"playing", backgroundColor:"#ffffff"
		}
		standardTile("previousTrack", "device.status", width: 1, height: 1, decoration: "flat") {
			state "previous", label:'', action:"music Player.previousTrack", icon:"st.sonos.previous-btn", backgroundColor:"#ffffff"
		}

		main "main"

		details([
			"previousTrack","playpause","nextTrack",
		])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def updateState() {
	log.debug "updateState: ${params.message}"
}

def installed() {
	log.debug "installed"
}

// handle commands
def refresh() {
	log.debug "refreshing"
}

def on() {
	log.debug "Turn AirPlay on"
}

def off() {
	log.debug "Turn AirPlay off"
}

def play() {
	log.debug "Executing 'play'"
}

def pause() {
	log.debug "Executing 'pause'"
}

def stop() {
	log.debug "Executing 'stop'"
}

def nextTrack() {
	log.debug "Executing 'nextTrack'"
}

def playTrack(String uri, metaData="") {
	log.debug "Executing 'playTrack'"
    sendCommand("playTrack&track=${uri}")
}

def playTrack(Map trackData) {
	log.debug "Executing 'playTrack'"
    sendCommand("playlist=${trackData.station}")
}

def setLevel(value) {
	log.debug "Executing 'setLevel' to $value"
}

def playText(String msg) {
	log.debug "Executing 'playText'"
	sendCommand("say=$msg")
}

def mute() {
	log.debug "Executing 'mute'"
}

def previousTrack() {
	log.debug "Executing 'previousTrack'"
}

def unmute() {
	log.debug "Executing 'unmute'"
}

def setTrack(String uri, metaData="") {
	log.debug "Executing 'setTrack'"
    sendCommand("track=$uri")
}

def resumeTrack() {
	log.debug "Executing 'resumeTrack'"
}

def restoreTrack() {
	log.debug "Executing 'restoreTrack'"
}

def playTrackAtVolume(String uri, volume) {
    log.trace "playTrackAtVolume($uri, $volume)"
	sendCommand("playTrack&track=${uri}&volume=${volume}")
}

def playTrackAndResume(uri, duration, volume=null) {
    log.debug "playTrackAndResume($uri, $duration, $volume)"
	def cmd = "playTrack&track=${uri}&resume"
	if (volume) {
		cmd += "&volume=${volume}"
    }
    sendCommand(cmd)
}

def playTextAndResume(text, volume=null)
{
    log.debug "playTextAndResume($text, $volume)"
    def sound = textToSpeech(text)
    playTrackAndResume(sound.uri, (sound.duration as Integer) + 1, volume)
}

def playTrackAndRestore(uri, duration, volume=null) {
    log.debug "playTrackAndResume($uri, $duration, $volume)"
	def cmd = "playTrack&track=${uri}&restore"
	if (volume) {
		cmd += "&volume=${volume}"
    }
    sendCommand(cmd)
}

def playTextAndRestore(text, volume=null)
{
    log.debug "playTextAndResume($text, $volume)"
	def sound = textToSpeech(text)
	playTrackAndRestore(sound.uri, (sound.duration as Integer) + 1, volume)
}

def playURL(theURL) {
	log.debug "Executing 'playURL'"
    sendCommand("url=$theURL")
}

def playSoundAndTrack(soundUri, duration, trackData, volume=null) {
	log.debug "playSoundAndTrack($uri, $duration, $trackData, $volume)"
	def cmd = "playTrack&track=${soundUri}&playlist=${trackData.station}"
	if (volume) {
		cmd += "&volume=${volume}"
    }
    sendCommand(cmd)
}

// Private functions used internally
private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private String convertIPtoHex(ipAddress) { 
	String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
	return hex
	}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
	}
	
private getHostAddress() {
	return ip + ":" + port
}

private sendCommand(command) {
	log.trace "SendCommand($command)"
	device.deviceNetworkId = convertIPtoHex(ip) + ":" + convertPortToHex(port)
	log.debug "IP: $ip Port: $port set to hex"
	def path = "/play.php?$command"
	def headers = [:] 
	headers.put("HOST", getHostAddress())
	headers.put("Content-Type", "application/x-www-form-urlencoded")

    def method = "POST"
    
    def result = new physicalgraph.device.HubAction(
        method: method,
        path: path,
        body: command,
        headers: headers
	)
    result
}

private getPlaylists() {
	log.debug "in getPlaylists!!!"
}

private getCallBackAddress()
{
    device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

private subscribeAction(path, callbackPath="") {
    log.trace "SubscribeAction"
}
