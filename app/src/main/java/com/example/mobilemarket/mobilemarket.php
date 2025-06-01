<?php
$username = "s2669198";
$password = "s2669198";
$database = "d2669198";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);


if (!$link) {
    die(json_encode(['success' => false, 'message' => 'Connection failed: ' . mysqli_connect_error()]));
}

header("Content-Type: application/json");
$output = ['success' => false, 'message' => ''];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);


    if (!isset($data['seller_id'], $data['name'], $data['price']) ||
        empty($data['name']) || !is_numeric($data['price'])) {
        $output['message'] = 'Invalid input data';
    } else {

        $stmt = $link->prepare("INSERT INTO items (seller_id, name, description, price) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("issd",
            $data['seller_id'],
            $data['name'],
            $data['description'] ?? '',
            $data['price']
        );
                if ($stmt->execute()) {                                                     $output['success'] = true;
                    $output['message'] = 'Item posted successfully';
                    $output['item_id'] = $link->insert_id;
                } else {
                    $output['message'] = 'Error: ' . $stmt->error;
                }
                $stmt->close();
            }
 } else {
            $output['message'] = 'Only POST requests accepted';
        }

mysqli_close($link);
echo json_encode($output);
?>