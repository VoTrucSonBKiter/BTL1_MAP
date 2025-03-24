package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PositionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position); // Đảm bảo activity_position.xml tồn tại
        findViewById(R.id.ivBottomLeft).setOnClickListener(v -> startActivity(new Intent(PositionActivity.this, MainActivity.class)));

    }
}
