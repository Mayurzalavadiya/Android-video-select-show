package com.example.demo;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

public class MainActivity extends Activity {

    private Button buttonSelectVideo;
    private VideoView videoView;
    private MediaController mediaController;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSelectVideo = findViewById(R.id.play_pause_button);
        videoView = findViewById(R.id.video_view);
        seekBar = findViewById(R.id.seek_bar);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        buttonSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    videoView.seekTo(progress);
                    handler.removeCallbacks(updateSeekBar);
                    handler.postDelayed(updateSeekBar, 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateSeekBar);
                handler.postDelayed(updateSeekBar, 100);
            }
        });
    }

    private Runnable updateSeekBar = new Runnable() {
        @Override
        public void run() {
            if(videoView.isPlaying()) {
                currentPosition = videoView.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                handler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
            seekBar.setMax(videoView.getDuration());
            handler.postDelayed(updateSeekBar, 100);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(currentPosition);
        videoView.start();
    }

}