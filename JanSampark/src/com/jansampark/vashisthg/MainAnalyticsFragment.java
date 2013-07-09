package com.jansampark.vashisthg;

import org.achartengine.GraphicalView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jansampark.vashisthg.widget.PieChartView;

public class MainAnalyticsFragment extends Fragment {
	FrameLayout pieChartHolder;
	TextView issueNumTV;
	TextView complaintsNumTV;
	CompoundButton overallButton;
	MyCount issueCounter;
	MyCount complaintCounter;
	//Spinner spinner;

	int[] vals;

	public static MainAnalyticsFragment newInstance(Bundle args) {
		return new MainAnalyticsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_analytics, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setViews();
	}

	private void setViews() {
		setButtons();
		setPieChart();
		//setLocationSpinner();
	}

	@Override
	public void onResume() {
		super.onResume();
		setCounts();
	}


	private void setCounts() {
		issueNumTV = (TextView) getActivity().findViewById(R.id.issue_num);
		complaintsNumTV = (TextView) getActivity().findViewById(
				R.id.complaint_num);
		if (null != issueCounter) {
			issueCounter.cancel();
		}
		issueCounter = MyCount.newInstance(10, issueNumTV);
		issueCounter.start();

		if (null != complaintCounter) {
			complaintCounter.cancel();
		}
		complaintCounter = MyCount.newInstance(245, complaintsNumTV);
		complaintCounter.start();
	}



	public void setPieChart() {
		pieChartHolder = (FrameLayout) getActivity().findViewById(
				R.id.pie_chart_holder);
		int values[] = new int[] { 12, 23, 23, 23, 23, 2 };
		GraphicalView chartView = PieChartView.getNewInstance(getActivity(),
				values);
		chartView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		pieChartHolder.addView(chartView);
	}

	public static class MyCount extends CountDownTimer {
		int number;
		int counter = 0;
		TextView textView;

		public static MyCount newInstance(int number, TextView textView) {

			long countDownInterval = 40;
			long millisInFuture = number * countDownInterval;
			return new MyCount(millisInFuture, countDownInterval, number,
					textView);
		}

		private MyCount(long millisInFuture, long countDownInterval,
				int number, TextView textView) {
			super(millisInFuture, countDownInterval);
			this.number = number;
			this.textView = textView;
		}

		@Override
		public void onFinish() {
			textView.setText("" + number);
			// complaintsNumTV.setText("" + number);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (!(counter > number)) {
				textView.setText("" + counter++);

			}
		}
	}

	public void onFragmentShown() {
		setCounts();
	}

	private void setButtons() {
		overallButton = (CompoundButton) getActivity().findViewById(
				R.id.analytics_overall);
	}

//	private void setLocationSpinner() {
//		spinner = (Spinner) getActivity().findViewById(
//				R.id.analytics_spinner);
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//				getActivity(), R.array.demo_place,
//				android.R.layout.simple_spinner_item);
//		// Specify the layout to use when the list of choices appears
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		// Apply the adapter to the spinner
//		spinner.setAdapter(adapter);
//		setLocationSpinnerListener();
//		
//	}
//	
//	private void setLocationSpinnerListener() {
//		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				overallButton.setChecked(false);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//
//		});
//	}

}
