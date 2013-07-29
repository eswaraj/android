package com.jansampark.vashisthg;

import java.util.List;

import android.app.Activity;
import android.location.Location;

import com.jansampark.vashisthg.helpers.ReverseGeoCodingTask;
import com.jansampark.vashisthg.helpers.ReverseGeoCodingTask.GeoCodingTaskListener;
import com.jansampark.vashisthg.models.Constituency;

public class LocationDataManager {
	Activity activity;
	
	
	public LocationDataManager(Activity activity, GeoCodingTaskListener listener) {
		this.activity = activity;
		this.geoCodingListener = listener;
	}

	
	
	public void fetchAddress(Location location) {
		ReverseGeoCodingTask geocodingTask = ReverseGeoCodingTask.newInstance(activity, geoCodingListener, location);
		geocodingTask.execute();
	}
	
	private ReverseGeoCodingTask.GeoCodingTaskListener geoCodingListener ;
	
	
}
