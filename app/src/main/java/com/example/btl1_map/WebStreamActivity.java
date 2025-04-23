package com.example.btl1_map;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class WebStreamActivity extends AppCompatActivity {
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); // optional
        webView.loadUrl("https://onlinealarmkur.com/vi/");

        ImageView ivBottomLeft = this.findViewById(R.id.ivBottomLeft);
        String imageUrl = "https://i.imgur.com/CaB5uuZ.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl)
                .into(ivBottomLeft);

        ivBottomLeft.setOnClickListener(v -> {
            Intent intent = new Intent(WebStreamActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });


    }
}
