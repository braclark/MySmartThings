<html>
 <head>
  <title>Php Play</title>
 </head>
<body>
<?php
$output = "No output";

$command = "/usr/bin/mpg321 -g " . $_GET["volume"] . " " . str_replace(" ","+",$_GET["track"]);

//mpg321 wasn't handling https requests, switching them to http requests worked
$command = str_replace("https","http",$command);

$output = shell_exec($command);

echo "Command: " . $command;
echo "<pre>$output</pre>";
?>
</body>
</html>
