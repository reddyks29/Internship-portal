package com.example.internship2;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;

public class Congragulations_Company extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congragulations_company);
        VideoView videoView = findViewById(R.id.Videoview);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.confetti);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        videoView.start();
    }
}