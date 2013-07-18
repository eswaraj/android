package com.jansampark.vashisthg.models;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;


public class Constituency {
	private String name;
	private int ID;
	private LatLng latLong;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public LatLng getLatLong() {
		return latLong;
	}

	public void setLatLong(LatLng latLong) {
		this.latLong = latLong;
	}
	
	
	public static Constituency createLocation(Address address) {		
		Constituency location = new Constituency();														     		
		location.name = getLocationString( address);
		return location;				
	}
	
	private static String getLocationString( Address address) {						
		String subLocality = address.getSubLocality();
		String city = address.getLocality();
		String state = address.getAdminArea();
		String country = address.getCountryName();
		
		String[] locationFields = new String[6];
		
		
		locationFields[0] = subLocality;
		locationFields[1] = ", " + city;
		locationFields[2] = ", " + state;
		locationFields[3] = ", " + country;
		
		StringBuffer locationAddress = new StringBuffer();
		
		for(int i = 0; i < locationFields.length; i++) {
			if(null != locationFields[i] && !", ".equals(locationFields[i])) {
				locationAddress.append(locationFields[i]);
			}
		}
	    return locationAddress.toString();		
	}

}
