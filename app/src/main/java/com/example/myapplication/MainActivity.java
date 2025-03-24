package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ivFindSurface).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CameraActivity.class)));
        findViewById(R.id.btnFindSurface).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CameraActivity.class)));

        findViewById(R.id.ivFindImage).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PositionActivity.class)));
        findViewById(R.id.btnFindImage).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PositionActivity.class)));

        findViewById(R.id.ivTopRight).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignActivity.class)));

    }
}
