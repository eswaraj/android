package com.next.eswaraj.models;

import java.util.Locale;

import android.location.Address;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.next.eswaraj.R;


public class Constituency {
	
	
	
	private String name;
	private int ID;	
	private LatLng latLong;
	
	
	private Address address;
	
	
	
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
		location.setAddress(address);
		return location;				
	}
	
	public static String getLocationString( Address address) {						
		String subLocality = address.getSubLocality();
		String city = address.getLocality();
		String state = address.getAdminArea();
		String country = address.getCountryName();
		
		String[] locationFields = new String[6];
		
		
		locationFields[0] = subLocality;
		locationFields[1] = city;
		locationFields[2] = state;
		locationFields[3] = country;
		
		StringBuffer locationAddress = new StringBuffer();
		
		for(int i = 0; i < locationFields.length; i++) {
			if(!isNull(locationFields[i]) && !", ".equals(locationFields[i])) {
				if(locationAddress.length() != 0) {
					locationAddress.append(", " + locationFields[i]);
				} else {
					locationAddress.append(locationFields[i]);
				}
			}
		}
		
		String formattedAddress = locationAddress.toString();
		
		if(TextUtils.isEmpty(formattedAddress)) {
			formattedAddress = "India";
		}
		
	    return formattedAddress;		
	}
	
	private static boolean isNull(String string) {
		boolean isNull = false;
		if(null == string) {
			isNull = true;
		} else {
			if("null".equalsIgnoreCase(string)) {
				isNull = true;
			}
		}
		return isNull;		
	}
	
	public static int getCityRefId(Address address) {
		int city;
		int delhi = R.integer.id_city_delhi;
		int bangalore = R.integer.id_city_bangalore;
		if(null != address.getAdminArea()) {
			String state = address.getAdminArea().toLowerCase(Locale.US);
			if(state.equalsIgnoreCase("karnataka")) {
				city = bangalore;
			}			
			city = delhi;
		}
		
		if(null != address.getLocality()) {
			String geocodedLocality = address.getLocality().toLowerCase(Locale.US);
			if(geocodedLocality.equalsIgnoreCase("bangalore")
					|| geocodedLocality.equalsIgnoreCase("bangaluru")) {
				city = bangalore;
			}
			city = delhi;
		}
		city = delhi;
		
		return city;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
