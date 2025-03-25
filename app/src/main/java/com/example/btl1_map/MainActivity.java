package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupClickListener(R.id.ivFindSurface, CameraActivity.class);
        setupClickListener(R.id.btnFindSurface, CameraActivity.class);

        setupClickListener(R.id.ivFindImage, PositionActivity.class);
        setupClickListener(R.id.btnFindImage, PositionActivity.class);

        setupClickListener(R.id.ivTopRight, SignActivity.class);
    }

    private void setupClickListener(int viewId, Class<?> activityClass) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, activityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
    }
}
