<?php
header('Content-Type: application/json');

$conn = new mysqli('localhost', 's2669198', 's2669198', 'd2669198');
if ($conn->connect_error) {
    die(json_encode(['success' => false, 'message' => 'Database connection failed']));
}

$data = json_decode(file_get_contents('php://input'), true);
$username = $data['username'];
$email = $data['email'];
$password = $data['password'];

if (empty($username) || empty($email) || empty($password)) {
    echo json_encode(['success' => false, 'message' => 'Missing fields']);
    exit;
}

$password_hash = password_hash($password, PASSWORD_BCRYPT);

$stmt = $conn->prepare("INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $username, $password_hash, $email);

if ($stmt->execute()) {
    echo json_encode(['success' => true, 'username' => $username]);
} else {
    echo json_encode(['success' => false, 'message' => 'Username or email already exists']);
}

$stmt->close();
$conn->close();
?>