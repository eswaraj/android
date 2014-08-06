package com.next.eswaraj;

import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.next.eswaraj.models.Constituency;
import com.next.eswaraj.volley.ServerDataUtil;

import de.greenrobot.event.EventBus;

public class JanSamparkApplication extends Application {

    public static String TAG = "Application";
    LocationClient locationClient;
    private BigDecimal longitude;
    private BigDecimal latittude;

    private RequestQueue requestQueue;

    private Location lastKnownLocation;
    private Constituency lastKnownConstituency;

    @Override
    public void onCreate() {
        super.onCreate();
        startLocationTracking();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        ServerDataUtil.getInstance().initData(this);
    }

    @Override
    public void onTerminate() {
        locationClient.removeLocationUpdates(mLocationListener);
    }
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        if (null != lastKnownLocation) {
            this.lastKnownLocation = lastKnownLocation;
        } else {
            Log.e(TAG, "trying to set null location");
        }
    }

    public Constituency getLastKnownConstituency() {
        return lastKnownConstituency;
    }

    public void setLastKnownConstituency(Constituency lastKnownConstituency) {
        if (null != lastKnownConstituency) {
            this.lastKnownConstituency = lastKnownConstituency;
        }
    }

    protected void startLocationTracking() {
        Log.i("eswaraj", "startLocationTracking");
        setQuickLastKnownLocation();

        if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
            locationClient = new LocationClient(this, mLocationConnectionCallbacks, mConnectionFailedListener);
            locationClient.connect();
            Log.i("eswaraj", "locationClient.connect()");
        }
        Log.i("eswaraj", "startLocationTracking Done");
    }

    private void setQuickLastKnownLocation() {
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        Log.i("eswaraj", "lastKnownLocation= " + lastKnownLocation);
        setLastKnownLocation(lastKnownLocation);

    }

    private ConnectionCallbacks mLocationConnectionCallbacks = new ConnectionCallbacks() {

        @Override
        public void onDisconnected() {
            Log.i("eswaraj", "Disconnected");
        }

        @Override
        public void onConnected(Bundle arg0) {
            Log.i("eswaraj", "onConnected " + locationClient.isConnected());
            if (locationClient.isConnected()) {
                lastKnownLocation = locationClient.getLastLocation();
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(getResources().getInteger(R.integer.location_update_millis)).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                locationClient.requestLocationUpdates(locationRequest, mLocationListener);
                Log.i("eswaraj", "onConnected requestLocationUpdates, lastKnownLocation=" + lastKnownLocation + ", " + locationClient.isConnecting() + " , " + locationClient.isConnected() + ", ");

            }
        }
    };

    private boolean isLocationActuallyChanged(Location location) {
        if (longitude == null || latittude == null) {
            return true;
        }
        BigDecimal newLongitude = new BigDecimal(location.getLongitude()).setScale(3, RoundingMode.DOWN);
        BigDecimal newLatitude = new BigDecimal(location.getLongitude()).setScale(3, RoundingMode.DOWN);
        if (!newLatitude.toString().equals(latittude) || !newLongitude.toString().equals(longitude)) {
            // Location has changed
            latittude = newLatitude;
            longitude = newLongitude;
            return true;
        }
        return false;
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setLastKnownLocation(location);
            Log.i("eswaraj", "location changed " + location + isLocationActuallyChanged(location));
            EventBus.getDefault().postSticky(location);
            // Send Event on EventBus to update all listeners that Location has
            // changed
            /*
             * if (issueFragment != null) { issueFragment.showLocation(); }
             */
            /*
             * LocationDataManager dataManager = new
             * LocationDataManager(MainActivity.this, new
             * ReverseGeoCodingTask.GeoCodingTaskListener() {
             * 
             * @Override public void didReceiveGeoCoding(List<Constituency>
             * locations) {
             * JanSamparkApplication.getInstance().setLastKnownConstituency
             * (locations.get(0)); if (issueFragment != null) {
             * issueFragment.showLocationName(); } if (analyticsFragment !=
             * null) { analyticsFragment.setCurrentCity(); } }
             * 
             * @Override public void didFailReceivingGeoCoding() { if (null !=
             * JanSamparkApplication.getInstance().getLastKnownConstituency()) {
             * if (issueFragment != null) { issueFragment.showLocationName(); }
             * if (analyticsFragment != null) {
             * 
             * analyticsFragment.setCurrentCity(); } } } });
             * dataManager.fetchAddress(location);
             */
        }
    };

    private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() {

        @Override
        public void onConnectionFailed(ConnectionResult arg0) {
            Log.i("eswaraj", "Failed to connect to Network");
        }
    };

    public void submitServerRequest(String requestTag, Request request) {
        requestQueue.cancelAll(requestTag);
        requestQueue.add(request);
        request.setTag(requestTag);
    }

}
