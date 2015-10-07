

1. Setup raspberry pi
2. Setup command line audio player MGP321
   2.1 http://www.raspberrypi-spy.co.uk/2013/06/raspberry-pi-command-line-audio/
   2.2 sudo apt-get -y install mpg321
   2.3 mpg321 http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3
3. Setup web server
   3.1 https://www.raspberrypi.org/documentation/remote-access/web-server/apache.md
   3.2 sudo apt-get install apache2 -y
   3.3 test from another computer on the network http://192.168.1.10 (whatever the Pi's IP address is)
   3.4 sudo chmod 777 /var/www
   3.5 sudo apt-get install php5 libapache2-mod-php5 -y
   3.6 sudo rm index.html
   3.7 sudo nano index.php
   3.8 <?php phpinfo(); />
4. Setup so that www-data can use audio
   4.1 http://raspberrypi.stackexchange.com/questions/19482/using-php-exec-command-to-play-audio-on-the-pi-no-audio-group
   4.2 sudo chmod 777 /dev/snd/ (not sure of the exact line)
5. Put play.php into /var/www/
6. Test by requesting the following from a computer on a network.
   6.1 http://192.168.1.10/play.php?track=http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3&volume=50
7. Install device type
8. Install "Sonos Notify With Sound"
