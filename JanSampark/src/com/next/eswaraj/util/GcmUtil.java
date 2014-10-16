package com.next.eswaraj.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eswaraj.web.dto.UserDto;
import com.eswaraj.web.dto.device.RegisterGcmDeviceId;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.next.eswaraj.config.Preferences;
import com.next.eswaraj.helpers.MobileSessionHelper;
import com.next.eswaraj.volley.RegisterGcmRequest;

public class GcmUtil {

    private static String GCM_SENDER_ID = "788694755236";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Get device RegistrationId from Cache
	 * @param prefs
	 * @return
	 */
	public static String getGcmDeviceRegistrationId(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Preferences.GCM_REGISTRATION_PREF, Context.MODE_PRIVATE);
		return getGcmDeviceRegistrationId(prefs);
	}
	/**
	 * Get device RegistrationId from Cache
	 * @param prefs
	 * @return
	 */
	public static String getGcmDeviceRegistrationId(SharedPreferences prefs){
        return prefs.getString(Preferences.PROPERTY_REG_ID, null);
	}
	public static String getGcmDeviceRegistrationStatus(SharedPreferences prefs){
        return prefs.getString(Preferences.PROPERTY_REG_ID_REGISTRATION_STATUS, null);
	}
	public static String getOrCreateDeviceRegistrationId(Context context){
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String newRegistrationId = null;
		try {
			newRegistrationId = gcm.register(GCM_SENDER_ID);
		} catch (IOException e) {
            Log.e("eswaraj", "Unable to get GCM ID", e);
		}
		return newRegistrationId;
	}
	public static String getDeviceType(){
		return "Android";
	}

    public static void ensureDeviceIsRegistered(final Activity activity) {
        if (checkPlayServices(activity)) {
            new AsyncTask<Object, Boolean, Boolean>() {

                @Override
                protected Boolean doInBackground(Object... params) {
                    // Make sure the app is registered with GCM and with the
                    // server
                    SharedPreferences prefs = activity.getSharedPreferences(Preferences.GCM_REGISTRATION_PREF, Context.MODE_PRIVATE);

                    String registrationStatus = getGcmDeviceRegistrationStatus(prefs);
                    Log.i("eswaraj", "PROPERTY_REG_ID_REGISTRATION_STATUS=" + registrationStatus);
                    // Which we have registered to google and our server last
                    // time
                    String existingRegistrationId = getGcmDeviceRegistrationId(prefs);
                    Log.i("eswaraj", "existingRegistrationId=" + existingRegistrationId);
                    // Registration Id can change so make sure if it has changed
                    // we re register it
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
                    String newRegistrationId = null;
                    try {
                        newRegistrationId = gcm.register(GCM_SENDER_ID);
                    } catch (IOException e) {
                        Log.e("eswaraj", "Unable to get GCM ID", e);
                    }
                    Log.i("eswaraj", "newRegistrationId=" + newRegistrationId);

                    if (registrationStatus != null && Preferences.REGISTRATION_STATUS_YES.equals(registrationStatus)) {
                        // Device RegId already registered with google and our
                        // server
                        Log.i("eswaraj", "Device already registered with google and our server");
                        // Now check if App has been used N times so that we can
                        // reregister GCM Id or if GCM Id has been changed
                        if (existingRegistrationId != null && newRegistrationId != null && existingRegistrationId.equals(newRegistrationId)) {
                            // There is no change in Registration id so just
                            // return and dont call server
                            Log.i("eswaraj", "New and exiting device ids are same, wo will not reregister");
                            return false;
                        }
                        if (newRegistrationId == null) {
                            // registration id returned by google is null, so we
                            // can not do anything
                            Log.i("eswaraj", "New device id is null, wo will not reregister");
                            return false;
                        }
                    }

                    Log.i("eswaraj", "Registering device id at server");
                    registerDeviceOnServer(activity, newRegistrationId, existingRegistrationId);
                    return true;
                }
            }.execute(null, null);
        }
	}
	
    private static void registerDeviceOnServer(final Activity activity,final String deviceRegId, final String previousRegistrationId){
	    UserDto user = MobileSessionHelper.getLoggedInUser(activity);
	    if(user == null){
	        Log.i("eswaraj","User is not registered so cant register device GCM");
	        return;
	    }
	    String android_id = DeviceUtil.getDeviceid(activity);
	    RegisterGcmDeviceId registerGcmDeviceId = new RegisterGcmDeviceId();
	    registerGcmDeviceId.setUserExternalId(user.getExternalId());
	    registerGcmDeviceId.setDeviceId(android_id);
	    registerGcmDeviceId.setGcmId(deviceRegId);
        try {
            RegisterGcmRequest registerGcmRequest = new RegisterGcmRequest(createMyReqErrorListener(), createMyReqSuccessListener(activity, deviceRegId), registerGcmDeviceId);
            RequestQueue mRequestQueue = Volley.newRequestQueue(activity.getApplicationContext());
            registerGcmRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
            mRequestQueue.add(registerGcmRequest);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
	}

    private static Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != error) {
                    Log.e("eswaraj", "" + error.getMessage(), error);
                }
            }
        };
    }

    private static Response.Listener<String> createMyReqSuccessListener(final Activity activity, final String gcmId) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("details", "response: " + response);
                deviceRegisteredSuccesfully(activity, gcmId);
            }
        };
    }
	public static void deviceRegisteredSuccesfully(Context context,String deviceRegId){
        Log.i("eswaraj", "deviceRegisteredSuccesfully = " + deviceRegId);
        SharedPreferences prefs = context.getSharedPreferences(Preferences.GCM_REGISTRATION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Preferences.PROPERTY_REG_ID_REGISTRATION_STATUS, Preferences.REGISTRATION_STATUS_YES);
        editor.putString(Preferences.PROPERTY_REG_ID, deviceRegId);
        editor.commit();
	}

    private static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("eswaraj", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
}
