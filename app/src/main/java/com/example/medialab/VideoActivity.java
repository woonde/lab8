package com.example.medialab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar volumeSeekBar;
    private TextView tvVolume;
    private AudioManager audioManager;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        tvVolume = findViewById(R.id.tvVolume);
        Button btnPlayVideo = findViewById(R.id.btnPlayVideo);
        Button btnPauseVideo = findViewById(R.id.btnPauseVideo);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);
        tvVolume.setText("Громкость: " + currentVolume + " / " + maxVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                tvVolume.setText("Громкость: " + progress + " / "
                        + audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.video_sample;
        videoView.setVideoURI(Uri.parse(videoPath));

        btnPlayVideo.setOnClickListener(v -> {
            BackgroundAudioManager.getInstance().pauseForVideo();
            videoView.start();
        });

        btnPauseVideo.setOnClickListener(v -> {
            videoView.pause();
            BackgroundAudioManager.getInstance().resumeAfterDelay();
        });

        videoView.setOnCompletionListener(mp ->
                BackgroundAudioManager.getInstance().resumeAfterDelay());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        BackgroundAudioManager.getInstance().resumeAfterDelay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}