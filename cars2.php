<?php
$username = "s2669198";
$password = "s2669198";
$database = "d2669198";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);

$brand = $_REQUEST["brand"];
$output=array();
/* Select queries return a resultset */
if ($r = mysqli_query($link, "SELECT * from `CARS` where `BRAND`='$brand';")) {
 while($row=$r->fetch_assoc()){
  $output[]=$row;
  }
}
mysqli_close($link);
echo json_encode($output);
?>
