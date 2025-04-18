package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load ảnh từ URL vào ImageView
        ImageView ivTopRight = findViewById(R.id.ivTopRight);
        String imageUrl = "https://i.imgur.com/ro3vPsG.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl)
                .into(ivTopRight);

        // Load ảnh từ URL vào ImageView
        ImageView ivFindSurface = findViewById(R.id.ivFindSurface);
        String imageUrl2 = "https://i.imgur.com/9vA99sd.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl2)
                .into(ivFindSurface);

        // Load ảnh từ URL vào ImageView
        ImageView ivFindImage = findViewById(R.id.ivFindImage);
        String imageUrl3 = "https://i.imgur.com/cN5JkKh.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl3)
                .into(ivFindImage);

        setupClickListener(R.id.ivFindSurface, CameraActivity.class);
        setupClickListener(R.id.btnFindSurface, CameraActivity.class);

        setupClickListener(R.id.ivFindImage, ModelActivity.class);
        setupClickListener(R.id.btnFindImage, ModelActivity.class);

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
