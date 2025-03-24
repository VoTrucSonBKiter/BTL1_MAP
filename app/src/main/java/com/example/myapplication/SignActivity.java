package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignActivity extends AppCompatActivity {
    private static final String TAG = "SignActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        try {
            TextView btnSignIn = findViewById(R.id.btnSignIn);

            if (btnSignIn != null) {
                btnSignIn.setOnClickListener(v -> {
                    Log.d(TAG, "Sign In button clicked");

                    Intent intent = new Intent(SignActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            } else {
                Log.e(TAG, "btnSignIn not found in activity_sign.xml");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing btnSignIn", e);
        }
    }
}
