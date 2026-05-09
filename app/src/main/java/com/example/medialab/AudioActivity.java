package com.example.medialab;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioActivity extends AppCompatActivity {

    private SeekBar audioSeekBar;
    private SeekBar volumeSeekBar;
    private TextView tvAudioStatus;
    private TextView tvCurrentTime;
    private Button btnPlayPause;

    private Timer updateTimer;
    private boolean isUserSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        audioSeekBar = findViewById(R.id.audioSeekBar);
        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        tvAudioStatus = findViewById(R.id.tvAudioStatus);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        btnPlayPause = findViewById(R.id.btnPlayPause);

        BackgroundAudioManager audio = BackgroundAudioManager.getInstance();

        audioSeekBar.setMax(audio.getDuration());
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audio.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                isUserSeeking = false;
            }
        });

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnPlayPause.setOnClickListener(v -> {
            audio.togglePlayPause();
            updateStatus();
        });

        updateStatus();
        startUpdateTimer();
    }

    private void updateStatus() {
        BackgroundAudioManager audio = BackgroundAudioManager.getInstance();
        tvAudioStatus.setText(
                audio.isPlaying() ? "Аудио воспроизводится" : "Аудио на паузе");
    }

    private void startUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    BackgroundAudioManager audio = BackgroundAudioManager.getInstance();
                    int current = audio.getCurrentPosition();
                    int duration = audio.getDuration();
                    if (!isUserSeeking) {
                        audioSeekBar.setProgress(current);
                    }
                    tvCurrentTime.setText(formatTime(current) + " / " + formatTime(duration));
                    updateStatus();
                });
            }
        }, 0, 1000);
    }

    private String formatTime(int millis) {
        int totalSeconds = millis / 1000;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }
}