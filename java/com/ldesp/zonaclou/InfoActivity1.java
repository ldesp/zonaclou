package com.ldesp.zonaclou;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class InfoActivity1 extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info1);
        Log.i("DisplayTouch", "Info Activity starts");
    }
}


