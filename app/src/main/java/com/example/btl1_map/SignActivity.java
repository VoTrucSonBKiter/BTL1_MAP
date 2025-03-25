package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl1_map.api.ApiClient;
import com.example.btl1_map.api.ApiService;
import com.example.btl1_map.api.LoginRequest;
import com.example.btl1_map.api.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        TextView btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pid = etPassword.getText().toString().trim();

            if (email.isEmpty() || pid.isEmpty()) {
                Toast.makeText(SignActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create login request
            LoginRequest loginRequest = new LoginRequest(email, pid);

            // Get API service
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            // Make login request
            Call<LoginResponse> call = apiService.login(loginRequest);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Login successful
                        Toast.makeText(SignActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity
                        Intent intent = new Intent(SignActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close SignActivity to prevent going back
                    } else {
                        // Login failed
                        Toast.makeText(SignActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                    // Network error
                    Toast.makeText(SignActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}