package com.swaraj.vashisthg;

import com.swaraj.vashisthg.models.Constituency;

import android.app.Application;
import android.location.Location;
import android.util.Log;

public class JanSamparkApplication extends Application {
	
	public static String TAG = "Application";
	
    private static JanSamparkApplication instance;
    public static JanSamparkApplication getInstance() {
        return instance;
    }
    
	
	private Location lastKnownLocation;
	private Constituency lastKnownConstituency;
	
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
		} else {
			Log.e(TAG, "trying to set null location");
		}
	}

	public Constituency getLastKnownConstituency() {
		return lastKnownConstituency;
	}

	public void setLastKnownConstituency(Constituency lastKnownConstituency) {
		if(null != lastKnownConstituency) {
			this.lastKnownConstituency = lastKnownConstituency;
		}
	}
			
}
