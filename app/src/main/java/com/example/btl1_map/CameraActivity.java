package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


public class CameraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera); // Đảm bảo activity_camera.xml tồn tại

        View bottomLeftButton = findViewById(R.id.ivBottomLeft);
        if (bottomLeftButton != null) {
            bottomLeftButton.setOnClickListener(v -> {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}

