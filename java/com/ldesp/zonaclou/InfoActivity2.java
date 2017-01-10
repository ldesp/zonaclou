package com.ldesp.zonaclou;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class InfoActivity2 extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.info2);
        Log.i("DisplayTouch", "Info Activity2 starts");
    }
}


