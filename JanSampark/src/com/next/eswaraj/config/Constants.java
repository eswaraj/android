package com.next.eswaraj.config;

public class Constants {

    public static final String BASE_URL = "http://dev.api.eswaraj.com/api/v0";
    public static final String URL_POST_COMPLAINT = BASE_URL + "/complaint";
    public static final String URL_POST_SAVE_DEVICE_ANONYMOUS_USER = BASE_URL + "/user/device";
    public static final String URL_POST_SAVE_FACEBOOK_USER = BASE_URL + "/user/facebook";
    public static final String URL_POST_SAVE_GCM_ID = BASE_URL + "/user/device/gcm";

	public static final int DROPBIT_OK = 0;
	public static final int DROPBIT_INVALID_CONSTITUENCY = 1;
	
	public static final int BANNER_SHOW = 1;
	public static final int BANNER_HIDE = 0;
}
