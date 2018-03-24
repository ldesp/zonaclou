package com.ldesp.zonaclou;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String PREF_MAP = "com.ldesp.zonaclou.MAP";
    public final static String PREF_PARAMS = "com.ldesp.zonaclou.PARAMS";
    public final static String PREF_TILES = "com.ldesp.zonaclou.TILES";
    public final static String PREF_NSITES = "com.ldesp.zonaclou.NSITES";


    private String mPattern;
    private int mDiv;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info1:
                displayInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void octa_easy(View view) {
        mDiv = 4;
        mPattern = "octa";
        displayTouch(view);
    }

    public void octa_medium(View view) {
        mDiv = 2;
        mPattern = "octa";
        displayTouch(view);
    }

    public void octa_expert(View view) {
        mDiv = 1;
        mPattern = "octa";
        displayTouch(view);
    }

    public void hexa_easy(View view) {
        mDiv = 4;
        mPattern = "hexa";
        displayTouch(view);
    }

    public void hexa_medium(View view) {
        mDiv = 2;
        mPattern = "hexa";
        displayTouch(view);
    }

    public void hexa_expert(View view) {
        mDiv = 1;
        mPattern = "hexa";
        displayTouch(view);
    }

    public void penta_easy(View view) {
        mDiv = 4;
        mPattern = "penta";
        displayTouch(view);
    }

    public void penta_medium(View view) {
        mDiv = 2;
        mPattern = "penta";
        displayTouch(view);
    }

    public void penta_expert(View view) {
        mDiv = 1;
        mPattern = "penta";
        displayTouch(view);
    }

    public void square_easy(View view) {
        mDiv = 4;
        mPattern = "square";
        displayTouch(view);
    }

    public void square_medium(View view) {
        mDiv = 3;
        mPattern = "square";
        displayTouch(view);
    }

    public void square_expert(View view) {
        mDiv = 1;
        mPattern = "square";
        displayTouch(view);
    }

    public void displayTouch(View view) {
        int nsites = 36;
        Display display = getWindowManager().getDefaultDisplay();
        Rect rct = new Rect();
        display.getRectSize(rct);
        Log.i("DisplayTouch", "display size   w=" + rct.right + "  h=" + rct.bottom);
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        Log.i("DisplayTouch", "display metrics   xdpi=" + metrics.xdpi + "  ydpi=" + metrics.ydpi);
        int w = (rct.right < rct.bottom) ? rct.right : rct.bottom;
        int maxcol = (int) (((1.0f * w ) / metrics.densityDpi)/0.17f);
        if (maxcol > 25)
            maxcol = 25;
        if (maxcol <= 11) {
            if (mDiv == 1)
                nsites = maxcol*maxcol;
            if (mDiv == 2)
                nsites = (maxcol-2)*(maxcol-2);
            if (mDiv == 3)
                nsites = (maxcol-2)*(maxcol-2);
            if (mDiv == 4)
                nsites = (maxcol-3)*(maxcol-3);
        } else if (maxcol <= 15) {
            if (mDiv == 1)
                nsites = maxcol*maxcol;
            if (mDiv == 2)
                nsites = (maxcol-3)*(maxcol-3);
            if (mDiv == 3)
                nsites = (maxcol-3)*(maxcol-3);
            if (mDiv == 4)
                nsites = (maxcol-5)*(maxcol-5);
        } else if (maxcol <= 19) {
            if (mDiv == 1)
                nsites = maxcol*maxcol;
            if (mDiv == 2)
                nsites = (maxcol-4)*(maxcol-4);
            if (mDiv == 3)
                nsites = (maxcol-4)*(maxcol-4);
            if (mDiv == 4)
                nsites = (maxcol-7)*(maxcol-7);
        } else  {
            if (mDiv == 1)
                nsites = maxcol*maxcol;
            if (mDiv == 2)
                nsites = (maxcol-5)*(maxcol-5);
            if (mDiv == 3)
                nsites = (maxcol-5)*(maxcol-5);
            if (mDiv == 4)
                nsites = (maxcol-9)*(maxcol-9);;
        }

        Intent intent = new Intent(this, GameActivity.class);
        int id1 = getResources().getIdentifier("params_" + mPattern, "array", getPackageName());
        int id2 = getResources().getIdentifier("tiles_" + mPattern, "array", getPackageName());
        SharedPreferences settings = getSharedPreferences(PREF_MAP, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PREF_PARAMS, id1);
        editor.putInt(PREF_TILES, id2);
        editor.putInt(PREF_NSITES, nsites);
        editor.commit();
        startActivity(intent);
    }

    public void displayInfo() {
        Intent intent = new Intent(this, InfoActivity1.class);
        startActivity(intent);
    }

}
