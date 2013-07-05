package com.jansampark.vashisthg;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jansampark.vashisthg.widget.DrawPieChart;

public class MainAnalyticsFragment extends Fragment {
	RelativeLayout la;
	DrawPieChart piView;
        int[] vals;

	
	public static MainAnalyticsFragment newInstance(Bundle args) {
		return new MainAnalyticsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_analytics, container, false);		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setPieChart();
	}
	
	
	private void setPieChart() {
		la = (RelativeLayout) getActivity().findViewById(R.id.piechart_lay);
        getPieChart();
	
	}
	
	 public void  getPieChart(){
//         vals[] = new vals[7];
         vals = new int[]{12,14,23,35,23,56};
         piView = new DrawPieChart(getActivity(), vals);
         
         la.removeAllViews();
     la.addView(piView); 
   }
	
}
