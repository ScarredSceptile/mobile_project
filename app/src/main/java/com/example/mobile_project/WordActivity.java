package com.example.mobile_project;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;

public class WordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        String[] words = this.getResources().getStringArray(R.array.words);
        String drawWord = words[new Random().nextInt(words.length)];

        final TextView wordView = findViewById(R.id.rndWord);
        final TextView timerText = findViewById(R.id.textTimer);

        wordView.setText(drawWord);

        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                final Intent draw = new Intent(WordActivity.this, DrawActivity.class);
                startActivity(draw);
            }
        }.start();
    }
}
