package com.jansampark.vashisthg;

import java.util.ArrayList;
import java.util.List;

import com.saulpower.piechart.adapter.PieChartAdapter;
import com.saulpower.piechart.extra.FrictionDynamics;
import com.saulpower.piechart.views.PieChartView;
import com.saulpower.piechart.views.PieChartView.PieChartAnchor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainAnalyticsFragment extends Fragment {
	
	private PieChartView pieChart;
	
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
		List<Float> slices = new ArrayList<Float>();
		
		slices.add(0.20f);
		slices.add(0.20f);
		slices.add(0.20f);
		slices.add(0.20f);
		slices.add(0.10f);
		slices.add(0.10f);
		PieChartAdapter adapter = new PieChartAdapter(getActivity(), slices);
		
		pieChart = (PieChartView) getActivity().findViewById(R.id.chart);
		pieChart.setDynamics(new FrictionDynamics(0.95f));
		pieChart.setSnapToAnchor(PieChartAnchor.BOTTOM);
		pieChart.setAdapter(adapter);
		pieChart.onResume();
	}
	
}
