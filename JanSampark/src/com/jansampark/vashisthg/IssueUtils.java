package com.jansampark.vashisthg;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

public class IssueUtils {
	
	
	
    
    public static int getColorInt(Context context, ISSUES issue) {
    	Resources resources = context.getResources();
    	int color;
    	switch (issue) {
		case WATER:
			color = resources.getColor(R.color.water);
			break;
		case ELECTRICITY:
			color = resources.getColor(R.color.electricity);
			break;
		case LAW:
			color = resources.getColor(R.color.law);
			break;
		case ROAD:
			color = resources.getColor(R.color.road);
			break;
		case SEWAGE:
			color = resources.getColor(R.color.sewage);
			break;
		case TRANSPORT:
			color = resources.getColor(R.color.transportation);
			break;
		default:
			color = 0;
			break;
		}
    	return color;
    }
}
