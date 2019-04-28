package com.example.mobile_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button drawButton = findViewById(R.id.button);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final Intent draw = new Intent(MainActivity.this, DrawActivity.class);
                startActivity(draw);
            }
        });
        Button wordButton = findViewById(R.id.button5);
        wordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final Intent word = new Intent(MainActivity.this, WordActivity.class);
                startActivity(word);
            }
        });
    }

}
