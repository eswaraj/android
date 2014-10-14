package com.next.eswaraj.util;

import android.app.Activity;
import android.provider.Settings.Secure;

public class DeviceUtil {

    public static String getDeviceid(Activity activity) {
        String android_id = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    public static String getDeviceTypeRef() {
        return "Android";
    }

}
