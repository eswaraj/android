package com.swaraj.vashisthg;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.swaraj.vashisthg.helpers.DialogFactory;
import com.swaraj.vashisthg.helpers.LocationDataManager;
import com.swaraj.vashisthg.helpers.ReverseGeoCodingTask;
import com.swaraj.vashisthg.helpers.TitleBarHelper;
import com.swaraj.vashisthg.helpers.Utils;
import com.swaraj.vashisthg.helpers.WindowAnimationHelper;
import com.swaraj.vashisthg.helpers.YouTubeVideoHelper;
import com.swaraj.vashisthg.models.Constituency;

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
	
	public final static int PROGRESS_POSITION1 = 25;
	public final static int PROGRESS_POSITION3 = 75;
	
	SeekBar seekBar;
	Handler seekHandler;
	int seekBarAnimProgress;
	int seekBarProgress;
	RepeatTimer seekTimer;
	
	
	  // LocationRequest locationRequest;
	    LocationClient locationClient;
	    boolean isResumed;
	YouTubeVideoHelper youtubeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        footerTab = (RadioGroup) findViewById(R.id.main_tabs);
        titleBar = (ViewGroup) findViewById(R.id.main_title_bar);
        seekBar = (SeekBar) findViewById(R.id.tab_anim_seekbar);
                
        youtubeHelper = new YouTubeVideoHelper(this);
        if(null == savedInstanceState) {
        	initFragments();
        } else {
        	lastKnownLocation = savedInstanceState.getParcelable(EXTRA_LOCATION);
        }
        initViewPagingAndTabs();
        setTitleBar();   
        setUpSeekBar();
        
        youtubeHelper.downloadYouTubeLinks();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	isResumed = true;
    	startLocationTracking();
    }
			
	
    
    @Override
    protected void onPause() {
    	if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
	    	if(locationClient.isConnected()) {
	    		locationClient.removeLocationUpdates(mLocationListener);
	    		locationClient.disconnect();
	    	}
    	}
		isResumed = false;
    	super.onPause();
    }
    
    private void setTitleBar() {
    	titleBarHelper = new TitleBarHelper();
    	titleBarHelper.setTitleBar(titleBar);
    	titleBarHelper.setLeftButtonIcon(R.drawable.ic_info);
    	titleBarHelper.setRightButtonIcon(R.drawable.settings_icon);   	
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
				seekBarProgress = PROGRESS_POSITION1;
			} else if (checkedId == R.id.tab_analytics) {
				viewPager.setCurrentItem(1);
				seekBarProgress = PROGRESS_POSITION3;
			}
			new Thread(seekTimer).start();
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
	
	private void setUpSeekBar() {
		seekBar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		seekHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				seekBar.setProgress(seekBarAnimProgress);
			}
		};
		seekTimer = new RepeatTimer();
	}
	
	
	private class RepeatTimer implements Runnable {

		@Override
		public void run() {
			while (seekBarAnimProgress != seekBarProgress) {
				if (seekBarAnimProgress < seekBarProgress) {
					seekBarAnimProgress ++;
				} else {
					seekBarAnimProgress --;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				seekHandler.sendEmptyMessage(0);
			}
		}
	}
	
	
	public void onTitleBarLeftButtonClick(View view) {
		WindowAnimationHelper.startActivityWithSlideFromRight(this, InfoActivity.class);
	}
	
	public void onTitleBarRightButtonClick(View view) {
		WindowAnimationHelper.startActivityWithSlideFromRight(this, SettingsActivity.class);
	}
	
	public void showTitleBarProgress() {
		titleBarHelper.showProgressBar();
	}
	
	public void hideTitleBarProgress() {
		titleBarHelper.hideProgressBar();
	}
	
	protected void startLocationTracking() {	
		setQuickLastKnownLocation();
	    if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
	        locationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
	        locationClient.connect();
	    }
	}
	
	private void setQuickLastKnownLocation() {
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		JanSamparkApplication.getInstance().setLastKnownLocation(lastKnownLocation);
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
    			
    			JanSamparkApplication.getInstance().setLastKnownLocation(location);
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
	
	
	
	@Override
	public void onBackPressed() {
		boolean disabledAutoComplete = false;
		if(analyticsFragment != null) {
			disabledAutoComplete = analyticsFragment.disableAutoComplete();
		}
		if(!disabledAutoComplete) {
			super.onBackPressed();
		}
	}
}
