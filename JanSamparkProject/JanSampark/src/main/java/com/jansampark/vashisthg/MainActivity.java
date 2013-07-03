package com.jansampark.vashisthg;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends Activity {

    public static enum ISSUES {
        WATER
    }

    private GoogleMap gMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(null == savedInstanceState) {
            initMap((SupportMapFragment )findFragmentById(R.id.map));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onSewageClick(View view) {

    }

    public void onTransportationClick(View view) {

    }

    public void onWaterClick(View view) {
        openIssueActivity(ISSUES.WATER);
    }

    public void onRoadClick(View view) {

    }

    public void onElectricityClick(View view) {

    }

    public void onLawAndOrderClick(View view) {

    }

    private void openIssueActivity(ISSUES issue) {
        Intent intent = new Intent(this, IssueActivity.class);
        intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
        startActivity(intent);
    }


    public void initMap(SupportMapFragment mapFragment) {
        View mapView = findViewById(R.id.map);

        gMap = mapFragment.getMap();

        MapHelper.setTransparent((ViewGroup)mapView);

        /// REMOVE FOR DEBUG EMULATOR
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);


        /// REMOVE FOR DEBUG EMULATOR
        mapHelper.showStartView(this);







        // enable / disable 3d
        SharedPreferences sharedPrefs = getSharedPreferences(SettingsFragment.SETTINGS_PREFS, Context.MODE_PRIVATE);
        boolean threedOn = sharedPrefs.getBoolean(SettingsFragment.THREED_MODE_ON, SettingsFragment.THREED_ON);
        gMap.getUiSettings().setTiltGesturesEnabled(threedOn);

        Log.d(TAG, "Map initialized.");
    }
}
