package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView btnFindImage;
    private TextView btnFindSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        btnFindImage = findViewById(R.id.btnFindImage);
        btnFindSurface = findViewById(R.id.btnFindSurface);

        // Set click listeners
        btnFindImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        btnFindSurface.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PositionActivity.class);
            startActivity(intent);
        });
    }
} 