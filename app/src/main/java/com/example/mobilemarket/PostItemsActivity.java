package com.example.mobilemarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;

public class PostItemsActivity extends AppCompatActivity {
    private static final String TAG = "PostItemsActivity";
    private static final String POST_ITEM_URL = "https://lamp.ms.wits.ac.za/home/s2669198/post_item.php";
    private EditText itemNameEditText, itemDescriptionEditText, itemPriceEditText;
    private Button postItemButton;
    private SharedPreferences sharedPreferences;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_items);

        // Initialize views
        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemDescriptionEditText = findViewById(R.id.itemDescriptionEditText);
        itemPriceEditText = findViewById(R.id.itemPriceEditText);
        postItemButton = findViewById(R.id.postItemButton);

        // Initialize SharedPreferences and RequestQueue
        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(this);

        // Set click listener for the post button
        postItemButton.setOnClickListener(v -> postItem());

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_post);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_browse) {
                startActivity(new Intent(this, BrowseItemsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_post) {
                return true;
            }
            return false;
        });
    }

    private void postItem() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemDescription = itemDescriptionEditText.getText().toString().trim();
        String itemPrice = itemPriceEditText.getText().toString().trim();

        // Basic validation
        if (itemName.isEmpty() || itemDescription.isEmpty() || itemPrice.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON object for the request
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", itemName);
            jsonObject.put("description", itemDescription);
            jsonObject.put("price", itemPrice);
            int userId = sharedPreferences.getInt("user_id", 0);
            if (userId == 0 ) {
                Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "User ID is empty");
                return;
            }
            jsonObject.put("user_id", userId);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "JSON creation error: " + e.getMessage());
            return;
        }

        // Create Volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, POST_ITEM_URL, jsonObject,
                response -> {
                    try {
                        Log.d(TAG, "Response: " + response.toString());
                        String message = response.getString("message");
                        boolean success = response.getBoolean("success");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        if (success) {
                            startActivity(new Intent(this, BrowseItemsActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON parse error: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Volley error: " + errorMessage, error);
                });

        // Add request to queue
        requestQueue.add(jsonObjectRequest);
    }
}