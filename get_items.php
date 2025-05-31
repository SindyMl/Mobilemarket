<?php
header('Content-Type: application/json');

// Database connection
$host = "localhost";
$username = "s2669198"; // Replace with your MySQL username
$password = "s2669198"; // Replace with your MySQL password
$database = "d2669198"; // Replace with your database name

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    echo json_encode([]);
    exit();
}

// Fetch items
$result = $conn->query("SELECT item_id, name, description, price,rating_count FROM items");

$items = [];
while ($row = $result->fetch_assoc()) {
    $items[] = $row;
}

echo json_encode($items);

$result->close();
$conn->close();
?>
