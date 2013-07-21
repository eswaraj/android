package com.jansampark.vashisthg;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextPagerAdapter extends PagerAdapter {
	
	private final int COUNT = 3;
	LayoutInflater inflater;
	View view;
	String[] splashText;
	
	
	public TextPagerAdapter(Context context) {
		inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		splashText = context.getResources().getStringArray(R.array.splash_text);
	}

	@Override
	public int getCount() {
		return COUNT;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		 return view.equals(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		view = inflater.inflate(R.layout.splash_pager_text, container, false);
		((TextView)view.findViewById(R.id.splash_text)).setText(splashText[position]);
		return view;
	}
}
