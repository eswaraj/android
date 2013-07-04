package com.jansampark.vashisthg;



import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jansampark.vashisthg.MainActivity.ISSUES;

public class MainIssueFragment extends Fragment{

    private LocationManager locationManager;
    
    private GoogleMap gMap = null;
    
    
    public static MainIssueFragment newInstance(Bundle args) {
    	return new MainIssueFragment();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_issue, container, false);		
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(null == savedInstanceState) {
                
        }     
        
      
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initMap((SupportMapFragment )getActivity().getSupportFragmentManager().findFragmentById(R.id.map));      
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

	        
	        gMap.setOnMapClickListener(null);        
	        
	        View mapBlocker = getActivity().findViewById(R.id.map_blocker);
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
	        Intent intent = new Intent(getActivity(), IssueActivity.class);
	        intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
	        startActivity(intent);
	    }
}
