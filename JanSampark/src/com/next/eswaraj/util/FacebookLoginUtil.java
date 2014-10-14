package com.next.eswaraj.util;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.next.eswaraj.R;
import com.next.eswaraj.helpers.MobileSessionHelper;
import com.next.eswaraj.volley.RegisterFacebookUserRequest;

public class FacebookLoginUtil {

    public static void startFacebookLogin(final Activity context) {
        // start Facebook Login
        Session.openActiveSession(context, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    String androidId = DeviceUtil.getDeviceid(context);
                    String deviceTypeRef = DeviceUtil.getDeviceTypeRef();
                    RegisterFacebookAccountRequest registerFacebookAccountRequest = new RegisterFacebookAccountRequest();
                    registerFacebookAccountRequest.setExpireTime(session.getExpirationDate());
                    registerFacebookAccountRequest.setFacebookAppId(context.getString(R.string.facebook_app_id));
                    registerFacebookAccountRequest.setToken(session.getAccessToken());
                    registerFacebookAccountRequest.setDeviceId(androidId);
                    registerFacebookAccountRequest.setDeviceTypeRef(deviceTypeRef);
                    registerFacebookAccountRequest.setUserExternalId(MobileSessionHelper.getLoggedInUser(context).getExternalId());

                    RegisterFacebookUserRequest registerFacebookUserRequest;
                    try {
                        registerFacebookUserRequest = new RegisterFacebookUserRequest(createErrorListener(context), createSuccessListener(context), registerFacebookAccountRequest);
                        RequestQueue mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
                        registerFacebookUserRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
                        mRequestQueue.add(registerFacebookUserRequest);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, com.facebook.Response response) {
                            if (user != null) {
                                Toast toast = Toast.makeText(context, "Hello " + user.getName() + "!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    private static Response.ErrorListener createErrorListener(final Activity context) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                if (null != error) {
                    Log.e("eswaraj", "error : " + error.getMessage(), error);
                }
            }
        };
    }

    private static Response.Listener<String> createSuccessListener(final Activity context) {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("details", "response: " + response);
                MobileSessionHelper.setLoggedInUser(context, response);
            }
        };
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Session.getActiveSession().onActivityResult(activity, requestCode, resultCode, data);
    }
}
