package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PositionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position); // Đảm bảo activity_position.xml tồn tại

        // Load ảnh từ URL vào ImageView
        ImageView ivTop1 = findViewById(R.id.ivTop1);
        String imageUrl = "https://i.imgur.com/GIhjn00.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl)
                .into(ivTop1);

        // Load ảnh từ URL vào ImageView
        ImageView ivTop2 = findViewById(R.id.ivTop2);
        String imageUrl2 = "https://i.imgur.com/XSvShr4.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl2)
                .into(ivTop2);

        // Load ảnh từ URL vào ImageView
        ImageView ivTop3 = findViewById(R.id.ivTop3);
        String imageUrl3 = "https://i.imgur.com/fMgTuhm.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl3)
                .into(ivTop3);

        // Load ảnh từ URL vào ImageView
        ImageView ivTop4 = findViewById(R.id.ivTop4);
        String imageUrl4 = "https://i.imgur.com/Dfr0qOp.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl4)
                .into(ivTop4);

        // Load ảnh từ URL vào ImageView
        ImageView bottomLeftButton = findViewById(R.id.ivBottomLeft);
        String imageUrl5 = "https://i.imgur.com/CaB5uuZ.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl5)
                .into(bottomLeftButton);

        bottomLeftButton.setOnClickListener(v -> {
            Intent intent = new Intent(PositionActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
