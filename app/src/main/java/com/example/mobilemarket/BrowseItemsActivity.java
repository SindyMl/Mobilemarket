package com.example.mobilemarket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class BrowseItemsActivity extends AppCompatActivity {
    private static final String TAG = "BrowseItemsActivity";
    private static final String GET_ITEMS_URL = "https://lamp.ms.wits.ac.za/home/s2669198/get_items.php";
    private ListView itemsListView;
    private Button addItemButton;
    private RequestQueue requestQueue;
    private ArrayList<String> itemsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_items);

        // Initialize views
        itemsListView = findViewById(R.id.itemsListView);
        addItemButton = findViewById(R.id.addItemButton);

        // Initialize Volley and list
        requestQueue = Volley.newRequestQueue(this);
        itemsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        itemsListView.setAdapter(adapter);

        // Set up add item button
        addItemButton.setOnClickListener(v -> {
            startActivity(new Intent(this, PostItemsActivity.class));
            finish();
        });

        // Fetch items
        fetchItems();

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_browse);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_browse) {
                return true;
            } else if (itemId == R.id.nav_post) {
                startActivity(new Intent(this, PostItemsActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void fetchItems() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_ITEMS_URL, null,
                response -> {
                    try {
                        Log.d(TAG, "Response: " + response.toString());
                        itemsList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            String name = item.getString("name");
                            String description = item.getString("description");
                            String price = item.getString("price");
                            itemsList.add(name + "\n" + description + "\nR " + price);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing items", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "JSON parse error: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Error fetching items: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Volley error: " + errorMessage, error);
                });

        requestQueue.add(jsonArrayRequest);
    }
}