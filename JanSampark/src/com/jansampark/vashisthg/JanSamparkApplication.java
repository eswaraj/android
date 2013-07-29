package com.jansampark.vashisthg;

import android.app.Application;
import android.location.Location;

public class JanSamparkApplication extends Application {
	
    private static JanSamparkApplication instance;
    public static JanSamparkApplication getInstance() {
        return instance;
    }
    
	
	private Location lastKnownLocation;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public Location getLastKnownLocation() {
		return lastKnownLocation;
	}

	public void setLastKnownLocation(Location lastKnownLocation) {
		if(null != lastKnownLocation) {
			this.lastKnownLocation = lastKnownLocation;
		}
	}
			
}
