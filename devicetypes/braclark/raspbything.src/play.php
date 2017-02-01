<?php
$upload_path = "uploads/";
$output["Debug"] = "";
$volume = 100;

if (is_numeric($_GET["volume"])) {
	$volume = $_GET["volume"];
	} 

$command = "/usr/bin/mpg321";
// Quiet mode (no title or boilerplate)
$command .= " -q";
// Set gain (audio volume) to N (0-100)
$command .= " -g $volume";

if (isset($_GET["track"])) {
  if (strlen($upload_path.basename($_GET["track"]))<44) {
    if (file_exists($upload_path.basename($_GET["track"]))){
      $output["Debug"] .= "Playing local file. ";
      $command .= " ".$upload_path.basename($_GET["track"]);
      } else {
      $output["Debug"] .= "Local file not found. Downloading file. ";
      file_put_contents($upload_path.basename($_GET["track"]), file_get_contents($_GET["track"]));
      if (file_exists($upload_path.basename($_GET["track"]))){
        $output["Debug"] .= "Playing freshly downloaded local copy. ";
        $command .= " " . $upload_path.basename($_GET["track"]);
        }else{
        $command .= " " . str_replace(" ","+",$_GET["track"]);
        $output["Debug"] .= "Download not successful. Playing remote version. ";
        }
      }
    } else {
      $command .= " " . str_replace(" ","+",$_GET["track"]);
      $output["Debug"] .= "Len>44: Not downloading file. Playing remote version. ";
    }

  $command = str_replace("https","http",$command);
  $output["trackData"] = basename($_GET["track"]);
  $output["Command"] = $command;
  $output["Output"] = shell_exec($command." 2>&1");

  echo json_encode($output);
} elseif (isset($_GET["refresh"])) {
  $dirContent = array_slice(scandir($upload_path),2);
  foreach($dirContent as $key => $content) {
    if (strlen($content)<44) {
      $alldata[] = $content;
      }
    }
//  $output["fileslist"] = array_slice(scandir($upload_path),2);
  $output["fileslist"] = $alldata;
  echo json_encode($output);
} else {
  echo "<html>\n";
  echo " <head>\n";
  echo "  <title>Php Play</title>\n";
  echo " </head>\n";
  echo "<body>\n";
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
  echo "<hr><form enctype=\"multipart/form-data\" action=\"play.php\" method=\"POST\">";
  echo "Choose a file to upload: <input name=\"uploadedfile\" type=\"file\"><br><br>";
  echo "New Name: <input type=\"text\" name=\"NewName\"> (optional)<br><br>";
  echo "<input type=\"submit\" value=\"Upload File\">";
  echo "</form><hr>";
  echo "Listing of ".$upload_path."<br>\n";
  $fileslist = array_slice(scandir($upload_path),2);
  foreach ($fileslist as &$filename) {
    echo "<a href=\"play.php?track=".$filename."\">".$filename."</a><br>\n";
    }
  //phpinfo();
  echo "</body>\n";
  echo "</html>\n";
}
?>

