package com.swaraj.vashisthg.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class Utils {
	public static final String SHARED_PREFS = "shared_pref";
	public static final String USER_IMAGE = "image";
	
	public static String cachedUserImage;
	
	
	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static void hideKeyboard(Context context, TextView textView) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService( Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
	}
	
	public static void showKeyboard(Context context, TextView textView) {		
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public static String getUniqueImageFilename() {
		return String.valueOf(Calendar.getInstance().getTimeInMillis());
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {		
		String[] proj = { MediaStore.Images.Media.DATA };
		try {
			CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
			Cursor cursor = loader.loadInBackground();
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			if (contentUri.toString().startsWith("content://com.google.android.gallery3d")){
				return getImage(context, getUniqueImageFilename(), contentUri);
			} else { // it is a regular local image file			
				return cursor.getString(columnIndex);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		String path = contentUri.getPath();
		return path;
	}

	private static String getImage(Context context, String tag, Uri url) {		
		File f = createImageFileInCache(context, tag);
		try {
			InputStream imageStream = null;			
			imageStream = context.getContentResolver().openInputStream(url);			
			OutputStream out = new FileOutputStream(f);
			byte buf[] = new byte[1024];
			int len;
			while ((len = imageStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			imageStream.close();
			return f.getPath();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static File createImageFileInCache(Context context, String tag) {
		File cacheDir;
		if (isExternalStorageUsable()) {
			cacheDir = context.getExternalCacheDir();
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists())
			cacheDir.mkdirs();

		File f = new File(cacheDir, tag);
		return f;
	}

	public static boolean isExternalStorageUsable() {
		boolean isExternalStorageAvailable = false;
		boolean isExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    isExternalStorageAvailable = isExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    isExternalStorageAvailable = true;
		    isExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    isExternalStorageAvailable = isExternalStorageWriteable = false;
		}

		return isExternalStorageAvailable && isExternalStorageWriteable;
	}

	
	
	public static String getUserImage(Context context) {
		 SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, 0);
		 if(null == cachedUserImage) {
			 cachedUserImage = settings.getString(USER_IMAGE, "");		
		 }
		 return cachedUserImage;
	}
	
	public static void setUserImage(Context context, String string) {
		if(null != string) {
			SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString(USER_IMAGE, string);
		    editor.commit();
		    cachedUserImage = string;
		}
	}
	
	public static void removeUserImage(Context context) {
		SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, 0);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.remove(USER_IMAGE);
	    editor.commit();
	    cachedUserImage = null;
	}
	
	public static boolean isFirstTimeBoot(Context context) {
		 SharedPreferences settings = context.getSharedPreferences(SHARED_PREFS, 0);
	     boolean isNotFirst = settings.getBoolean("isNotFirst", false);
	     
	     if(!isNotFirst) {
	    	SharedPreferences.Editor editor = settings.edit();
	 	    editor.putBoolean("isNotFirst", true);
	 	    editor.commit();
	     }
	     
	     return !isNotFirst;
	}
	
	
}
