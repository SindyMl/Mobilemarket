package com.example.mobilemarket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class BrowseItemsActivity extends AppCompatActivity {
    private static final String TAG = "BrowseItemsActivity";
    private static final String GET_ITEMS_URL = "https://lamp.ms.wits.ac.za/home/s2669198/get_items.php";
    private EditText searchInput;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ArrayList<Item> itemList = new ArrayList<>();
    private Button addItemButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_items);

        // Initialize views
        searchInput = findViewById(R.id.searchInput);
        recyclerView = findViewById(R.id.recyclerView);
        addItemButton = findViewById(R.id.addItemButton);

        // Initialize Volley and RecyclerView
        requestQueue = Volley.newRequestQueue(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        // Set up add item button
        addItemButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Item button clicked, navigating to PostItemsActivity");
            Intent intent = new Intent(this, PostItemsActivity.class);
            startActivity(intent);
            finish();
        });

        // Set up search functionality
        findViewById(R.id.searchButton).setOnClickListener(v -> {
            String keyword = searchInput.getText().toString().trim();
            fetchItems(keyword.isEmpty() ? null : keyword);
        });

        searchInput.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String keyword = searchInput.getText().toString().trim();
                fetchItems(keyword.isEmpty() ? null : keyword);
                return true;
            }
            return false;
        });

        // Set up RecyclerView item click listener
        itemAdapter.setOnItemClickListener(position -> {
            Item selectedItem = itemList.get(position);
            try {
                Intent intent = new Intent(BrowseItemsActivity.this, DetailsActivity.class);
                intent.putExtra("item_id", selectedItem.getId());
                intent.putExtra("name", selectedItem.getName());
                intent.putExtra("description", selectedItem.getDescription());
                intent.putExtra("price", String.valueOf(selectedItem.getPrice()));
                intent.putExtra("rating_count", String.valueOf(selectedItem.getRating())); // Map rating to rating_count
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Error opening item", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Item click error: " + e.getMessage());
            }
        });

        // Fetch all items initially
        fetchItems(null);

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Log.d(TAG, "Selected item: " + item.getTitle() + " (ID: " + itemId + ")");
            if (itemId == R.id.nav_home) {
                Log.d(TAG, "Navigating to MainActivity");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_browse) {
                Log.d(TAG, "Already on Browse, no navigation needed");
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

    private void fetchItems(String keyword) {
        itemList.clear();
        itemAdapter.notifyDataSetChanged(); // Clear RecyclerView

        String url = GET_ITEMS_URL;
        if (keyword != null) {
            try {
                url = GET_ITEMS_URL + "?name=" + URLEncoder.encode(keyword, "UTF-8");
                Log.d(TAG, "Fetching items with URL: " + url);
            } catch (UnsupportedEncodingException e) {
                Toast.makeText(this, "Error encoding search keyword", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Encoding error: " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Fetching all items: " + url);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "Raw response: " + response.toString());
                        boolean success = response.optBoolean("success", false);
                        if (!success) {
                            String message = response.optString("message", "Failed to fetch items");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "API error: " + message);
                            return;
                        }

                        JSONArray items = response.getJSONArray("data");
                        itemList.clear();
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            int itemId = item.optInt("item_id", 0);
                            String name = item.optString("name", "Unknown");
                            String description = item.optString("description", "");
                            double price = item.optDouble("price", 0.0);
                            double rating = item.optDouble("rating", 0.0);
                            String datePosted = item.optString("date_posted", "");
                            itemList.add(new Item(itemId, name, description, price, rating, datePosted));
                        }
                        itemAdapter.notifyDataSetChanged();
                        if (itemList.isEmpty()) {
                            Toast.makeText(this, "No items found", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Loaded " + itemList.size() + " items");
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Parse error: " + e.getMessage(), e);
                    }
                },
                error -> {
                    String errorMessage = "Error fetching items: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
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
}