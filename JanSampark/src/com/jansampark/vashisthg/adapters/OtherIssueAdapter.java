package com.jansampark.vashisthg.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jansampark.vashisthg.R;

public class OtherIssueAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	static class ViewHolder {

		TextView title;
		TextView type;
		TextView percentage;
		TextView complaints;
		View colorView;
	}
	
	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(null == convertView) {
			convertView = inflater.inflate(R.layout.issue_row, parent, false);
			holder = new ViewHolder();
			
			
			holder.type = (TextView) convertView.findViewById(R.id.issue_row_type);
			holder.colorView = convertView.findViewById(R.id.issue_row_color);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}

}
