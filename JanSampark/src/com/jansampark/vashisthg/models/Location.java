package com.jansampark.vashisthg.models;

import com.google.android.gms.maps.model.LatLng;

public class Location {
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

}
