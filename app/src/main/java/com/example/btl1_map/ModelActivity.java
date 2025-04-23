package com.example.btl1_map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import android.util.Log;
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

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class ModelActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private PreviewView previewView;
    protected Button buttonToggleCamera;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private boolean isCameraOn = false;
    private float joystickLeftX, joystickLeftY, joystickRightX, joystickRightY;
    private WebView modelView;
    private FrameLayout modelShow;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        previewView = findViewById(R.id.preview_view);
        View buttonToggleCamera = findViewById(R.id.button_toggle_camera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
            buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.new_blue))); // Giữ shape, đổi màu button
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
                buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.new_blue))); // Giữ shape, đổi màu button
                isCameraOn = true;
            }
        });

        modelView = findViewById(R.id.modelWebView);
        modelShow = findViewById(R.id.modelShowView);
        modelView.setBackgroundColor(Color.TRANSPARENT);

        if (modelView != null) {
            modelView.loadUrl(getString(R.string.model_viewer_location));

            WebSettings webSettings = modelView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
        }
        setupJoysticks();
        setupUI();
    }
    private void setupUI() {
        ImageView joystickLeftKnob = findViewById(R.id.joystickLeftKnob);
        ImageView joystickRightKnob = findViewById(R.id.joystickRightKnob);
        ImageView ivTop1 = findViewById(R.id.ivTop1);
        ImageView ivTop2 = findViewById(R.id.ivTop2);

        ImageView bottomLeftButton = findViewById(R.id.ivBottomLeft);

        Glide.with(this).load("https://i.imgur.com/So6FKTd.png").into(joystickLeftKnob);
        Glide.with(this).load("https://i.imgur.com/So6FKTd.png").into(joystickRightKnob);
        Glide.with(this).load("https://i.imgur.com/GIhjn00.png").into(ivTop1);
        Glide.with(this).load("https://i.imgur.com/XSvShr4.png").into(ivTop2);
        Glide.with(this).load("https://i.imgur.com/CaB5uuZ.png").into(bottomLeftButton);

        TextView tvModelInfo = findViewById(R.id.tvModelInfo);

        ivTop1.setOnClickListener(v -> {
            if (tvModelInfo.getVisibility() == View.VISIBLE) {
                tvModelInfo.setVisibility(View.GONE);
            } else {
                tvModelInfo.setText("Model: Earth\nFile type: glb\nStatus: Active");
                tvModelInfo.setVisibility(View.VISIBLE);
            }
        });

        bottomLeftButton.setOnClickListener(v -> {
            Intent intent = new Intent(ModelActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        ivTop2.setOnClickListener(v -> {
            Intent intent = new Intent(ModelActivity.this, WebStreamActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setupJoysticks() {
        FrameLayout bigJoystickLeft = findViewById(R.id.Big_joystickLeft);
        FrameLayout bigJoystickRight = findViewById(R.id.Big_joystickRight); // if you have this
        // Prevent clipping of knob when it moves outside bounds
        if (bigJoystickLeft != null) {
            bigJoystickLeft.setClipChildren(false);
            bigJoystickLeft.setClipToPadding(false);
        }
        if (bigJoystickRight != null) {
            bigJoystickRight.setClipChildren(false);
            bigJoystickRight.setClipToPadding(false);
        }


        View joystickLeft = findViewById(R.id.joystickLeft);
        ImageView joystickLeftKnob = findViewById(R.id.joystickLeftKnob);
        View joystickRight = findViewById(R.id.joystickRight);
        ImageView joystickRightKnob = findViewById(R.id.joystickRightKnob);

        joystickLeft.setOnTouchListener((v, event) -> {
            handleJoystick(joystickLeft, joystickLeftKnob, event, true);
            return true;
        });

        joystickRight.setOnTouchListener((v, event) -> {
            handleJoystick(joystickRight, joystickRightKnob, event, false);
            return true;
        });

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                runOnUiThread(this::updateEarthRotation);
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void handleJoystick(View joystick, ImageView knob, MotionEvent event, boolean isLeft) {
        float centerX = joystick.getWidth() / 2f;
        float centerY = joystick.getHeight() / 2f;
        float maxRadius = joystick.getWidth() / 2f;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            knob.setTranslationX(0);
            knob.setTranslationY(0);
            if (isLeft) {
                joystickLeftX = 0;
                joystickLeftY = 0;
            } else {
                joystickRightX = 0;
                joystickRightY = 0;
            }
            return;
        }

        float x = event.getX() - centerX;
        float y = event.getY() - centerY;

        // If it's the right joystick, ignore horizontal movement
        if (!isLeft) {
            x = 0; // disable X-axis movement
            y = Math.max(-120, Math.min(y, 120));
        } else {
            float distance = (float) Math.sqrt(x * x + y * y);

            if (distance > maxRadius) {
                float ratio = maxRadius / distance;
                x *= ratio;
                y *= ratio;
            }
        }

        knob.setTranslationX(x);
        knob.setTranslationY(y);

        if (isLeft) {
            joystickLeftX = x / maxRadius;
            joystickLeftY = y / maxRadius;
        } else {
            joystickRightX = x / maxRadius;
            joystickRightY = y / maxRadius;
        }
    }
    private void updateEarthRotation() {
        if (modelView == null) return;

        // Scale the model using the right joystick (Y-axis only)
        float scaleChange = joystickRightY * 2f; // Up: negative → increase size, Down: positive → decrease

        int currentWidth = modelView.getLayoutParams().width;
        int currentHeight = modelView.getLayoutParams().height;

        // Default to MATCH_PARENT if initial values are not set
        if (currentWidth <= 0 || currentHeight <= 0) {
            currentWidth = modelView.getWidth();
            currentHeight = modelView.getHeight();
        }

        int newWidth = (int) (currentWidth - scaleChange); // subtract because up = -1 (increase)
        int newHeight = (int) (currentHeight - scaleChange);

        int screenWidth = modelShow.getWidth();
        int screenHeight = modelShow.getHeight();

        // Limit size range
        int maxSize = Math.min(screenHeight, screenWidth);
        // Clamp size to prevent it from being too small or too big
        newWidth = Math.max(200, Math.min(newWidth, maxSize)); // min = 300px, max = 2000px
        newHeight = Math.max(200, Math.min(newHeight, maxSize));

        modelView.getLayoutParams().width = newWidth;
        modelView.getLayoutParams().height = newHeight;
        modelView.requestLayout(); // Apply new size

        // Move the model using the left joystick
        float dx = joystickLeftX * 10;  // Adjust movement sensitivity
        float dy = joystickLeftY * 10;

        // Get current translation
        float newTranslationX = modelView.getTranslationX() + dx;
        float newTranslationY = modelView.getTranslationY() + dy;

        // Get screen and WebView dimensions
        int modelWidth = modelView.getWidth();
        int modelHeight = modelView.getHeight();

        // Compute max allowed translation (keep the model inside screen)
        float maxTranslationX = (screenWidth - modelWidth) / 2f;
        float minTranslationX = -maxTranslationX;
        float maxTranslationY = (screenHeight - modelHeight) / 2f;
        float minTranslationY = -maxTranslationY;

        // Clamp translation to stay within bounds
        newTranslationX = Math.max(minTranslationX, Math.min(newTranslationX, maxTranslationX));
        newTranslationY = Math.max(minTranslationY, Math.min(newTranslationY, maxTranslationY));

        modelView.setTranslationX(newTranslationX);
        modelView.setTranslationY(newTranslationY);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
                buttonToggleCamera.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.new_blue))); // Giữ shape, đổi màu button
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