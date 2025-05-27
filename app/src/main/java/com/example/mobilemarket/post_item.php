<?php
header('Content-Type: application/json');

// Database connection
$host = "localhost";
$username = "s2669198"; // Replace with your MySQL username
$password = "s2669198"; // Replace with your MySQL password
$database = "d2669198"; // Replace with your database name

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed: " . $conn->connect_error]);
    exit();
}

// Get JSON input
$data = json_decode(file_get_contents("php://input"), true);

if (!$data) {
    echo json_encode(["success" => false, "message" => "Invalid or missing JSON data"]);
    exit();
}

// Validate input
if (!isset($data['name']) || !isset($data['description']) || !isset($data['price']) || !isset($data['user_id'])) {
    echo json_encode(["success" => false, "message" => "Missing required fields"]);
    exit();
}

$name = $data['name'];
$description = $data['description'];
$price = $data['price'];
$user_id = $data['user_id'];

// Sanitize inputs
$name = $conn->real_escape_string($name);
$description = $conn->real_escape_string($description);
$price = floatval($price);
$user_id = intval($user_id);

if (empty($name) || empty($description) || $price <= 0 || $user_id <= 0) {
    echo json_encode(["success" => false, "message" => "Invalid input data"]);
    exit();
}

// Insert item
$stmt = $conn->prepare("INSERT INTO items (name, description, price, user_id) VALUES (?, ?, ?, ?)");
$stmt->bind_param("ssdi", $name, $description, $price, $user_id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Item posted successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to post item: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>