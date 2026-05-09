package com.example.medialab;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView tvImageIndex;
    private Button btnSlideshow;

    private final int[] images = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4
    };

    private int currentIndex = 0;
    private Timer slideshowTimer;
    private boolean isSlideshowRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        tvImageIndex = findViewById(R.id.tvImageIndex);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        btnSlideshow = findViewById(R.id.btnSlideshow);
        Button btnVideo = findViewById(R.id.btnVideo);
        Button btnAudio = findViewById(R.id.btnAudio);

        showImage(currentIndex);

        btnPrev.setOnClickListener(v -> showPreviousImage());
        btnNext.setOnClickListener(v -> showNextImage());
        btnSlideshow.setOnClickListener(v -> toggleSlideshow());

        btnVideo.setOnClickListener(v ->
                startActivity(new Intent(this, VideoActivity.class)));
        btnAudio.setOnClickListener(v ->
                startActivity(new Intent(this, AudioActivity.class)));
    }

    @SuppressLint("SetTextI18n")
    private void showImage(int index) {
        if (index >= 0 && index < images.length) {
            imageView.setImageResource(images[index]);
            currentIndex = index;
            tvImageIndex.setText((index + 1) + " / " + images.length);
        }
    }

    private void showNextImage() {
        currentIndex = (currentIndex + 1) % images.length;
        showImage(currentIndex);
    }

    private void showPreviousImage() {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        showImage(currentIndex);
    }

    private void toggleSlideshow() {
        if (isSlideshowRunning) {
            stopSlideshow();
        } else {
            startSlideshow();
        }
    }

    private void startSlideshow() {
        slideshowTimer = new Timer();
        slideshowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> showNextImage());
            }
        }, 3000, 3000);
        isSlideshowRunning = true;
        btnSlideshow.setText("Остановить слайд-шоу");
    }

    private void stopSlideshow() {
        if (slideshowTimer != null) {
            slideshowTimer.cancel();
            slideshowTimer = null;
        }
        isSlideshowRunning = false;
        btnSlideshow.setText("Запустить слайд-шоу");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (slideshowTimer != null) {
            slideshowTimer.cancel();
        }
    }
}