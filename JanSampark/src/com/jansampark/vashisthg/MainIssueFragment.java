package com.jansampark.vashisthg;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.GoogleMapOptionsCreator;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jansampark.vashisthg.widget.CustomSupportMapFragment;
import com.saulpower.piechart.adapter.PieChartAdapter;
import com.saulpower.piechart.extra.FrictionDynamics;
import com.saulpower.piechart.views.PieChartView;
import com.saulpower.piechart.views.PieChartView.PieChartAnchor;

public class MainIssueFragment extends Fragment implements LocationListener{

    private LocationManager locationManager;
    private LocationProvider locationProvider;
    
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
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initMap((CustomSupportMapFragment )getActivity().getSupportFragmentManager().findFragmentById(R.id.map));     
		
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		lastKnownLocation = locationManager
				.getLastKnownLocation(locationProvider);
		showLocation();
		initButtonListeners();
		initTitleBar();
	}
	
	
	
	private void initButtonListeners() {
		getActivity().findViewById(R.id.main_electricity).setOnClickListener(buttonListener);		
		getActivity().findViewById(R.id.main_law).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_road).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_sewage).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_transportation).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_water).setOnClickListener(buttonListener);
	}
	
	private void initTitleBar() {
		((ImageButton)getActivity().findViewById(R.id.title_bar_left_button)).setImageResource(R.drawable.ic_info);
	}

	public void initMap(CustomSupportMapFragment mapFragment) {
		gMap = mapFragment.getMap();

		// / REMOVE FOR DEBUG EMULATOR
		gMap.setMyLocationEnabled(false);
		
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria,true);
		
		locationManager.requestLocationUpdates(provider, 20000, 0,this);
		
		
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
	
	Location lastKnownLocation;
	    
	private void showLocation() {
		
		LatLng lastKnownLatLng = new LatLng(lastKnownLocation.getLatitude(),
				lastKnownLocation.getLongitude());
		gMap.clear();

		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 15));
		gMap.addMarker(new MarkerOptions().position(lastKnownLatLng).icon(
				BitmapDescriptorFactory
						.fromResource(R.drawable.ic_main_annotation)));
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
		openIssueActivity(ISSUES.ELECTRICITY);
	}

	public void onLawAndOrderClick(View view) {
		
	}

	private void openIssueActivity(ISSUES issue) {
		Intent intent = new Intent(getActivity(), IssueActivity.class);
		intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
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
		lastKnownLocation = location;
		showLocation();
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