<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

// Connect to the database
$conn = new mysqli("localhost", "s2669198", "s2669198", "d2669198");

if ($conn->connect_error) {
    echo json_encode(["error" => "Connection failed"]);
    exit;
}

// Query to get all items ordered by date_posted
$sql = "SELECT name, price FROM items ORDER BY date_posted DESC";
$result = $conn->query($sql);

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode($data);
$conn->close();
?>

