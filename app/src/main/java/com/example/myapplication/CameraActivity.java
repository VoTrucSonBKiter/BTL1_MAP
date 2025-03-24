package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera); // Đảm bảo activity_camera.xml tồn tại
        findViewById(R.id.ivBottomLeft).setOnClickListener(v -> startActivity(new Intent(CameraActivity.this, MainActivity.class)));

    }
}
