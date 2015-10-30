<html>
 <head>
  <title>Php Play</title>
 </head>
<body>
<?php
$upload_path = "uploads/";
$output = "No output";

if (($_GET["volume"] >= 0) && ($_GET["volume"] <= 100)) {
	$volume = $_GET["volume"];
	} else {
	$volume = 100;
	}

if (isset($_GET["track"])) {
  if (file_exists($upload_path.basename($_GET["track"]))){
    echo "Playing local file";
    $command = "/usr/bin/mpg321 -g " . $volume . " " . $upload_path.basename($_GET["track"]);
    } else {
    echo "Local file not found. Downloading file. ";
    file_put_contents($upload_path.basename($_GET["track"]), file_get_contents($_GET["track"]));
		if (file_exists($upload_path.basename($_GET["track"]))){
		  echo "Playing freshly downloaded local copy.";
      $command = "/usr/bin/mpg321 -g " . $volume . " " . $upload_path.basename($_GET["track"]);
			}else{
      $command = "/usr/bin/mpg321 -g " . $volume . " " . str_replace(" ","+",$_GET["track"]);
		  echo "Download not found. Playing remote version.";
			}
    }
  
  $command = str_replace("https","http",$command);
  echo "<pre>Command: ".$command."</pre>";
  $output = shell_exec($command." 2>&1");

  echo "<pre>Ouput: ".$output."</pre>";
} else {
  if ($_FILES){
    if (strlen($_POST['NewName']) > 0) {
      $target_path = $upload_path.$_POST['NewName'];
      } else {
      $target_path = $upload_path.basename($_FILES['uploadedfile']['name']);
      }
    if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'],$target_path)) {
      echo "The file ".basename($_FILES['uploadedfile']['name'])." has been uploaded as ".$target_path."<br />";
      } else {
      echo "There was an error uploading the file.";
      }
  } else {
    echo "No file specified to play.";
  }
  echo "<br><hr><br><form enctype=\"multipart/form-data\" action=\"test.php\" method=\"POST\">";
  echo "Choose a file to upload: <input name=\"uploadedfile\" type=\"file\"><br><br>";
  echo "New Name: <input type=\"text\" name=\"NewName\"><br><br>";
  echo "<input type=\"submit\" value=\"Upload File\">";
  echo "</form><br><hr><br>";
  echo "<pre> Listing of ".$upload_path."\n";
  $fileslist = scandir($upload_path);
echo print_r($fileslist) . "</pre>";
//phpinfo();
}
?>
</body>
</html>
