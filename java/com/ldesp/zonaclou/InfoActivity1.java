package com.ldesp.zonaclou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class InfoActivity1 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info1);
        Log.i("DisplayTouch", "Info Activity starts");
    }
}


