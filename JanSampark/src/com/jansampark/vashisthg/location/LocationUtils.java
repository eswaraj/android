/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jansampark.vashisthg.location;

import com.google.android.gms.maps.model.LatLng;
import com.jansampark.vashisthg.R;

import android.content.Context;
import android.location.Location;



/**
 * Defines app-wide constants and utilities
 */
public final class LocationUtils {

    // Debugging tag for the application
    public static final String APPTAG = "LocationSample";

    // Name of shared preferences repository that stores persistent state
    public static final String SHARED_PREFERENCES =
            "com.example.android.location.SHARED_PREFERENCES";

    // Key for storing the "updates requested" flag in shared preferences
    public static final String KEY_UPDATES_REQUESTED =
            "com.example.android.location.KEY_UPDATES_REQUESTED";

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;

    // Create an empty string for initializing strings
    public static final String EMPTY_STRING = new String();

    /**
     * Get the latitude and longitude from the Location object returned by
     * Location Services.
     *
     * @param currentLocation A Location object containing the current location
     * @return The latitude and longitude of the current location, or null if no
     * location is available.
     */
    public static LatLng getLatLng(Context context, Location currentLocation) {
    	LatLng latLng = new LatLng(0, 0);
        // If the location is valid
        if (currentLocation != null) {
        	latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());      
        }
        return latLng;
    }
    
    
    
    
    public static final int TEN_SECONDS = 10000;
    public static final int TEN_METERS = 10;
    public static final int TWO_MINUTES = 1000 * 60 * 2;

 	
 	 public static  Location getBetterLocation(Location newLocation, Location currentBestLocation) {
 	      if (currentBestLocation == null) {
 	          // A new location is always better than no location
 	          return newLocation;
 	      }

 	      // Check whether the new location fix is newer or older
 	      long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
 	      boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
 	      boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
 	      boolean isNewer = timeDelta > 0;

 	      // If it's been more than two minutes since the current location, use the new location
 	      // because the user has likely moved.
 	      if (isSignificantlyNewer) {
 	          return newLocation;
 	      // If the new location is more than two minutes older, it must be worse
 	      } else if (isSignificantlyOlder) {
 	          return currentBestLocation;
 	      }

 	      // Check whether the new location fix is more or less accurate
 	      int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
 	      boolean isLessAccurate = accuracyDelta > 0;
 	      boolean isMoreAccurate = accuracyDelta < 0;
 	      boolean isSignificantlyLessAccurate = accuracyDelta > 200;

 	      // Check if the old and new location are from the same provider
 	      boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
 	              currentBestLocation.getProvider());

 	      // Determine location quality using a combination of timeliness and accuracy
 	      if (isMoreAccurate) {
 	          return newLocation;
 	      } else if (isNewer && !isLessAccurate) {
 	          return newLocation;
 	      } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
 	          return newLocation;
 	      }
 	      return currentBestLocation;
 	  }
 	   /** Checks whether two providers are the same */
 	   private static boolean isSameProvider(String provider1, String provider2) {
 	       if (provider1 == null) {
 	         return provider2 == null;
 	       }
 	       return provider1.equals(provider2);
 	   }
}
