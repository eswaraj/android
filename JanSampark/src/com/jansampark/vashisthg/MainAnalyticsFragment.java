package com.jansampark.vashisthg;

import org.achartengine.GraphicalView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.internal.c;
import com.jansampark.vashisthg.widget.PieChartView;

public class MainAnalyticsFragment extends Fragment {
	FrameLayout pieChartHolder;
	TextView issueNumTV;
	TextView complaintsNumTV;
	
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
		setViews();
		
	}	
	
	private void setViews() {		
		
		setPieChart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(getTag(), "onResume");
		setCounts();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		Log.d(getTag(), "onHiddenChanged");
	}
	
	
	@Override
	public void onPause() {
		Log.d(getTag(), "onPause");
		super.onPause();
	}
	
	private void setCounts() {
		issueNumTV = (TextView) getActivity().findViewById(R.id.issue_num);
		complaintsNumTV = (TextView) getActivity().findViewById(R.id.complaint_num);
		if(null != issueCounter) {
			issueCounter.cancel();
		}
		issueCounter = MyCount.newInstance(10, issueNumTV);
		issueCounter.start();
		
		
		if(null != complaintCounter) {
			complaintCounter.cancel();
		}
		complaintCounter = MyCount.newInstance(245, complaintsNumTV);
		complaintCounter.start();
	}
	
	MyCount issueCounter;
	MyCount complaintCounter;
	
	
	
	 public void  setPieChart(){
		 pieChartHolder = (FrameLayout) getActivity().findViewById(R.id.pie_chart_holder);
         int values[] = new int[]{12,23,23,23,23,2};         
         GraphicalView chartView = PieChartView.getNewInstance(getActivity(), values);
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
			 return new MyCount(millisInFuture, countDownInterval, number, textView);
		 }
	
        private MyCount(long millisInFuture, long countDownInterval, int number, TextView textView) {
            super(millisInFuture, countDownInterval);
            this.number = number;
            this.textView = textView;
        }

        @Override
        public void onFinish() {
        	textView.setText("" + number);
            //complaintsNumTV.setText("" + number);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        	if(!(counter > number) ) {
        		textView.setText("" + counter++);
    
        	}
        }
	 }
	 
	 public void onFragmentShown() {
		 setCounts();
	 }
	
}
