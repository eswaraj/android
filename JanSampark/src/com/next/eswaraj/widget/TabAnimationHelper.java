package com.next.eswaraj.widget;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class TabAnimationHelper {

	private static final int ANIMATIION_DURATION = 300;
	
	public static Animation inFromRightAnimation() {

	    Animation inFromRight = new TranslateAnimation(
	            Animation.RELATIVE_TO_PARENT, +1.0f,
	            Animation.RELATIVE_TO_PARENT, 0.0f,
	            Animation.RELATIVE_TO_PARENT, 0.0f,
	            Animation.RELATIVE_TO_PARENT, 0.0f);
	    inFromRight.setDuration(ANIMATIION_DURATION);
	    inFromRight.setInterpolator(new AccelerateInterpolator());
	    return inFromRight;
	}

	public static Animation outToLeftAnimation() {
	    Animation outtoLeft = new TranslateAnimation(
	            Animation.RELATIVE_TO_PARENT, 0.0f,
	            Animation.RELATIVE_TO_PARENT, -1.0f,
	            Animation.RELATIVE_TO_PARENT, 0.0f,
	            Animation.RELATIVE_TO_PARENT, 0.0f);
	    outtoLeft.setDuration(ANIMATIION_DURATION);
	    outtoLeft.setInterpolator(new AccelerateInterpolator());
	    return outtoLeft;
	}
	
}
