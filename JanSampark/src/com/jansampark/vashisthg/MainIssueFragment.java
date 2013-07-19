package com.jansampark.vashisthg;



import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.widget.CustomSupportMapFragment;

public class MainIssueFragment extends Fragment implements LocationListener{

    private LocationManager locationManager;
    
    private static final String SAVED_LOCATION = "SAVED_LOCATION";
    
    private GoogleMap gMap = null;
    boolean isResumed;
    Location lastKnownLocation;
    
    
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
        
        if(null != savedInstanceState) {
        	lastKnownLocation = savedInstanceState.getParcelable(SAVED_LOCATION);
        }
    }
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(SAVED_LOCATION, lastKnownLocation);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initMap((CustomSupportMapFragment )getActivity().getSupportFragmentManager().findFragmentById(R.id.map));     
		
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		showLocation();
		initButtonListeners();
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;
		requestLocationUpdates();
	}
	
	@Override
	public void onPause() {
		isResumed = false;
		locationManager.removeUpdates((android.location.LocationListener) this);
		super.onPause();
	}
	private void initButtonListeners() {
		getActivity().findViewById(R.id.main_electricity).setOnClickListener(buttonListener);		
		getActivity().findViewById(R.id.main_law).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_road).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_sewage).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_transportation).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_water).setOnClickListener(buttonListener);
	}
	
	
	private void requestLocationUpdates() {
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria,true);		
		locationManager.requestLocationUpdates(provider, 20000, 0, this);
	}
	

	public void initMap(CustomSupportMapFragment mapFragment) {
		gMap = mapFragment.getMap();

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
		Location location = lastKnownLocation;
		if(null != location) {
			LatLng lastKnownLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			gMap.clear();	
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 15));
			gMap.addMarker(new MarkerOptions().position(lastKnownLatLng).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.ic_main_annotation)));
		} 
	}

	public void onSewageClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.SEWAGE);
	}

	public void onTransportationClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.TRANSPORT);
	}

	public void onWaterClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.WATER);
	}

	public void onRoadClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.ROAD);
	}

	public void onElectricityClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.ELECTRICITY);
	}

	public void onLawAndOrderClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.LAW);
	}

	private void openIssueActivity(ISSUE_CATEGORY issue) {
		Intent intent = new Intent(getActivity(), IssueActivity.class);
		intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
		intent.putExtra(IssueActivity.EXTRA_LOCATION, lastKnownLocation);
		startActivity(intent);
	}
	android.view.View.OnClickListener buttonListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {						
			int id = view.getId();
			
			switch (id) {
			case R.id.main_road:
				onRoadClick(view);
				break;
			case R.id.main_law:
				onLawAndOrderClick(view);
				break;
			case R.id.main_electricity:
				onElectricityClick(view);
				break;
			case R.id.main_sewage:
				onSewageClick(view);
				break;
			case R.id.main_transportation:
				onTransportationClick(view);
				break;
			case R.id.main_water:
				onWaterClick(view);
				break;

			default:
				break;
			}
		}
	};


	@Override
	public void onLocationChanged(Location location) {
		if(isResumed) {
			lastKnownLocation =  location;
			Log.d("Issue", "location changed");
			showLocation();
		}
		locationManager.removeUpdates((android.location.LocationListener) this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
}
