package com.jansampark.vashisthg;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.jansampark.vashisthg.helpers.ReverseGeoCodingTask;
import com.jansampark.vashisthg.helpers.TitleBarHelper;
import com.jansampark.vashisthg.models.Constituency;

public class MainActivity extends FragmentActivity  {
	public static final String TAG = "Main";

	public static final String EXTRA_LOCATION = "location";
    MainAnalyticsFragment analyticsFragment;
    MainIssueFragment issueFragment;
    private MyPagerAdapter adapter;
    
	private ViewPager viewPager;
	private RadioGroup footerTab;
	private ViewGroup titleBar;
	TitleBarHelper titleBarHelper;
	Location lastKnownLocation;
	
	   LocationRequest locationRequest;
	    LocationClient locationClient;
	    boolean isResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        footerTab = (RadioGroup) findViewById(R.id.main_tabs);
        titleBar = (ViewGroup) findViewById(R.id.main_title_bar);
                
        if(null == savedInstanceState) {
        	initFragments();
        } else {
        	lastKnownLocation = savedInstanceState.getParcelable(EXTRA_LOCATION);
        }
        initViewPagingAndTabs();
        setTitleBar();        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	isResumed = true;
    	startLocationTracking();
    }
    
    @Override
    protected void onPause() {
    	if(locationClient.isConnected()) {
    		locationClient.removeLocationUpdates(mLocationListener);
    		locationClient.disconnect();
    	}
		isResumed = false;
    	super.onPause();
    }
    
    private void setTitleBar() {
    	titleBarHelper = new TitleBarHelper();
    	titleBarHelper.setTitleBar(titleBar);
    	titleBarHelper.setLeftButtonIcon(R.drawable.ic_info);
    	titleBarHelper.setRightButtonIcon(R.drawable.profile_image);   	
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putParcelable(EXTRA_LOCATION, lastKnownLocation);
    }
    
    
    private void initFragments() {
    	Log.d(TAG, "initFragments");
		if( null == analyticsFragment) {
			analyticsFragment = MainAnalyticsFragment.newInstance(null);
		}
		if(null == issueFragment) {
			issueFragment = MainIssueFragment.newInstance();
		}
	}
    
    private void initViewPagingAndTabs() {
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(mOnPageChangeListener);
		footerTab.setOnCheckedChangeListener(onCheckedChangeListener);	
	}    
    
    public class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			 if (0 == position) {
				 return issueFragment;
			 } else if (1 == position) {
				 return analyticsFragment;
			 }
			 return null;
		}
	}
    
	private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.tab_issue) {				
				viewPager.setCurrentItem(0);
			} else if (checkedId == R.id.tab_analytics) {
				viewPager.setCurrentItem(1);
			}
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		public void onPageScrollStateChanged(int arg0) {	 }
		public void onPageScrolled(int arg0, float arg1, int arg2) {	}
		public void onPageSelected(int position) {						
			if (0 == position) {
				footerTab.check(R.id.tab_issue);
				if(issueFragment != null) {
					issueFragment.showLocationName();
				}
				
			} else {
				footerTab.check(R.id.tab_analytics);
				if(null != analyticsFragment) {
					analyticsFragment.onFragmentShown();
				}
//				if(analyticsFragment != null) {
//    				analyticsFragment.setCurrentCity();
//    			}
			}	
		}
	}; 
	
	
	public void onTitleBarLeftButtonClick(View view) {
		startActivity(new Intent(this, InfoActivity.class));
	}
	
	public void onTitleBarRightButtonClick(View view) {
		startActivity(new Intent(MainActivity.this, UserImageActivity.class));
	}
	
	protected void startLocationTracking() {	
	    if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
	        locationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
	        locationClient.connect();
	    }
	}

	private ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks() {

	    @Override
	    public void onDisconnected() {
	    }

	    @Override
	    public void onConnected(Bundle arg0) {	
	    	if(locationClient.isConnected()) {
		    	lastKnownLocation = locationClient.getLastLocation();
		        LocationRequest locationRequest = LocationRequest.create();
		        locationRequest.setInterval(getResources().getInteger(R.integer.location_update_millis)).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		        locationClient.requestLocationUpdates(locationRequest, mLocationListener);
	    	}
	    }
	};

	private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() {

	    @Override
	    public void onConnectionFailed(ConnectionResult arg0) {
	    }
	};

	private LocationListener mLocationListener = new LocationListener() {
	    @Override
        public void onLocationChanged(Location location) {	         
            if(isResumed) {
    			lastKnownLocation =  location;
    			Log.d("Issue", "location changed");
    			JanSamparkApplication.getInstance().setLastKnownLocation(lastKnownLocation);
    			if(issueFragment != null) {
    				issueFragment.showLocation();   
    			}
    			
    			
    			LocationDataManager dataManager = new LocationDataManager(MainActivity.this, new  ReverseGeoCodingTask.GeoCodingTaskListener() {
					
					@Override
					public void didReceiveGeoCoding(List<Constituency> locations) {
						JanSamparkApplication.getInstance().setLastKnownConstituency(locations.get(0));
						if(issueFragment != null) {
							issueFragment.showLocationName();
						}
						if(analyticsFragment != null) {
							
		    				analyticsFragment.setCurrentCity();
		    			}
					}
					
					@Override
					public void didFailReceivingGeoCoding() {
						if(null != JanSamparkApplication.getInstance().getLastKnownConstituency()) {
							if(issueFragment != null) {
								issueFragment.showLocationName();
							}
							if(analyticsFragment != null) {
							
			    				analyticsFragment.setCurrentCity();
			    			}
						}						
					}
				});
    			dataManager.fetchAddress(location);
            }
	    }
	};
}
