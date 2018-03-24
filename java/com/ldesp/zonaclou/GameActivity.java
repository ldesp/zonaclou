package com.ldesp.zonaclou;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class GameActivity extends AppCompatActivity  {


    /**
     * The view responsible for drawing the window.
     */
    GameView mView;
    boolean mTrainFlag;
    boolean mAlertFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i("DisplayTouch", "Game Activity starts");

        setContentView(R.layout.sub2);

        mView = (GameView) findViewById(R.id.gamev1);


        mTrainFlag = savedInstanceState != null ? savedInstanceState.getBoolean("training", false) : false;
        mAlertFlag = savedInstanceState != null ? savedInstanceState.getBoolean("alert", false) : false;
        initClickMode(mAlertFlag);
        SharedPreferences settings = getSharedPreferences(MainActivity.PREF_MAP, 0);

        int id1 = settings.getInt(MainActivity.PREF_PARAMS, 0);
        int id2 = settings.getInt(MainActivity.PREF_TILES, 0);
        int nsites = settings.getInt(MainActivity.PREF_NSITES, 100);
        mView.configGame(savedInstanceState, this, mTrainFlag, mAlertFlag, id1, id2, nsites);
        mView.requestFocus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_train).setChecked(mTrainFlag);
        // menu.findItem(R.id.menu_alert).setChecked(mAlertFlag);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_train:
                mTrainFlag = !mTrainFlag;
                mView.setTraining(mTrainFlag);
                return true;
            case R.id.menu_info2:
                displayInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // nothing to do 

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mView.saveConfigGame(outState);
        outState.putBoolean("training", mTrainFlag);
        outState.putBoolean("alert", mAlertFlag);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mTrainFlag = savedInstanceState != null ? savedInstanceState.getBoolean("training", false) : false;
        mAlertFlag = savedInstanceState != null ? savedInstanceState.getBoolean("alert", false) : false;
        initClickMode(mAlertFlag);
        SharedPreferences settings = getSharedPreferences(MainActivity.PREF_MAP, 0);

        int id1 = settings.getInt(MainActivity.PREF_PARAMS, 0);
        int id2 = settings.getInt(MainActivity.PREF_TILES, 0);
        int nsites = settings.getInt(MainActivity.PREF_NSITES, 100);
        mView.configGame(savedInstanceState, this, mTrainFlag, mAlertFlag, id1, id2, nsites);
        mView.requestFocus();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // nothing to do
    }

    @Override
    public void onBackPressed() {
        if (mView.isGameNotRunning()) {
            super.onBackPressed();
        }
    }

    private void initClickMode(boolean alert) {
        findViewById(R.id.replaybutton).setVisibility(View.INVISIBLE);
        if (alert) {
            setClickMode(findViewById(R.id.fallbutton));
        } else {
            setClickMode(findViewById(R.id.coverbutton));
        }
        findViewById(R.id.coverbutton).setVisibility(View.VISIBLE);
        findViewById(R.id.fallbutton).setVisibility(View.VISIBLE);
    }

    public void displayInfo() {
        Intent intent = new Intent(this, InfoActivity2.class);
        startActivity(intent);
    }

    public void setClickMode(View v) {

        ImageButton releaseBtn;
        boolean alertMode = (v.getId() == R.id.fallbutton);

        if (alertMode) {
            releaseBtn = (ImageButton) findViewById(R.id.coverbutton);
        } else {
            releaseBtn = (ImageButton) findViewById(R.id.fallbutton);
        }
        releaseBtn.setBackgroundColor(0x00000000);
        v.setBackgroundResource(R.drawable.my_shape2);
        mView.setAlert(alertMode);
    }

    public void refreshGame(View v) {
        initClickMode(false);
        mView.restart(mTrainFlag, mAlertFlag);
    }

}


