package com.next.eswaraj;



import java.lang.reflect.Type;
import java.util.List;

import org.json.JSONArray;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.next.eswaraj.helpers.DialogFactory;
import com.next.eswaraj.helpers.WindowAnimationHelper;
import com.next.eswaraj.widget.CustomSupportMapFragment;

import de.greenrobot.event.EventBus;

public class MainIssueFragment extends Fragment {
   
    private GoogleMap gMap = null;
    private ProgressBar progressBar;
    boolean isResumed;
    private RequestQueue mRequestQueue;
    private String requestTag = "Category";
    private GridView gridView;
    
    
    public static MainIssueFragment newInstance() {
    	MainIssueFragment issueFragment = new MainIssueFragment();
    	return issueFragment;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_issue, container, false);
		try{
			Log.i("eswaraj","Creating extra issue type");
			gridView = (GridView)view.findViewById(R.id.main_layout_for_issue_type_buttons);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
			OnItemClickListener onItemClickListener =  new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					CategoryWithChildCategoryDto categoryDto = (CategoryWithChildCategoryDto)parent.getItemAtPosition(position);
					Toast toast = Toast.makeText(getActivity(), "Clicked on "+categoryDto.getName(), Toast.LENGTH_SHORT);
					toast.show();
					openIssueActivity(categoryDto);
				}
				
			};
			gridView.setOnItemClickListener(onItemClickListener);

			Log.i("eswaraj","Done Creating extra issue type");
			
		}catch(Exception ex){
			Log.e("eswaraj", "Unable to create Issue type automatically", ex);
		}

		return view;
	}
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        mRequestQueue = Volley.newRequestQueue(getActivity()
				.getApplicationContext());
        executeGetCategoriesRequest();
        // In case we have already found the location
        Location location = EventBus.getDefault().getStickyEvent(Location.class);
        if (location != null) {
            showLocation(location);
        }
    }
			
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initMap((CustomSupportMapFragment )getActivity().getSupportFragmentManager().findFragmentById(R.id.map));     		
	}	
	
	@Override
	public void onResume() {

		super.onResume();
        Log.i("eswaraj", "Registering Location Event");
        EventBus.getDefault().register(this);
		isResumed = true;
	}
	
	@Override
	public void onPause() {
        Log.i("eswaraj", "UnRegistering Location Event");
        EventBus.getDefault().unregister(this);
		isResumed = false;
		super.onPause();
	}
	private void executeGetCategoriesRequest() {
		mRequestQueue.cancelAll(requestTag);
		String url = "http://dev.admin.eswaraj.com/eswaraj-web/mobile/categories";
		JsonArrayRequest request = new JsonArrayRequest(url, createCategoryReqSuccessListener(), createMyReqErrorListener());
		//JsonArrayRequestWithCache request = new JsonArrayRequestWithCache(url, createAnalyticsReqSuccessListener(), createMyReqErrorListener());
		mRequestQueue.add(request);
		request.setTag(requestTag);
		//showProgressBar();
	}
	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (isResumed) {
					Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
					Log.e("eswaraj", "Unable to connect to service", error);
				}
			}
		};
	}
	private Response.Listener<JSONArray> createCategoryReqSuccessListener() {
		return new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray jsonObject) {
				try {
					if (isResumed) 
					{
						Gson gson = new Gson();
						Type listType = new TypeToken<List<CategoryWithChildCategoryDto>>() {}.getType();
						List<CategoryWithChildCategoryDto> list = gson.fromJson(jsonObject.toString(), listType);
						createAllButtons(list);
					}
					//hideProgressBar();
				} catch (Exception e) {
					Log.e("Error", "Error occured" , e);
				}
			}

		};
	}
	private void createAllButtons(List<CategoryWithChildCategoryDto> list){
		BitmapLruCache bitmapLruCache = new BitmapLruCache();
		RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
		ImageLoader imageLoader = new ImageLoader(requestQueue, bitmapLruCache);
		gridView.setAdapter(new IssueButtonAdapter(getActivity(), list, imageLoader));
	}

	public void initMap(CustomSupportMapFragment mapFragment) {				
		if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            // DialogFactory.createMessageDialog("Map is available").show(getFragmentManager(),
            // "Success");
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
		} else {
			DialogFactory.createMessageDialog(getString(R.string.no_google_map_services)).show(getFragmentManager(), "ERROR");
		}
	}
	
	    
    public void onEvent(Location location) {
        Log.i("eswaraj", "Location hcanged in Main Issue Framegment");
        showLocation(location);

    }

    public void showLocation(Location location) {
		if(null != location) {
            LatLng lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			if(null != gMap) {
				gMap.clear();	
				gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 15));
				gMap.addMarker(new MarkerOptions().position(lastKnownLatLng).icon(
						BitmapDescriptorFactory
								.fromResource(R.drawable.ic_main_annotation)));
                progressBar.setVisibility(View.INVISIBLE);
			}
			
		}  else {
			Log.e("ISSUE", "location is null");
		}
	}
	
	public void showLocationName() {
        if (null != ((JanSamparkApplication) getActivity().getApplication()).getLastKnownConstituency()) {
            ((TextView) getActivity().findViewById(R.id.main_map_location_text)).setText(((JanSamparkApplication) getActivity().getApplication()).getLastKnownConstituency().getName());
		} else {
			Log.e("ISSUE", "current constituency is null");
		}
	}


	private void openIssueActivity(CategoryWithChildCategoryDto selectedCategory) {
		Intent intent = new Intent(getActivity(), IssueActivity.class);
		intent.putExtra(IssueActivity.EXTRA_CATEGORY, selectedCategory);
		WindowAnimationHelper.startActivityWithSlideFromRight(getActivity(), intent);
	}
	
}
