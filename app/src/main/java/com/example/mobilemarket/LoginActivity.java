package com.example.mobilemarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREFS_NAME = "auth";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String LOGIN_URL = "https://lamp.ms.wits.ac.za/home/s2669198/login.php";

    private EditText usernameEditText, passwordEditText;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login_button);
        TextView registerText = findViewById(R.id.register_link);

        requestQueue = Volley.newRequestQueue(this);

        // Set up login button
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            performLogin(username, password);
        });

        // Set up register text with clickable span
        String text = "Don't have an account? Register";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan, text.indexOf("Register"), text.indexOf("Register") + "Register".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        registerText.setText(spannableString);
        registerText.setMovementMethod(LinkMovementMethod.getInstance());

        // Ensure the TextView is clickable and focusable
        registerText.setClickable(true);
        registerText.setFocusable(true);
    }

    private void performLogin(String username, String password) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (Exception e) {
            Toast.makeText(this, "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, jsonBody,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            String token = response.optString("token", "");
                            int userId = response.optInt("user_id", 0);
                            String savedUsername = response.optString("username", username);

                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_TOKEN, token);
                            editor.putInt(KEY_USER_ID, userId);
                            editor.putString(KEY_USERNAME, savedUsername);
                            editor.apply();

                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.optString("message", "Login failed");
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show());

        requestQueue.add(jsonObjectRequest);
    }
}