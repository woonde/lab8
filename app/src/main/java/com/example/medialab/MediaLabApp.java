package com.example.medialab;

import android.app.Application;

public class MediaLabApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BackgroundAudioManager.getInstance().init(this);
        BackgroundAudioManager.getInstance().start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BackgroundAudioManager.getInstance().release();
    }
}