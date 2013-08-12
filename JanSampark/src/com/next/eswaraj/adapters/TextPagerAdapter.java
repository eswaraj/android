package com.next.eswaraj.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.next.eswaraj.R;
import com.next.eswaraj.TextPagerFragment;

public class TextPagerAdapter extends FragmentPagerAdapter {
	
	LayoutInflater inflater;
	View view;
	String[] splashText;
	private final int mSize;
	
	
	public TextPagerAdapter(FragmentManager fragmentManager, Context context, int size) {
		super(fragmentManager);
		mSize = size;		
		splashText = context.getResources().getStringArray(R.array.splash_text);
	}

	@Override
	public int getCount() {
		return mSize;
	}

	@Override
	public Fragment getItem(int position) {			
		return TextPagerFragment.newInstance(splashText[position]);
	}		
}
