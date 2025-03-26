package com.example.btl1_map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class SignActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        setupClickListener(R.id.btnSignIn);

        // Load ảnh từ URL vào ImageView
        ImageView imgUniversityLogo = findViewById(R.id.imgUniversityLogo);
        String imageUrl = "https://i.imgur.com/33IcSQg.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl)
                .into(imgUniversityLogo);

        // Load ảnh từ URL vào ImageView
        ImageView imgBiotechLogo = findViewById(R.id.imgBiotechLogo);
        String imageUrl2 = "https://i.imgur.com/bLY4pZV.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl2)
                .into(imgBiotechLogo);
    }

    private void setupClickListener(int viewId) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v -> {
                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            });
        }
    }
}
