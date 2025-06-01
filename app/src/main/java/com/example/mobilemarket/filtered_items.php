<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

// Check if the name parameter is set
if (!isset($_GET['name'])) {
    echo json_encode([]);
    exit;
}

$name = $_GET['name'];

// Database connection
$conn = new mysqli("localhost", "s2669198", "s2669198", "d2669198");

if ($conn->connect_error) {
    echo json_encode(["error" => "Connection failed"]);
    exit;
}

// Prepare and run the query
$stmt = $conn->prepare("SELECT name, description, price FROM items WHERE name LIKE CONCAT('%', ?, '%') ORDER BY date_po>$stmt->bind_param("s", $name);
$stmt->execute();
$result = $stmt->get_result();

$items = [];
while ($row = $result->fetch_assoc()) {
    $items[] = $row;
}

echo json_encode($items);

$stmt->close();
$conn->close();
