package com.example.mobile_project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
public class DrawActivity extends AppCompatActivity {

    MyCanvas myCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myCanvas = new MyCanvas(this,null);
        setContentView(myCanvas);
    }


}
