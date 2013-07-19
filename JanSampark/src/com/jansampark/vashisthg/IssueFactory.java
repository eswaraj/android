package com.jansampark.vashisthg;

import java.util.ArrayList;
import java.util.List;

import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.models.IssueItem;

import android.content.Context;
import android.content.res.Resources;

public class IssueFactory {
	
	
	
	public static List<IssueItem> getIssuesFor(Context context, ISSUE_CATEGORY issue)  {
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
			item.setIssueCategory(issueId);
			issueItems.add(item);
		}						
		return issueItems;
	}	
	
	public static String getIssueCategoryName(Context context, int issueCategoryId ) {
		Resources resources = context.getResources();
		String categoryName = "";
		
		if( issueCategoryId == resources.getInteger(R.integer.electricity) ) {
			categoryName = resources.getString(R.string.electricity);
		} else if (issueCategoryId == resources.getInteger(R.integer.lawandorder)) {
			categoryName = resources.getString(R.string.law);
		} else if (issueCategoryId == resources.getInteger(R.integer.water)) {
			categoryName = resources.getString(R.string.water);
		} else if (issueCategoryId == resources.getInteger(R.integer.sewage)) {
			categoryName = resources.getString(R.string.sewage);
		} else if (issueCategoryId == resources.getInteger(R.integer.transportation)) {
			categoryName = resources.getString(R.string.transportation);
		} else if(issueCategoryId == resources.getInteger(R.integer.road)) {
			categoryName = resources.getString(R.string.road);
		}
		
		return categoryName;		
	}	
	
	
	public static String getIssueTypeString(Context context, int templateId) {
		int type = getIssueTypeInt(templateId);		
		String[] typeArray = context.getResources().getStringArray(R.array.infra);		
		return typeArray[type];
	}
	
	
	private static int getIssueTypeInt(int templateId) {
		int type;
		if(templateId == 0) {
			type = 5;
		} else {
		    type = templateId/10;
		}				
		return type;
	}
	
	public static int getIssueTypeColor(Context context, int templateId) {
		int type = getIssueTypeInt(templateId);
		Resources resources = context.getResources();
		
		int color;
		switch (type) {
		case 0:
			color = resources.getColor(R.color.lack_of_infra);
			break;
		case 1:
			color = resources.getColor(R.color.lack_of_maintainence);
			break;
		case 2:
			color = resources.getColor(R.color.lack_of_quality);
			break;
		case 3:
			color = resources.getColor(R.color.poor_pricing);
			break;
		case 4:
			color = resources.getColor(R.color.awareness);
			break;
		case 5:
		default:
			color = resources.getColor(R.color.others);
			break;						
		}
		return color;		
	}
	
	public static int getIssueId(Context context, ISSUE_CATEGORY issue) {
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
