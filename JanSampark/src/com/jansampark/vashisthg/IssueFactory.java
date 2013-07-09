package com.jansampark.vashisthg;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

public class IssueFactory {
	
	public static List<IssueItem> getIssuesFor(Context context, ISSUES issue)  {
		List<IssueItem> issueItems = new ArrayList<IssueItem>();
		Resources resources = context.getResources();
		
		
		
		String[] issues;
		int[] type;
		
		
		switch (issue) {
		case WATER:
			issues = resources.getStringArray(R.array.water);
		    type = resources.getIntArray(R.array.water_index);
			break;
		case ELECTRICITY:
			issues = resources.getStringArray(R.array.electricity);
		    type = resources.getIntArray(R.array.electricity_index);
			break;
		case LAW:
			issues = resources.getStringArray(R.array.lawandorder);
		    type = resources.getIntArray(R.array.lawandorder_index);
			break;
		case ROAD:
			issues = resources.getStringArray(R.array.road);
		    type = resources.getIntArray(R.array.road_index);
			break;
		case SEWAGE:
			issues = resources.getStringArray(R.array.sewage);
		    type = resources.getIntArray(R.array.sewage_index);
			break;
		case TRANSPORT:
			issues = resources.getStringArray(R.array.transportation);
		    type = resources.getIntArray(R.array.transportation_index);
			break;

		default:
			throw new IllegalArgumentException("wrong issue specified");
		}
		
		int issueId = getIssueId(context, issue);
		
		for(int i = 0; i < issues.length; i++) {
			IssueItem item = new IssueItem();
			item.setIssueName(issues[i]);						
			item.setTemplateId(type[i]);
			item.setIssueId(issueId);
			issueItems.add(item);
		}						
		return issueItems;
	}	
	
	public static String getIssueTypeString(Context context, int templateId) {
		int type = templateId/10;		
		String[] typeArray = context.getResources().getStringArray(R.array.infra);		
		return typeArray[type];
	}
	
	public static int getIssueTypeColor(Context context, int templateId) {
		int type = templateId/10;
		int color;
		switch (type) {
		case 0:
			color = Color.RED;
			break;
		case 1:
			color = Color.YELLOW;
			break;
		case 2:
			color = Color.BLUE;
			break;
		case 4:
			color = Color.MAGENTA;
			break;
		case 5:
			color = Color.GREEN;
			break;
		case 6:
		default:
			color = Color.BLACK;
			break;						
		}
		return color;		
	}
	
	public static int getIssueId(Context context, ISSUES issue) {
		int issueId;
		Resources resources = context.getResources();
		switch (issue) {
		case WATER:
			issueId = resources.getInteger(R.integer.water);
			break;
		case ELECTRICITY:
			issueId = resources.getInteger(R.integer.electricity);			
			break;
		case LAW:
			issueId = resources.getInteger(R.integer.lawandorder);
			break;
		case ROAD:
			issueId = resources.getInteger(R.integer.road);
			break;
		case SEWAGE:
			issueId = resources.getInteger(R.integer.sewage);
			break;
		case TRANSPORT:
			issueId = resources.getInteger(R.integer.transportation);
			break;

		default:
			throw new IllegalArgumentException("wrong issue specified");
		}
		return issueId;
	}
	
	
}
