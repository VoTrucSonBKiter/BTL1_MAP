package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class PositionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position); // Đảm bảo activity_position.xml tồn tại

        View bottomLeftButton = findViewById(R.id.ivBottomLeft);
        if (bottomLeftButton != null) {
            bottomLeftButton.setOnClickListener(v -> {
                Intent intent = new Intent(PositionActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}
