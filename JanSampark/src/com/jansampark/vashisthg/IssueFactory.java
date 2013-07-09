package com.jansampark.vashisthg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

public class IssueFactory {
	
	public static class IssueItem {
		private String issueName;
//		private Type issueType;
		private int issueId;
		public String getIssueName() {
			return issueName;
		}
		public void setIssueName(String issueName) {
			this.issueName = issueName;
		}
//		public Type getType() {
//			return issueType;
//		}
//		public void setType(Type type) {
//			this.issueType = type;
//		}
		public int getIssueId() {
			return issueId;
		}
		public void setIssueId(int issueId) {
			this.issueId = issueId;
		}
	
	}
	
	
	
	
	public static List<IssueItem> getIssuesFor(Context context, ISSUES issue)  {
		List<IssueItem> issueItems = new ArrayList<IssueFactory.IssueItem>();
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
		
		
		for(int i = 0; i < issues.length; i++) {
			IssueItem item = new IssueItem();
			item.issueName = issues[i];						
			item.issueId = type[i];
			issueItems.add(item);
		}						
		return issueItems;
	}
	
	public static Type getIssueType(int index) {
		int type = index/10;
		Type issueType;
		switch (type) {
		case 0:
			issueType = Type.LACK_IN_INFRA;
			break;
		case 1:
			issueType = Type.LACK_IN_MAIN;
			break;
		case 2:
			issueType = Type.LACK_OF_QUALITY_STAFF;
			break;
		case 4:
			issueType = Type.POOR_PRICING;
			break;
		case 5:
			issueType = Type.AWARENESS;
			break;
		case 6:
		default:
			issueType = Type.OTHERS;
			break;						
		}
		return issueType;
	}
	
	public static String getIssueTypeString(Context context,int issueId) {
		int type = issueId/10;		
		String[] typeArray = context.getResources().getStringArray(R.array.infra);		
		return typeArray[type];
	}
	
	public static int getIssueTypeColor(Context context, int issueId) {
		int type = issueId/10;
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
	
}
