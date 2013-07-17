package com.jansampark.vashisthg;

import android.app.Application;
import android.location.Location;

public class JanSamparkApplication extends Application {

	private Location lastKnownLocation;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public Location getLastKnownLocation() {
		return lastKnownLocation;
	}
	
	
	public void setLastKnownLocation(Location lastKnownLocation) {
		this.lastKnownLocation = lastKnownLocation;
	}
	
}
