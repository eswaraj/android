package com.jansampark.vashisthg;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jansampark.vashisthg.IssueFactory.IssueItem;

public class IssueAdapter extends BaseAdapter {
	
	static class ViewHolder {

		TextView title;
		TextView type;
		View colorView;
	}
	
	public static IssueAdapter newInstance(Context context, ISSUES issue) {
		IssueAdapter adapter = new IssueAdapter();
		adapter.issueItems = IssueFactory.getIssuesFor(context, issue);
		adapter.context = context;
		adapter.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return adapter;		
	}
	
	
	private List<IssueItem> issueItems;
	private Context context;
	private LayoutInflater inflater;
	
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
		return issueItems.get(position).getIssueId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView) {
			convertView = inflater.inflate(R.layout.issue_row, parent, false);
			holder = new ViewHolder();
			
			holder.title = (TextView) convertView.findViewById(R.id.issue_row_title);
			holder.type = (TextView) convertView.findViewById(R.id.issue_row_type);
			holder.colorView = convertView.findViewById(R.id.issue_row_color);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		IssueItem item = (IssueItem)getItem(position);
		
		holder.title.setText(item.getIssueName());
		holder.type.setText(IssueFactory.getIssueTypeString(context, item.getIssueId()));
		int color = IssueFactory.getIssueTypeColor(context, item.getIssueId());
		
		holder.type.setTextColor(color);
		holder.colorView.setBackgroundColor(color);
		
		return convertView;
	}

}
