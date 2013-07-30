package com.jansampark.vashisthg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jansampark.vashisthg.models.Analytics;
import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.models.IssueItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;


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
	
	
	
	public static IssueAdapter newInstance(Context context, ISSUE_CATEGORY issue, int layoutId, List<Analytics> analyticsList) {
		IssueAdapter adapter = new IssueAdapter();
		adapter.issueItems = IssueFactory.getIssuesFor(context, issue);
		adapter.context = context;
		adapter.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter.layoutId = layoutId;
		
		adapter.templateCount = new HashMap<Integer, Integer>();
		
		if(null != analyticsList) {
			for (Analytics analytics : analyticsList) {
				adapter.templateCount.put(analytics.getTemplateId(), analytics.getCount());
			}
			adapter.totalComplaints = adapter.getTotalComplaints();
		}
		
		return adapter;		
	}
	
	

	
	
	private IssueAdapter() {
		
	}

	
	
	@Override
	public int getCount() {
		return issueItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return issueItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return issueItems.get(position).getTemplateId();
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
		
		IssueItem item = (IssueItem)getItem(position);
		
		holder.title.setText(item.getIssueName());
		holder.type.setText(IssueFactory.getIssueTypeString(context, item.getTemplateId()));
		int color = IssueFactory.getIssueTypeColor(context, item.getTemplateId());
		
		holder.type.setTextColor(color);
		holder.colorView.setBackgroundColor(color);
		
		int complaintCount = 0;
		int complaintPercentage = 0;
		if(null != holder.percentage) {
			if(null != templateCount) {
				if(templateCount.containsKey(item.getTemplateId())) {
					complaintCount = templateCount.get(item.getTemplateId());
				}
				
				holder.complaints.setText(complaintCount + " complaints");
				if(0  != totalComplaints) {
					complaintPercentage = (complaintCount  * 100) / totalComplaints;
				}
				holder.percentage.setText(complaintPercentage + "%");			 
			}			
		}
		
		return convertView;
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
}
