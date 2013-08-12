package com.next.eswaraj.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.next.eswaraj.R;
import com.next.eswaraj.models.Constituency;

public class LocationAutoCompleteAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater inflater;
	private List<Constituency> locations;
	private List<Constituency> masterLocationList;
	private String searchStr;

	public LocationAutoCompleteAdapter(Context context, List<Constituency> locations) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		masterLocationList = locations;
		this.locations = new ArrayList<Constituency>(masterLocationList);
	}

	@Override
	public int getCount() {
		if (null != locations) {
			return locations.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return locations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return locations.get(position).getID();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv;
		if(null == convertView) {
			convertView = inflater.inflate(R.layout.autocomplete_list_row, parent, false);
			tv = (TextView) convertView.findViewById(R.id.autocomplete_list_row);
			convertView.setTag(tv);
		} else {
			tv = (TextView) convertView.getTag();
		}
		String name = locations.get(position).getName();
		SpannableString text = new SpannableString(name);
		if(null != searchStr) {
			int index = name.toLowerCase(Locale.US).indexOf(searchStr.toLowerCase(Locale.US));
			if(index > -1) {
				text.setSpan(new StyleSpan(Typeface.BOLD), index, index + searchStr.length(), 0);
			}
			tv.setText(text, BufferType.SPANNABLE);
		} else {
			tv.setText("");
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	private Filter filter = new Filter() {

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			@SuppressWarnings("unchecked")
			List<Constituency> values = (List<Constituency>) results.values;
			locations = values;
			notifyDataSetChanged();
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			List<Constituency> filteredList = new ArrayList<Constituency>(masterLocationList);
			searchStr = TextUtils.isEmpty(constraint) ? "" : constraint.toString().toLowerCase(Locale.US);
			for(Constituency loc : masterLocationList) {
				if(!loc.getName().toLowerCase(Locale.US).contains(searchStr)) {
					filteredList.remove(loc);
				}
			}
			results.values = filteredList;
			return results;
		}
	};
}
