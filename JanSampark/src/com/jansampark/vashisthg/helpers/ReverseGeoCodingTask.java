package com.jansampark.vashisthg.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.jansampark.vashisthg.models.Constituency;

public class ReverseGeoCodingTask  extends AsyncTask< String, Void, List<Constituency>> {
	public interface GeoCodingTaskListener {
		public void didReceiveGeoCoding(List<Constituency> locations);
		public void didFailReceivingGeoCoding();
	}
	
	private GeoCodingTaskListener geoCodingCallBack;
	private Location location;
	private Context context;
		
	public static ReverseGeoCodingTask newInstance(Context context, GeoCodingTaskListener callBack, Location location) {
		return new ReverseGeoCodingTask(context, callBack, location);
	}		

	protected ReverseGeoCodingTask(Context context, GeoCodingTaskListener callBack, Location location) {
		this.context = context;
		geoCodingCallBack = callBack;
		this.location = location;
	}

	@Override
	protected List<Constituency> doInBackground(String... arg0) {		
		try {
			Geocoder geocoder = new Geocoder(context, Locale.getDefault());
			List<Address> addresses = null;
			List<Constituency> locations = new ArrayList<Constituency>();
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 8);
			for (Iterator<Address> iterator = addresses.iterator(); iterator.hasNext();) {
				Address address = (Address) iterator.next();
				Constituency archeTypeLocation = Constituency.createLocation(address);
				archeTypeLocation.setLatLong(new LatLng(location.getLatitude(), location.getLongitude()));
				
				locations.add(archeTypeLocation);
			}
			
			return locations;
		} catch (IOException e) {
			e.printStackTrace();
			return null;			
		}		
	}
	
	@Override
	protected void onPostExecute(List<Constituency> result) {
		super.onPostExecute(result);
		if(null != result) {
			geoCodingCallBack.didReceiveGeoCoding(result);
		} else {
			geoCodingCallBack.didFailReceivingGeoCoding();
		}
	}
}