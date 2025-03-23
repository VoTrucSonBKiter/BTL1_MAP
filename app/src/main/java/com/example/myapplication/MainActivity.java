package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnOpenCamera).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CameraActivity.class)));

        findViewById(R.id.btnShowPosition).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PositionActivity.class)));

        findViewById(R.id.btnSignOut).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignActivity.class)));

    }
}
