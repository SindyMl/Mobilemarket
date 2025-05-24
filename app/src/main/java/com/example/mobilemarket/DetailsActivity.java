package com.example.mobilemarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailsActivity extends AppCompatActivity {

    TextView nameTextView, descriptionTextView, priceTextView, sellerTextView, ratingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        nameTextView = findViewById(R.id.textView);
        descriptionTextView = findViewById(R.id.textView2);
        priceTextView = findViewById(R.id.textView3);
        sellerTextView = findViewById(R.id.textView4);
         ratingTextView=findViewById(R.id.AverageratingBar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String description = intent.getStringExtra("description");
        String price = intent.getStringExtra("price");
        String seller = intent.getStringExtra("sellername");
        String rating=intent.getStringExtra("rating_count");
        int itemId = intent.getIntExtra("item_id", -1);

        nameTextView.setText(name);
        descriptionTextView.setText(description);
        priceTextView.setText("R" + price);
        sellerTextView.setText("By: " + seller);
        ratingTextView.setText(rating);


        if (itemId == -1) {
            Toast.makeText(this, "Invalid item selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        Log.d("DETAILS_PREFS", "Loaded user_id: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button rateButton = findViewById(R.id.button);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        rateButton.setOnClickListener(v -> {
            float userRating = ratingBar.getRating();
            rateItem(userId, itemId, userRating);
        });
    }

    private void rateItem(int userId, int itemId, float rating) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(userId))
                .add("item_id", String.valueOf(itemId))
                .add("rating", String.valueOf(rating))
                .build();

        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2669198/rate_item.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(DetailsActivity.this, "Failed to rate item", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("SERVER_RESPONSE", responseBody);
     Log.d("API_RESPONSE",responseBody);
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String status = jsonObject.optString("status", "failed");

                        if (status.equals("success")) {
                            final String newAverage = jsonObject.optString("new_rating", "0");
                             final String count=jsonObject.optString("rating_count","0");
                            runOnUiThread(() -> {
                                ratingTextView.setText("Ratings: " + newAverage+"                             Number of ratings:   "+count);
                                Toast.makeText(DetailsActivity.this, "Rating submitted!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(DetailsActivity.this, "Rating failed", Toast.LENGTH_SHORT).show()
                            );
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(DetailsActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(DetailsActivity.this, "Server error", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void fetchSellerUsername(int itemId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://lamp.ms.wits.ac.za/home/s2669198/get_seller.php?item_id=" + itemId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SELLER_FETCH", "Failed to fetch seller username", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        final String username = jsonObject.optString("username", "Unknown");

                        runOnUiThread(() -> {
                            sellerTextView.setText("By: " + username);
                        });
                    } catch (JSONException e) {
                        Log.e("SELLER_FETCH", "Error parsing seller response", e);
                    }
                }
            }
        });
    }
}