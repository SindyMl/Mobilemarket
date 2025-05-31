<?php
$username = "s2669198";
$password = "s2669198";
$database = "d2669198";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);
$output=array();
/* Select queries return a resultset */
if ($result = mysqli_query($link, "SELECT NUMBER from CARS")) {
while ($row=$result->fetch_assoc()){
$output[]=$row;
}
}
mysqli_close($link);
echo json_encode($output);
?>
