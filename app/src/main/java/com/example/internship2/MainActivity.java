package com.example.internship2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int DELAY_DURATION = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        handler.postDelayed(() -> startCompanyInternActivity(), DELAY_DURATION);

        ImageView imageView = findViewById(R.id.flogo);
        Animation zoomInAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in_animation);

        zoomInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        imageView.startAnimation(zoomInAnimation);
    }
    private void startCompanyInternActivity() {
        Intent intent = new Intent(MainActivity.this, CompanyIntern.class);
        startActivity(intent);
        finish();
    }
}