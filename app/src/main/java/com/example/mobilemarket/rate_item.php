<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
error_reporting(E_ALL);

// 1. Database connection
$host = "localhost";
$username = "s2669198";
$password = "s2669198";
$database = "d2669198";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    echo json_encode(["status" => "failed", "message" => "DB connection error"]);
    exit();
}

// 2. Handle POST request
if ($_SERVER["REQUEST_METHOD"] === "POST") {
    if (
        isset($_POST['user_id']) &&
        isset($_POST['item_id']) &&
        isset($_POST['rating'])
    ) {
        $userId = intval($_POST['user_id']);
        $itemId = intval($_POST['item_id']);
        $rating = floatval($_POST['rating']);

        // 3. Insert or update rating
        $stmt = $conn->prepare("INSERT INTO ratings (user_id, item_id, rating) VALUES (?, ?, ?)
                                ON DUPLICATE KEY UPDATE rating = VALUES(rating)");

        if ($stmt) {
            $stmt->bind_param("iii", $userId, $itemId, $rating);

            if ($stmt->execute()) {
                // 4. Update average rating and rating count
                $avgSql = "
                    UPDATE items
                    SET rating = (
                        SELECT ROUND(AVG(rating), 1) FROM ratings WHERE item_id = ?
                    ),
                    rating_count = (
                        SELECT COUNT(*) FROM ratings WHERE item_id = ?
                    )
                    WHERE item_id = ?;
                ";

                $avgStmt = $conn->prepare($avgSql);
                if ($avgStmt) {
                    $avgStmt->bind_param("iii", $itemId, $itemId, $itemId);
                    $avgStmt->execute();
                    $avgStmt->close();
                }

                // 5. Fetch updated values
                $select_sql = "SELECT rating, rating_count FROM items WHERE item_id = ?;";
                $select_stmt = $conn->prepare($select_sql);
                if ($select_stmt) {
                    $select_stmt->bind_param("i", $itemId);
                    $select_stmt->execute();
                    $result = $select_stmt->get_result();

                    if ($result && $result->num_rows > 0) {
                        $row = $result->fetch_assoc();
                        echo json_encode([
                            "status" => "success",
                            "new_rating" => $row["rating"],
                            "rating_count" => $row["rating_count"]
                        ]);
                    } else {
                        echo json_encode([
                            "status" => "failed",
                            "message" => "No item found for given ID"
                        ]);
                    }

                    $select_stmt->close();
                } else {
                    echo json_encode(["status" => "failed", "message" => "Select prepare failed"]);
                }

            } else {
                echo json_encode(["status" => "failed", "error" => $stmt->error]);
            }

            $stmt->close();
        } else {
            echo json_encode(["status" => "failed", "message" => "Insert prepare failed"]);
        }

    } else {
        echo json_encode(["status" => "failed", "message" => "Missing POST parameters"]);
    }
}

$conn->close();
?>
