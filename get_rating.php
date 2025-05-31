
<?php
include 'db_config.php';

$product_id = $_GET['product_id'];

$stmt = $conn->prepare("SELECT AVG(rating) as avg_rating, COUNT(*) as rating_count FROM product_ratings WHERE product_id = ?");
$stmt->bind_param("i", $product_id);
$stmt->execute();
$result = $stmt->get_result();
$data = $result->fetch_assoc();

echo json_encode($data);
?>
