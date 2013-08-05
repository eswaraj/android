package com.swaraj.vashisthg.helpers;

import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.swaraj.vashisthg.models.Constituency;

public class LocationDataManager {
	Activity activity;
	private static final String TAG = "LocationDataManager";
	
	public LocationDataManager(Activity activity, ReverseGeoCodingTask.GeoCodingTaskListener geoCodingTaskListener) {
		this.activity = activity;
		this.geoCodingListener = geoCodingTaskListener;
	}	
	
	public void fetchAddress(Location location) {
		if(null != location) {
			ReverseGeoCodingTask geocodingTask = ReverseGeoCodingTask.newInstance(activity, mainGeoCodingListener, location);
			geocodingTask.execute();
		} else {
			Log.e(TAG, "location is null, so can't fetch address");
		}
	}
	
	private ReverseGeoCodingTask.GeoCodingTaskListener geoCodingListener ;
	
	private ReverseGeoCodingTask.GeoCodingTaskListener mainGeoCodingListener = new ReverseGeoCodingTask.GeoCodingTaskListener() {
		
		@Override
		public void didReceiveGeoCoding(List<Constituency> locations) {
			geoCodingListener.didReceiveGeoCoding(locations);
		}
		
		@Override
		public void didFailReceivingGeoCoding() {
			geoCodingListener.didFailReceivingGeoCoding();
		}
	};
	
}
