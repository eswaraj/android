package com.next.eswaraj.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.R;
import com.next.eswaraj.models.Analytics;
import com.next.eswaraj.models.ISSUE_CATEGORY;
import com.next.eswaraj.models.IssueItem;


public class IssueAdapter extends BaseAdapter {
	
	static class ViewHolder {

		TextView title;
		TextView type;
		TextView percentage;
		TextView complaints;
		View colorView;
	}
	
	private List<IssueItem> issueItems;
	private Context context;
	private LayoutInflater inflater;
	private int layoutId;
	
	private Map<Integer, Integer> templateCount;
	private int totalComplaints;
	
	private List<CategoryWithChildCategoryDto> categories;
	
	
	
	public static IssueAdapter newInstance(Context context, ISSUE_CATEGORY issue, int layoutId, List<Analytics> analyticsList, List<CategoryWithChildCategoryDto> categories) {
		IssueAdapter adapter = new IssueAdapter();
		/*
		adapter.issueItems = IssueFactory.getIssuesFor(context, issue);
		*/
		adapter.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter.context = context;
		adapter.layoutId = layoutId;
		if(categories == null){
			categories = new ArrayList<CategoryWithChildCategoryDto>();
		}
		adapter.categories = categories;
		
		adapter.templateCount = new HashMap<Integer, Integer>();
		/*
		if(null != analyticsList) {
			for (Analytics analytics : analyticsList) {
				if(analytics.getTemplateId() % 10 == 0) {
					if(adapter.templateCount.containsKey(Integer.valueOf(0))) {
						int oldCount = adapter.templateCount.get(0);
						adapter.templateCount.put(0, analytics.getCount() + oldCount);
					} else {
						adapter.templateCount.put(0, analytics.getCount());
					}
				} else {
					adapter.templateCount.put(analytics.getTemplateId(), analytics.getCount());
				}
				
			}
			adapter.totalComplaints = adapter.getTotalComplaints();
		}
		*/
		
		return adapter;		
	}
	
	
	
	

	
	
	private IssueAdapter() {
		
	}

	
	
	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int position) {		
		return categories.get(position);
	}

	@Override
	public long getItemId(int position) {
		return categories.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView) {
			convertView = inflater.inflate(layoutId, parent, false);
			holder = new ViewHolder();
			
			holder.title = (TextView) convertView.findViewById(R.id.issue_row_title);
			holder.type = (TextView) convertView.findViewById(R.id.issue_row_type);
			holder.colorView = convertView.findViewById(R.id.issue_row_color);
			holder.percentage = (TextView) convertView.findViewById(R.id.issue_row_percentage);
			holder.complaints = (TextView) convertView.findViewById(R.id.issue_row_complaints);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		CategoryWithChildCategoryDto item = (CategoryWithChildCategoryDto)getItem(position);
		
		holder.title.setText(item.getName());
		holder.type.setText("");
		int color =  Color.RED;// IssueFactory.getIssueTypeColor(context, 1);
		
		holder.type.setTextColor(color);
		holder.colorView.setBackgroundColor(color);
		//setComplaint(holder, item, position);
		
		
		return convertView;
	}	
	
	private void setComplaint(ViewHolder holder, IssueItem item, int position) {
		int complaintCount = 0;
		int complaintPercentage = 0;
		if(null != holder.percentage) {
			if(null != templateCount) {				
				if(templateCount.containsKey(item.getTemplateId())) {
					complaintCount = getIndividualComplaintCount(item, position);
				}
				
				holder.complaints.setText(complaintCount + " complaints");
				if(0  != totalComplaints) {
					complaintPercentage = (complaintCount  * 100) / totalComplaints;
				}
				holder.percentage.setText(complaintPercentage + "%");	
				
			}			
		}
	}
	
	private int getTotalComplaints() {
		int counter = 0;
		Iterator<Entry<Integer, Integer>> it = templateCount.entrySet().iterator();
		
	    while (it.hasNext()) {
	        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>)it.next();
	        counter += pairs.getValue();
	    }
		return counter;
	}
	
	private int getIndividualComplaintCount(IssueItem item,int position) {
		return templateCount.get(item.getTemplateId());
	}
	
	
}
