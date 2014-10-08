package com.next.eswaraj.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.eswaraj.web.dto.UserDto;
import com.google.gson.Gson;
import com.next.eswaraj.config.Preferences;

public class MobileSessionHelper {

    private static final String USER_FILE_NAME = "user";
    public static UserDto getLoggedInUser(Context context) {
        UserDto userDto = null;
        SharedPreferences sharedPref = context.getSharedPreferences(USER_FILE_NAME, Context.MODE_PRIVATE);
        String userJson = sharedPref.getString(Preferences.LOGGED_IN_USER, null);
        if (userJson != null) {
            try {
                userDto = new Gson().fromJson(userJson, UserDto.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return userDto;
    }

    public static void setLoggedInUser(Context context, UserDto userDto) {
        SharedPreferences sharedPref = context.getSharedPreferences(USER_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Preferences.LOGGED_IN_USER, new Gson().toJson(userDto));
        editor.commit();
    }

    public static void setLoggedInUser(Context context, String userJson) {
        SharedPreferences sharedPref = context.getSharedPreferences(USER_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Preferences.LOGGED_IN_USER, userJson);
        editor.commit();
    }
}
