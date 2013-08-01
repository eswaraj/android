package com.jansampark.vashisthg;

import java.util.List;

import android.app.Activity;
import android.location.Location;

import com.jansampark.vashisthg.helpers.ReverseGeoCodingTask;
import com.jansampark.vashisthg.models.Constituency;

public class LocationDataManager {
	Activity activity;
	
	
	public LocationDataManager(Activity activity, ReverseGeoCodingTask.GeoCodingTaskListener geoCodingTaskListener) {
		this.activity = activity;
		this.geoCodingListener = geoCodingTaskListener;
	}	
	
	public void fetchAddress(Location location) {
		ReverseGeoCodingTask geocodingTask = ReverseGeoCodingTask.newInstance(activity, mainGeoCodingListener, location);
		geocodingTask.execute();
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
