<?php
include 'db_config.php';
$product_id = $_POST['product_id'];
$user_id = $_POST['user_id'];
$rating = $_POST['rating'];


$stmt = $conn->prepare("SELECT * FROM product_ratings WHERE product_id = ? AND user_id = ?");
$stmt->bind_param("ii", $product_id, $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    
    $stmt = $conn->prepare("UPDATE product_ratings SET rating = ? WHERE product_id = ? AND user_id = ?");
    $stmt->bind_param("dii", $rating, $product_id, $user_id);
} else {
    
    $stmt = $conn->prepare("INSERT INTO product_ratings (product_id, user_id, rating) VALUES (?, ?, ?)");
    $stmt->bind_param("iid", $product_id, $user_id, $rating);
}
$stmt->execute();
echo json_encode(["success" => true]);
?>
