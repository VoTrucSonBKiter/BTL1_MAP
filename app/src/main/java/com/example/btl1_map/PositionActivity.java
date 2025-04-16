package com.example.btl1_map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

public class PositionActivity extends AppCompatActivity {
    private ArSceneView arSceneView;
    private Node earthNode;
    private float joystickLeftX, joystickLeftY, joystickRightX, joystickRightY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        arSceneView = findViewById(R.id.arSceneView);
        loadEarthModel();
        setupJoysticks();

        ImageView joystickLeftKnob = findViewById(R.id.joystickLeftKnob);
        Glide.with(this).load("https://i.imgur.com/QEHpjz7.png").into(joystickLeftKnob);

        ImageView joystickRightKnob = findViewById(R.id.joystickRightKnob);
        Glide.with(this).load("https://i.imgur.com/QEHpjz7.png").into(joystickRightKnob);

        ImageView ivTop1 = findViewById(R.id.ivTop1);
        Glide.with(this).load("https://i.imgur.com/GIhjn00.png").into(ivTop1);

        ImageView ivTop2 = findViewById(R.id.ivTop2);
        Glide.with(this).load("https://i.imgur.com/XSvShr4.png").into(ivTop2);

        ImageView ivTop3 = findViewById(R.id.ivTop3);
        Glide.with(this).load("https://i.imgur.com/fMgTuhm.png").into(ivTop3);

        ImageView ivTop4 = findViewById(R.id.ivTop4);
        Glide.with(this).load("https://i.imgur.com/Dfr0qOp.png").into(ivTop4);

        ImageView bottomLeftButton = findViewById(R.id.ivBottomLeft);
        Glide.with(this).load("https://i.imgur.com/CaB5uuZ.png").into(bottomLeftButton);

        bottomLeftButton.setOnClickListener(v -> {
            Intent intent = new Intent(PositionActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadEarthModel() {
        // URL trực tiếp của file earth.glb trên Google Drive
        String modelUrl = "https://drive.google.com/uc?export=download&id=1jRVakBQ9zsiMz23470hr2jh7u_jajKTM";

        ModelRenderable.builder()
                .setSource(this, Uri.parse(modelUrl))
                .build()
                .thenAccept(renderable -> {
                    earthNode = new Node();
                    earthNode.setRenderable(renderable);
                    earthNode.setLocalPosition(new Vector3(0f, 0f, -1f));
                    earthNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));
                    arSceneView.getScene().addChild(earthNode);
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    private void setupJoysticks() {
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
            while (true) {
                runOnUiThread(() -> updateEarthRotation());
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
        float distance = (float) Math.sqrt(x * x + y * y);

        if (distance > maxRadius) {
            float ratio = maxRadius / distance;
            x *= ratio;
            y *= ratio;
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
        if (earthNode == null) return;

        float rotationSpeed = 0.05f;
        Quaternion rotation = earthNode.getLocalRotation();
        rotation = Quaternion.multiply(rotation,
                new Quaternion(new Vector3(joystickLeftY * rotationSpeed, -joystickLeftX * rotationSpeed, 0), 1));

        float zoomSpeed = 0.01f;
        Vector3 position = earthNode.getLocalPosition();
        position.z += joystickRightY * zoomSpeed;
        position.z = Math.max(-2f, Math.min(-0.5f, position.z));

        earthNode.setLocalRotation(rotation);
        earthNode.setLocalPosition(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            arSceneView.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        arSceneView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arSceneView.destroy();
    }
}