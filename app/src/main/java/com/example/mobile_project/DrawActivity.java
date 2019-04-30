package com.example.mobile_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class DrawActivity extends AppCompatActivity {

    MyCanvas myCanvas;
    private Bitmap b;
    Button clearButton;
    Canvas canvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_word);

        String[] words = this.getResources().getStringArray(R.array.words);
        String drawWord = words[new Random().nextInt(words.length)];

        final TextView wordView = findViewById(R.id.rndWord);
        final TextView timerText = findViewById(R.id.textTimer);

        wordView.setText(drawWord);

        myCanvas = new MyCanvas(this, null);

        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                setContentView(R.layout.activity_draw);

                myCanvas = findViewById(R.id.myCanvas);
                clearButton = findViewById(R.id.button2);

                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                try{
                    myCanvas.setDrawingCacheEnabled(true);
                    b = myCanvas.getDrawingCache();
                    File newBitmap = null;


                    try
                    {
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            File file = new File(Environment.getExternalStorageDirectory(), "project_test");
                            if (!file.exists()){
                                file.mkdirs();
                            }

                            System.out.println("saving......................................................"+ file.getAbsolutePath() + "test" + ".png");

                            newBitmap = new File(file.getAbsolutePath() + "/test" + ".png");
                        }
                        FileOutputStream ostream = new FileOutputStream(newBitmap);
                        b.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                        System.out.println("saving......................................................");
                        ostream.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }finally
                    {
                        myCanvas.setDrawingCacheEnabled(false);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }


                    }

                });

            }
        }.start();

    }

}
