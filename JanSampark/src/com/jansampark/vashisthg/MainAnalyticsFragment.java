package com.jansampark.vashisthg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.achartengine.GraphicalView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.jansampark.vashisthg.adapters.LocationAutoCompleteAdapter;
import com.jansampark.vashisthg.helpers.ConstuencyParserHelper;
import com.jansampark.vashisthg.helpers.DialogFactory;
import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.models.Analytics;
import com.jansampark.vashisthg.models.Constituency;
import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.volley.JsonRequestWithCache;
import com.jansampark.vashisthg.widget.IssueButton;
import com.jansampark.vashisthg.widget.PieChartView;

@SuppressLint("UseSparseArrays")
public class MainAnalyticsFragment extends Fragment {
	
	private static final String TAG = "Analytics";
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
	
	private RequestQueue mRequestQueue;
	
	Map<Integer, List<Analytics>> analyticsMap;
	
	private IssueButton roadButton;
	private IssueButton waterButton;
	private IssueButton transportationButton;
	private IssueButton electricityButton;
	private IssueButton lawButton;
	private IssueButton sewageButton;

	public static MainAnalyticsFragment newInstance(Bundle args) {
		return new MainAnalyticsFragment();
	}

	private void parseLocations() {
		try {
			locations = ConstuencyParserHelper.readLocations(getActivity());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
		analyticsMap = new HashMap<Integer, List<Analytics>>();
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
		initButtonListeners();
	}
	private void initButtonListeners() {
		electricityButton.setOnClickListener(buttonListener);		
		lawButton.setOnClickListener(buttonListener);
		roadButton.setOnClickListener(buttonListener);
		sewageButton.setOnClickListener(buttonListener);
		transportationButton.setOnClickListener(buttonListener);
		waterButton.setOnClickListener(buttonListener);
	}
	
	boolean isResumed;
	
	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;
		setCounts(issueCount, complaintCount);
	}

	@Override
	public void onPause() {
		isResumed = false;
		super.onPause();
	}

	private void setCounts(int issueCount, int complaintCount) {
		issueNumTV = (TextView) getActivity().findViewById(R.id.issue_num);
		complaintsNumTV = (TextView) getActivity().findViewById(
				R.id.complaint_num);
		if (null != issueCounter) {
			issueCounter.cancel();
		}
		issueCounter = MyCount.newInstance(issueCount, issueNumTV);
		issueCounter.start();

		if (null != complaintCounter) {
			complaintCounter.cancel();
		}
		complaintCounter = MyCount.newInstance(complaintCount, complaintsNumTV);
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

	public void onFragmentShown() {
		setCounts(issueCount, complaintCount);
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
		
		
		electricityButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_electricity);
		roadButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_road);
		waterButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_water);
		transportationButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_transportation);
		lawButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_law);
		sewageButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_sewage);
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
				fetchAnalytics(loc);
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
		intent.putExtra(IssueActivity.EXTRA_IS_ANALYTICS, true);
		startActivity(intent);
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
	
	private void fetchAnalytics(Constituency constituency) {
		
		executeMLAIdRequest(constituency.getLatLong());
	}
	
	private void executeMLAIdRequest(LatLng latlng)  {	
		double lat = latlng.latitude;
		double lon = latlng.longitude;
		String url = "http://50.57.224.47/html/dev/micronews/getmlaid.php?lat=" +lat + "&long=" + lon;		
		JsonRequestWithCache request = new JsonRequestWithCache(Method.GET, url, null, createMLAIDReqSuccessListener(), createMyReqErrorListener());
	
		mRequestQueue.add(request);
		DialogFactory.showPleaseWaitProgressDialog(getActivity());
	}
	
	
	
	 private Response.ErrorListener createMyReqErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {
	            	if(isResumed) {
	            		Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
	            		DialogFactory.hideProgressDialog();
	            	}
	            }
	        };
	  }
	 
	private Response.Listener<JSONObject> createMLAIDReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {
            		Log.d(TAG, jsonObject.toString(2));
					String mlaId = jsonObject.getString("consti_id");
					executeAnalyticsRequest( mlaId);
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }
        };
    }
	
	private void executeAnalyticsRequest(String mlaId) {
		String url = "http://50.57.224.47/html/dev/micronews/get_summary.php?cid=" + mlaId + "&time_frame=1m";
		JsonRequestWithCache request = new JsonRequestWithCache(Method.GET, url, null, createMLADetailsReqSuccessListener(), createMyReqErrorListener());
		mRequestQueue.add(request);		
	}
	
	String MLAName;
	String MLAPic;
	
	
	private Response.Listener<JSONObject> createMLADetailsReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {
            		
            		if(isResumed) {
						Log.d(TAG, jsonObject.toString(2));
						Iterator<String> iter = jsonObject.keys();
						while(iter.hasNext()) {
							String key = iter.next().toString();
							int keyInt = Integer.parseInt(key);
							JSONArray array = jsonObject.getJSONArray(key);
							List<Analytics> analyticsList = new ArrayList<Analytics>();
							
							
							for(int i = 0; i < array.length(); i++) {
								Analytics analytics = new Analytics();
								analytics.setIssueCategory(keyInt);
								JSONObject itemObject = array.getJSONObject(i);
								analytics.setTemplateId(itemObject.getInt("template_id"));
								analytics.setCount(itemObject.getInt("counter"));
								analyticsList.add(analytics);
							}
							
							Log.d(TAG, "analytics, array length: " + analyticsList.size());
							analyticsMap.put(keyInt, analyticsList);
							setViewsAccordingToAnalytics();
						}
            		}
            		DialogFactory.hideProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }
        };
    }
	
	int complaintCount = 0;
	int issueCount = 0;
	
	private void setViewsAccordingToAnalytics() {
		setTotalComplaintCount();
	    setIssueCount(waterButton, R.integer.water);
	    setIssueCount(sewageButton, R.integer.sewage);
	    setIssueCount(lawButton, R.integer.sewage);
	    setIssueCount(roadButton, R.integer.road);
	    setIssueCount(electricityButton, R.integer.electricity);
	    setIssueCount(transportationButton, R.integer.transportation);	    
	}
	
	private void setTotalComplaintCount() {
		int totalCount = 0;
		Iterator<Entry<Integer, List<Analytics>>> it = analyticsMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, List<Analytics>>  pairs = (Map.Entry<Integer, List<Analytics>>)it.next();
	        System.out.println(pairs.getKey() + " = " + pairs.getValue());
	        for (Analytics analytics : pairs.getValue()) {
				totalCount += analytics.getCount();
			}	        
	    }
	    complaintCount = totalCount;
	    setCounts(issueCount, complaintCount);
	}
	
	private void setIssueCount(IssueButton issueButton, int categoryResId) {
		try {		
			int waterPercentage = 0;
			int waterCount = 0;
			Resources resources = getActivity().getResources();
			Integer waterIssueCategory = resources.getInteger(categoryResId);
			if(analyticsMap.containsKey(waterIssueCategory)) {			
				waterCount = analyticsMap.get(waterIssueCategory).size();
			}
			waterPercentage = waterCount/complaintCount;
			issueButton.setPercentage(waterPercentage );
		} catch (Exception e) {
			issueButton.setPercentage(0);
			e.printStackTrace();
		}
	}
	
}
