<?php<?php

header("Content-Type: application/json");header("Content-Type: application/json");

ini_set('display_errors', 1);

if (!isset($_GET['name'])) {error_reporting(E_ALL);

    echo json_encode([]);

    exit;// Check if the name parameter is set

}if (!isset($_GET['name'])) {

    echo json_encode([]);

$name = $_GET['name'];    exit;

$conn = new mysqli("localhost", "s2669198", "s2669198", "d2669198");}



if ($conn->connect_error) {$name = $_GET['name'];

    echo json_encode(["error" => "Connection failed"]);

    exit;// Database connection

}$conn = new mysqli("localhost", "s2669198", "s2669198", "d2669198");



$sql = "SELECT name, description, price FROM items WHERE name LIKE ? ORDER BY date_posted DESC";if ($conn->connect_error) {

$stmt = $conn->prepare($sql);    echo json_encode(["error" => "Connection failed"]);

$searchTerm = "%" . $name . "%";    exit;

$stmt->bind_param("s", $searchTerm);}

$stmt->execute();

$result = $stmt->get_result();// Prepare and run the query

$stmt = $conn->prepare("SELECT name, description, price FROM items WHERE name LIKE CONCAT('%', ?, '%') ORDER BY date_posted DESC");

$items = [];$stmt->bind_param("s", $name);

while ($row = $result->fetch_assoc()) {$stmt->execute();

    $items[] = $row;$result = $stmt->get_result();

}

$items = [];

echo json_encode($items);while ($row = $result->fetch_assoc()) {

$stmt->close();    $items[] = $row;

$conn->close();}

?>
echo json_encode($items);

$stmt->close();
$conn->close();
?>