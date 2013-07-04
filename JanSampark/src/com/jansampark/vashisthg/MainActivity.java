package com.jansampark.vashisthg;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity  {
	public static final String TAG = "Main";

    public static enum ISSUES {
        WATER
    }
    
    private LocationManager locationManager;
//    private  Location thisLocation;
    
    private GoogleMap gMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(null == savedInstanceState) {
            initMap((SupportMapFragment )getSupportFragmentManager().findFragmentById(R.id.map));           
        }     
        
       showLocation();
    }
    
    public void initMap(SupportMapFragment mapFragment) {
        gMap = mapFragment.getMap();
        
        /// REMOVE FOR DEBUG EMULATOR
        gMap.setMyLocationEnabled(false);
        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);

        Log.d(TAG, "Map initialized.");
        
        gMap.setOnMapClickListener(null);        
        
        View mapBlocker = findViewById(R.id.map_blocker);
        mapBlocker.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
    }
    
    private void showLocation() {
    	 String locationProvider = LocationManager.NETWORK_PROVIDER;
         Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
         LatLng lastKnownLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
         
         gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 15));                 
         gMap.addMarker(new MarkerOptions()
         .position(lastKnownLatLng)       
         .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_main_annotation)));
    }
    
    @Override
	protected void onStart() {
		super.onStart();
	}
    
    @Override
    public void onStop() {       
        super.onStop();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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


 
}
