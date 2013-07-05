package com.jansampark.vashisthg.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomSupportMapFragment extends SupportMapFragment {

	View view = null;
	Context mContext = null;

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)     {
	    super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setMapTransparent((ViewGroup) view);
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	private void setMapTransparent(ViewGroup group) {
		// Handle redraw issues with v2 maps for <4.1 devices
		int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup) {
				setMapTransparent((ViewGroup) child);
			} else if (child instanceof SurfaceView) {
				child.setBackgroundColor(0x00000000);
			}
		}
	}
}