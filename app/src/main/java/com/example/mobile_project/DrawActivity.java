package com.example.mobile_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class DrawActivity extends AppCompatActivity {

    MyCanvas myCanvas;
    static ImageView iv;
    private Bitmap b;
    static String filename;
    String endPointID;
    public static String ENDPOINTID = "com.example.mobile_project_ENDPOINTID";

    private static boolean payloadRecieved;
    private static boolean sentPayload;
    private static File guessImage;
    private static String guessWord;

    static EditText guess;
    static Button guessBtn;
    static TextView waitText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        endPointID = intent.getStringExtra(ENDPOINTID);

        payloadRecieved = sentPayload = false;

        setContentView(R.layout.activity_word);

        String[] words = this.getResources().getStringArray(R.array.words);
        final String drawWord = words[new Random().nextInt(words.length)];

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
                final TextView textWait = findViewById(R.id.drawCount);

                new CountDownTimer(45000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        textWait.setText(String.valueOf(millisUntilFinished / 1000));
                        System.out.println(textWait.getText().toString());
                    }

                    public void onFinish() {
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

                                    filename = file.getAbsolutePath() + "/" + drawWord + ".png";

                                    System.out.println("saving......................................................"+ filename);

                                    newBitmap = new File(filename);
                                }
                                FileOutputStream ostream = new FileOutputStream(newBitmap);
                                b.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                                ostream.close();
                                try {
                                    Payload filePayLoad = Payload.fromFile(newBitmap);

                                    String payloadWord = filePayLoad.getId() + ":" + drawWord;

                                    Payload wordPayLoad = Payload.fromBytes(payloadWord.getBytes(StandardCharsets.UTF_8));
                                    Nearby.getConnectionsClient(DrawActivity.this).sendPayload(endPointID, wordPayLoad);
                                    Nearby.getConnectionsClient(DrawActivity.this).sendPayload(endPointID, filePayLoad);
                                    sentPayload = true;
                                    changeView();
                                }catch (Exception e) {
                                    Log.e("Payload","Not able to send payload", e);
                                }

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

                }.start();

            }
        }.start();

    }

    public static void getImage(File image, String word) {
        guessImage = image;
        guessWord = word;
    }

    public static void payloadIsReceived() {
        payloadRecieved = true;
    }

    void changeView() {

        setContentView(R.layout.activity_guess);
        iv = findViewById(R.id.imageView);
        guess = findViewById(R.id.guess);
        guessBtn = findViewById(R.id.guessBtn);
        waitText = findViewById(R.id.waitText);

        guess.setVisibility(View.INVISIBLE);
        guessBtn.setVisibility(View.INVISIBLE);

        startGuess();
    }

    public static void startGuess() {
        if (payloadRecieved && sentPayload) {

            String filePath = guessImage.getPath();
            Bitmap bitm = BitmapFactory.decodeFile(filePath);
            iv.setImageBitmap(bitm);
            guess.setVisibility(View.VISIBLE);
            guessBtn.setVisibility(View.VISIBLE);
            waitText.setVisibility(View.INVISIBLE);

            guessBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv.setVisibility(View.INVISIBLE);
                    waitText.setVisibility(View.VISIBLE);
                    guess.setEnabled(false);
                    guessBtn.setEnabled(false);

                    waitText.setText("The image was a depiction of: " + guessWord
                                    + "\nYou guessed: " + guess.getText().toString());
                }
            });
        }
    }

}
