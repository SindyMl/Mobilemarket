<?php
$username = "s2669198";
$password = "s2669198";
$database = "d2669198";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);


$id = isset($_GET['id']) ? $_GET['id'] : 1;

$result = mysqli_query($link, "SELECT * FROM products WHERE id = $id");

if ($row = mysqli_fetch_assoc($result)) {
    echo json_encode($row); 
} else {
    echo json_encode(["error" => "Product not found"]);
}

mysqli_close($link);
?>
