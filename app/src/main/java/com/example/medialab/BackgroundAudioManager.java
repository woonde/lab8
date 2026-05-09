package com.example.medialab;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

public class BackgroundAudioManager {

    private static BackgroundAudioManager instance;
    private MediaPlayer mediaPlayer;
    private final Handler resumeHandler = new Handler(Looper.getMainLooper());
    private boolean wasPlayingBeforePause = false;

    private BackgroundAudioManager() { }

    public static synchronized BackgroundAudioManager getInstance() {
        if (instance == null) {
            instance = new BackgroundAudioManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(
                    context.getApplicationContext(), R.raw.audio_sample);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.seekTo(0);
                    mp.start();
                });
            }
        }
    }

    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pauseForVideo() {
        resumeHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            wasPlayingBeforePause = true;
            mediaPlayer.pause();
        }
    }

    public void resumeAfterDelay() {
        resumeHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null && wasPlayingBeforePause) {
            resumeHandler.postDelayed(() -> {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                wasPlayingBeforePause = false;
            }, 1500);
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public void seekTo(int msec) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(msec);
        }
    }

    public void release() {
        resumeHandler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}