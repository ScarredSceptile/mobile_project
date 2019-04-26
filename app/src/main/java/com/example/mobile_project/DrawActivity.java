package com.example.mobile_project;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Random;

public class DrawActivity extends AppCompatActivity {

    MyCanvas myCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_draw);

        String[] words = this.getResources().getStringArray(R.array.words);
        String drawWord = words[new Random().nextInt(words.length)];

        final TextView wordView = findViewById(R.id.rndWord);
        final TextView timerText = findViewById(R.id.textTimer);
        myCanvas = new MyCanvas(this,null);

        wordView.setText(drawWord);

        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                setContentView(myCanvas);
            }
        }.start();

    }


}
