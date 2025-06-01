package com.example.mobilemarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilemarket.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String PREFS_NAME = "auth";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String GET_TOP_ITEMS_URL = "https://lamp.ms.wits.ac.za/home/s2669198/get_items.php?top=3";

    private ActivityMainBinding binding;
    private RequestQueue requestQueue;
    private ArrayList<Item> topItemList = new ArrayList<>();
    private ItemAdapter topItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for token before setting content view
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        Log.d(TAG, "Checking token: " + (token == null ? "null" : token));

        if (token == null) {
            Log.d(TAG, "No token found, redirecting to LoginActivity");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Token exists, proceed with MainActivity using Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Set up welcome text with username
        String username = prefs.getString(KEY_USERNAME, "User");
        binding.welcomeText.setText("Welcome, " + username);

        // Set up RecyclerView for top-rated items
        binding.topRatedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        topItemAdapter = new ItemAdapter(topItemList);
        binding.topRatedRecyclerView.setAdapter(topItemAdapter);

        // Initialize Volley and fetch top items
        requestQueue = Volley.newRequestQueue(this);
        fetchTopRatedItems();

        // Set up logout button
        binding.logoutButton.setOnClickListener(v -> performLogout());

        // Set up bottom navigation
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d(TAG, "Selected item: " + item.getTitle() + " (ID: " + itemId + ")");
            if (itemId == R.id.nav_home) {
                Log.d(TAG, "Already on Home, no navigation needed");
                return true;
            } else if (itemId == R.id.nav_browse) {
                Log.d(TAG, "Navigating to BrowseItemsActivity");
                Intent intent = new Intent(this, BrowseItemsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_post) {
                Log.d(TAG, "Navigating to PostItemsActivity");
                Intent intent = new Intent(this, PostItemsActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else {
                Log.w(TAG, "Unknown navigation item selected: " + itemId);
                return false;
            }
        });
    }

    private void fetchTopRatedItems() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_TOP_ITEMS_URL, null,
                response -> {
                    try {
                        Log.d(TAG, "Raw response: " + response.toString());
                        boolean success = response.optBoolean("success", false);
                        if (!success) {
                            String message = response.optString("message", "Failed to fetch top items");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "API error: " + message);
                            return;
                        }

                        JSONArray items = response.getJSONArray("data");
                        topItemList.clear();
                        for (int i = 0; i < items.length() && i < 3; i++) {
                            JSONObject item = items.getJSONObject(i);
                            int itemId = item.optInt("item_id", 0);
                            String name = item.optString("name", "Unknown");
                            String description = item.optString("description", "");
                            double price = item.optDouble("price", 0.0);
                            double rating = item.optDouble("rating", 0.0);
                            String datePosted = item.optString("date_posted", "");
                            topItemList.add(new Item(itemId, name, description, price, rating, datePosted));
                        }
                        topItemAdapter.notifyDataSetChanged();
                        if (topItemList.isEmpty()) {
                            Toast.makeText(this, "No top-rated items found", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Loaded " + topItemList.size() + " top-rated items");
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Parse error: " + e.getMessage(), e);
                    }
                },
                error -> {
                    String errorMessage = "Error fetching top items: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    if (error.networkResponse != null) {
                        errorMessage += " (HTTP " + error.networkResponse.statusCode + ")";
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            errorMessage += " Response: " + responseBody;
                        } catch (Exception e) {
                            errorMessage += " (Unable to parse error response)";
                        }
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Volley error: " + errorMessage, error);
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void performLogout() {
        // Clear token and user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.apply();

        Log.d(TAG, "Token cleared, redirecting to LoginActivity");

        // Navigate to LoginActivity and clear activity stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}