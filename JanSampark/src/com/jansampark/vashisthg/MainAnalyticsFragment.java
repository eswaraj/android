package com.jansampark.vashisthg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.GraphicalView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.jansampark.vashisthg.adapters.LocationAutoCompleteAdapter;
import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.models.Constituency;
import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.widget.PieChartView;

public class MainAnalyticsFragment extends Fragment {
	FrameLayout pieChartHolder;
	TextView issueNumTV;
	TextView complaintsNumTV;
	CompoundButton overallButton;
	CompoundButton autoCompleteButton;
	MyCount issueCounter;
	MyCount complaintCounter;
	
	private AutoCompleteTextView autoCompleteTextView;
	private View overlay;
	private List<Constituency> locations;
	private Constituency lastSelectedLocation;

	int[] vals;

	public static MainAnalyticsFragment newInstance(Bundle args) {
		return new MainAnalyticsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	private void parseLocations() {
		try {
			readLocations();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readLocations() throws IOException {
		InputStream inputStream = getActivity().getAssets().open("centroid.txt");
		BufferedReader f = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		locations = new ArrayList<Constituency>();
		while ((line = f.readLine()) != null) {
			String row[] = line.split(",");
			parseRow(row);
		}
	}

	private void parseRow(String[] row) {
		Constituency loc = new Constituency();
		loc.setName(row[0]);
		loc.setID(Integer.parseInt(row[1].trim()));
		loc.setLatLong(new LatLng(Double.parseDouble(row[2]), Double.parseDouble(row[3])));
		locations.add(loc);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_analytics, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setViews(view);
	}

	private void setViews(View view) {
		setButtons();
		setPieChart();
		setAutoComplete(view);
		//setLocationSpinner();
		initButtonListeners();
	}
	private void initButtonListeners() {
		getActivity().findViewById(R.id.main_analytics_electricity).setOnClickListener(buttonListener);		
		getActivity().findViewById(R.id.main_analytics_law).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_analytics_road).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_analytics_sewage).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_analytics_transportation).setOnClickListener(buttonListener);
		getActivity().findViewById(R.id.main_analytics_water).setOnClickListener(buttonListener);
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
		autoCompleteButton = (CompoundButton) getActivity().findViewById(R.id.analytics_spinner);
		autoCompleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onRadioClick();
			}

		});
	}
	
	private void onRadioClick() {
		if(autoCompleteTextView.getVisibility() == View.VISIBLE) {
			disableAutoComplete();
		} else {
			if(null != lastSelectedLocation) {
				autoCompleteTextView.setText(lastSelectedLocation.getName());
			}
			autoCompleteTextView.setVisibility(View.VISIBLE);
			overlay.setVisibility(View.VISIBLE);
			autoCompleteTextView.requestFocus();
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		}
		
	}

	private void disableAutoComplete() {        
        Utils.hideKeyboard(getActivity(), autoCompleteTextView);
		autoCompleteTextView.setVisibility(View.GONE);
		overlay.setVisibility(View.INVISIBLE);
	}
	
	private void setAutoComplete(View view) {
		parseLocations();
		autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.analytics_autocomplete);
		overlay = view.findViewById(R.id.analytics_overlay);
		overlay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				disableAutoComplete();
			}
		});
		LocationAutoCompleteAdapter adapter = new LocationAutoCompleteAdapter(getActivity(), locations);
		autoCompleteTextView.setAdapter(adapter);
		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				disableAutoComplete();
				Constituency loc = (Constituency) parent.getAdapter().getItem(position);
				setLocation(loc);
			}
		});
		
	}

	private void setLocation(Constituency loc) {
		lastSelectedLocation = loc;
		autoCompleteButton.setText(loc.getName());
	}
	
	public void onSewageClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.SEWAGE);
	}

	public void onTransportationClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.TRANSPORT);
	}

	public void onWaterClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.WATER);
	}

	public void onRoadClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.ROAD);
	}

	public void onElectricityClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.ELECTRICITY);
	}

	public void onLawAndOrderClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.LAW);
	}

	private void openIssueActivity(ISSUE_CATEGORY issue) {
		Intent intent = new Intent(getActivity(), IssueActivity.class);
		intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
		intent.putExtra(IssueActivity.EXTRA_LOCATION, getLastKnownLocation());
		intent.putExtra(IssueActivity.EXTRA_IS_ANALYTICS, true);
		startActivity(intent);
	}
	
	private Location getLastKnownLocation() {
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);  
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		return locationManager.getLastKnownLocation(locationProvider);
	}
	
	android.view.View.OnClickListener buttonListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {						
			int id = view.getId();
			
			switch (id) {
			case R.id.main_analytics_road:
				onRoadClick(view);
				break;
			case R.id.main_analytics_law:
				onLawAndOrderClick(view);
				break;
			case R.id.main_analytics_electricity:
				onElectricityClick(view);
				break;
			case R.id.main_analytics_sewage:
				onSewageClick(view);
				break;
			case R.id.main_analytics_transportation:
				onTransportationClick(view);
				break;
			case R.id.main_analytics_water:
				onWaterClick(view);
				break;

			default:
				break;
			}
		}
	};
	
	
}
