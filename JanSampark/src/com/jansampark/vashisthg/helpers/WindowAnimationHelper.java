package com.jansampark.vashisthg.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.jansampark.vashisthg.R;

public class WindowAnimationHelper {
	
	public static void startActivityWithSlideFromRight(Activity activity, Class<?> cls) {				
		Intent subActivity = new Intent(activity, cls);
        Bundle translateBundle =ActivityOptionsCompat.makeCustomAnimation(activity,
                R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
        ActivityCompat.startActivity(activity, subActivity, translateBundle);
	}
	
	public static void startActivityWithSlideFromRight(Activity activity, Intent subActivity) {					
        Bundle translateBundle =ActivityOptionsCompat.makeCustomAnimation(activity,
                R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
        ActivityCompat.startActivity(activity, subActivity, translateBundle);
	}
	
	public static void startActivityWithScaleAnimation(Activity activity, Intent subActivity, View v) {
		
        Bundle scaleBundle = ActivityOptionsCompat.makeScaleUpAnimation(
                v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
        ActivityCompat.startActivity(activity, subActivity, scaleBundle);
	}
	
	
	
	public static void finish(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

}
