package com.example.btl1_map;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private RecyclerView horizontalScrollView;
    private boolean isVisible = false;
    private View midContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera); // Đảm bảo activity_camera.xml tồn tại

        horizontalScrollView = findViewById(R.id.horizontalScrollView);

        midContainer = findViewById(R.id.midContainer); // View cần đẩy lên

        horizontalScrollView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<String> items = Arrays.asList("Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật", "Tủ nuôi cấy tế bào động vật");
        HorizontalAdapter adapter = new HorizontalAdapter(items);
        horizontalScrollView.setAdapter(adapter);

        TextView btnDoc = findViewById(R.id.btnDoc);
        LinearLayout subContainer = findViewById(R.id.submidContainer);

        btnDoc.setOnClickListener(v -> {
            isVisible = !isVisible;

            if (isVisible) {
                midContainer.animate()
                        .translationY(-180f)
                        .setDuration(300)
                        .withEndAction(() -> {
                            horizontalScrollView.setAlpha(0f);
                            horizontalScrollView.setVisibility(View.VISIBLE);
                            horizontalScrollView.animate().alpha(1f).setDuration(200).start();
                        })
                        .start();
            } else {
                horizontalScrollView.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            horizontalScrollView.setVisibility(View.GONE);
                            midContainer.animate().translationY(0f).setDuration(300).start();
                        })
                        .start();
            }

            int newColor = isVisible ? ContextCompat.getColor(this, R.color.light_blue)
                    : ContextCompat.getColor(this, R.color.default_button_color);
            subContainer.setBackgroundTintList(ColorStateList.valueOf(newColor));
        });

        View bottomLeftButton = findViewById(R.id.ivBottomLeft);
        if (bottomLeftButton != null) {
            bottomLeftButton.setOnClickListener(v -> {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
}
