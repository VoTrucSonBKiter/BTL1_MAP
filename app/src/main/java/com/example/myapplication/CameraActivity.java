package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private PreviewView previewView;
    private Button buttonToggleCamera;
    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private boolean isCameraOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Ánh xạ các thành phần giao diện
        previewView = findViewById(R.id.preview_view);
        buttonToggleCamera = findViewById(R.id.button_toggle_camera);

        // Kiểm tra quyền camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // Nếu đã có quyền, khởi động camera
            startCamera();
            buttonToggleCamera.setText(R.string.turn_off_camera); // Sẽ hiển thị "Tắt máy ảnh"
            isCameraOn = true;
        }

        // Xử lý sự kiện bật/tắt camera
        buttonToggleCamera.setOnClickListener(v -> {
            if (isCameraOn) {
                stopCamera();
                buttonToggleCamera.setText(R.string.turn_on_camera); // Sẽ hiển thị "Bật máy ảnh"
                isCameraOn = false;
            } else {
                startCamera();
                buttonToggleCamera.setText(R.string.turn_off_camera); // Sẽ hiển thị "Tắt máy ảnh"
                isCameraOn = true;
            }
        });
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
                buttonToggleCamera.setText(R.string.turn_off_camera); // Sẽ hiển thị "Tắt máy ảnh"
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

                // Chọn camera sau (back camera)
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Gỡ bỏ các use case trước đó
                cameraProvider.unbindAll();

                // Kết nối camera với Preview
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
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
}