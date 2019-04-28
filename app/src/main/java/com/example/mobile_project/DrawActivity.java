package com.example.mobile_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;

public class DrawActivity extends AppCompatActivity {

    MyCanvas myCanvas;
    private Bitmap b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(DrawActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DrawActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        setContentView(R.layout.activity_draw);
        Button clearButton = findViewById(R.id.button2);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    myCanvas.setDrawingCacheEnabled(true);
                    b = myCanvas.getDrawingCache();

                    File fileDirectory = new File(Environment.getExternalStorageDirectory() + "project_test");
                    fileDirectory.mkdirs();

                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String file = "test.png";
                    final File newBitmap = new File(fileDirectory, file);
                    try
                    {
                        newBitmap.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(newBitmap);
                        b.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                        System.out.println("saving......................................................"+filePath+file);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(DrawActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }


}
