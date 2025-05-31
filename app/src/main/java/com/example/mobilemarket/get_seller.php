<?php
header('Content-Type: application/json');

$username = "s2669198";
$password = "s2669198";
$database = "d2669198";

$link = mysqli_connect("127.0.0.1", $username, $password, $database);

if (!$link) {
    die(json_encode(["error" => "Connection failed"]));
}

if (!isset($_GET['item_id'])) {
    die(json_encode(["error" => "Missing item_id"]));
}

$item_id = $_GET['item_id'];

$query = "
    SELECT u.username
    FROM items i
    JOIN users u ON i.user_id = u.user_id
    WHERE i.item_id = ?
";

$stmt = $link->prepare($query);
$stmt->bind_param("i", $item_id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["username" => $row['username']]);
} else {
    echo json_encode(["username" => "Unknown"]);
}

$stmt->close();
$link->close();
?>
