package com.example.mobilemarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "auth";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        client = new OkHttpClient();

        // Set login button click listener
        loginButton.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON request body
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);
            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Log.d(TAG, "Request body: " + json.toString());

            // Build HTTP request (replace with your API endpoint)
            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2669198/login.php") // Update with actual endpoint
                    .post(body)
                    .build();

            // Execute request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(
                            LoginActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            String token = jsonResponse.getString("token");
                            int userId = jsonResponse.getInt("user_id");

                            Log.d(TAG, "Response: " + responseBody);
                            Log.d(TAG, "Token: " + token + ", UserID: " + userId);

                            // Store token and user ID in SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_TOKEN, token);
                            editor.putInt(KEY_USER_ID, userId);
                            editor.apply();

                            // Navigate to MainActivity and clear activity stack
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish(); // Close LoginActivity
                        } else {
                            runOnUiThread(() -> Toast.makeText(
                                    LoginActivity.this,
                                    "Invalid credentials",
                                    Toast.LENGTH_SHORT
                            ).show());
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(
                                LoginActivity.this,
                                "Error parsing response",
                                Toast.LENGTH_SHORT
                        ).show());
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }
}