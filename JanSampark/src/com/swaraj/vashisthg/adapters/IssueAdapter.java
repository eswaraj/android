package com.swaraj.vashisthg.adapters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swaraj.vashisthg.R;
import com.swaraj.vashisthg.helpers.IssueFactory;
import com.swaraj.vashisthg.models.Analytics;
import com.swaraj.vashisthg.models.ISSUE_CATEGORY;
import com.swaraj.vashisthg.models.IssueItem;


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
		setComplaint(holder, item, position);
		
		
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
