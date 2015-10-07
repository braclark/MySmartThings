#Installation Instructions

1. Setup raspberry pi using their setup instructions
1. Setup command line audio player MGP321
  * [Reference used](http://www.raspberrypi-spy.co.uk/2013/06/raspberry-pi-command-line-audio/)
  * Type in command line `sudo apt-get -y install mpg321`
  * Test by typing in command line `mpg321 http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3`
1. Setup web server
   * [Reference used](https://www.raspberrypi.org/documentation/remote-access/web-server/apache.md)
   * `sudo apt-get install apache2 -y`
   * Test from another computer on the network `http://192.168.1.XX` (whatever the Pi's IP address is)
   * `sudo chmod 777 /var/www` I did this so that user "pi" could edit the files without sudo
   * `sudo apt-get install php5 libapache2-mod-php5 -y`
   * `rm index.html`
   * `nano /var/www/index.php`
   * `<?php phpinfo(); />`
   * Test from another computer on the network `http://192.168.1.XX/index.php?track=test`
1. Setup for web requests to play audio
  * Setup so that www-data can use audio
   * [Reference used](http://raspberrypi.stackexchange.com/questions/19482/using-php-exec-command-to-play-audio-on-the-pi-no-audio-group)
   * `sudo chmod 777 /dev/snd/` (not sure of the exact line)
   * Copy play.php into /var/www/
   * Test by requesting the following from a computer on a network.
    * `http://192.168.1.XX/play.php?track=http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3&volume=50`
1. Install Raspbything device type
  * Modified from Obything code
1. Install "Sonos Notify With Sound"
