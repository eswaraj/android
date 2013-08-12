package com.next.eswaraj.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.next.eswaraj.models.Constituency;
import com.next.eswaraj.R;

public class ConstuencyParserHelper {

	public static ArrayList<Constituency> readLocations(Context context, int locationRef) throws IOException {
		
		String fileName = "centroid.txt";
		if(locationRef == R.integer.id_city_bangalore) {
			fileName = "centroid_Blr.txt";
		}
		
		InputStream inputStream = context.getAssets().open(fileName);
		BufferedReader f = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		ArrayList<Constituency> locations = new ArrayList<Constituency>();
		while ((line = f.readLine()) != null) {
			String row[] = line.split(",");
			parseRow(row, locations);
		}
		return locations;
	}

	private static void parseRow(String[] row, ArrayList<Constituency> locations) {
		Constituency loc = new Constituency();
		loc.setName(row[0]);
		loc.setID(Integer.parseInt(row[1].trim()));
		loc.setLatLong(new LatLng(Double.parseDouble(row[2]), Double.parseDouble(row[3])));
		locations.add(loc);
	}
	
}
