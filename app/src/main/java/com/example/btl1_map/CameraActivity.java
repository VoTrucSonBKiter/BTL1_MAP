package com.example.btl1_map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private PreviewView previewView;
    private Button buttonToggleCamera;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private boolean isCameraOn = false;
    private RecyclerView horizontalScrollView;
    private boolean isVisible = false;
    private View midContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera); // Đảm bảo activity_camera.xml tồn tại

        // Load ảnh từ URL vào ImageView
        @SuppressLint("CutPasteId") ImageView ivBottomLeft = this.findViewById(R.id.ivBottomLeft);
        String imageUrl = "https://i.imgur.com/CaB5uuZ.png"; // Thay bằng URL ảnh của bạn

        Glide.with(this)
                .load(imageUrl)
                .into(ivBottomLeft);

        previewView = findViewById(R.id.preview_view);
        View buttonToggleCamera = findViewById(R.id.button_toggle_camera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
            buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_blue))); // Giữ shape, đổi màu button
            isCameraOn = true;
        }

        // Xử lý sự kiện bật/tắt camera
        buttonToggleCamera.setOnClickListener(v -> {
            if (isCameraOn) {
                stopCamera();
                buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))); // Giữ shape, đổi màu button
                isCameraOn = false;
            } else {
                startCamera();
                buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_blue))); // Giữ shape, đổi màu button
                isCameraOn = true;
            }
        });

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
                        .translationY(-170f)
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
    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
                buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_blue))); // Giữ shape, đổi màu button
                isCameraOn = true;
            } else {
                Toast.makeText(this, "Quyền truy cập máy ảnh bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Khởi động camera
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                // Tạo Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (ExecutionException | InterruptedException e) {
                Log.e("CameraActivity", "Lỗi khi khởi động máy ảnh", e);
                Toast.makeText(this, "Lỗi khi khởi động máy ảnh", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Tắt camera
    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            camera = null;
        }
    }

    public Camera getCamera() {
        return camera;
    }
}
