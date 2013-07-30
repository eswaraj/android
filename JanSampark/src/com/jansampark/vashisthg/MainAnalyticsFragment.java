package com.jansampark.vashisthg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.achartengine.GraphicalView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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
	RadioButton overallButton;
	RadioButton autoCompleteButton;
	RadioGroup analyticsRadioGroup;
	Spinner overallSpinner;
	MyCount issueCounter;
	MyCount complaintCounter;
	
	private AutoCompleteTextView autoCompleteTextView;
	private View overlay;
	private List<Constituency> locations;
	private Constituency lastSelectedLocation;
	
	private int cityResId = -1;
	private int constituencyId = -1;
	
	private void setCityResId(int cityResId) {
		this.cityResId = cityResId;
	}
	
	public int getCityResId() {
		return cityResId;
	}
	

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
		initButtonListeners();
		autoCompleteTextView = (AutoCompleteTextView) getActivity().findViewById(R.id.analytics_autocomplete);
		overlay = getActivity().findViewById(R.id.analytics_overlay);
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
		issueNumTV.setText(issueCount + "");
		complaintsNumTV.setText(complaintCount + "");
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
	
	boolean autoCompleteCheck;

	private void setButtons() {
		
		overallButton = (RadioButton) getActivity().findViewById(
				R.id.analytics_overall);
		overallSpinner = (Spinner) getActivity().findViewById(
				R.id.analytics_overall_spinner);
		autoCompleteButton = (RadioButton) getActivity().findViewById(R.id.analytics_spinner);
		analyticsRadioGroup = (RadioGroup) ((RadioGroup) getActivity().findViewById(R.id.analytics_chooser_container));
		analyticsRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.analytics_overall:
					Log.d(TAG, "clicked on overall");
					fetchCityAnalytics();
					break;
					
				case R.id.analytics_spinner:
					autoCompleteCheck = true;
					Log.d(TAG, "clicked on autocomplete: ");
					break;

				default:
					break;
				}
			}
			
			
		});
		
		autoCompleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(autoCompleteCheck) {
					autoCompleteCheck = false;
					if( -1 == constituencyId) {
						executeCurrentMLAIdRequest();
					} else {
						executeAnalyticsRequest();
					}
					Log.d(TAG, "clicked on not already checked autocomplete: " + autoCompleteCheck);
				} else {
					onAutoCompleteRadioClick();
				}
				
			}
		});
		
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.city, R.layout.analytics_city_selector_bg);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
	    overallSpinner.setAdapter(adapter);
	    
	    overallSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				Log.d(TAG, "in onitemselectedListener");
				analyticsRadioGroup.check(R.id.analytics_overall);
				switch (position) {
				case 0:					
					setCityResId(R.integer.id_city_delhi);
					break;
				case 1:
					setCityResId(R.integer.id_city_bangalore);					
					break;

				default:
					break;
				}
				Log.d(TAG, "call setCurrentCity from onitemselectedListener");
				setCurrentCity();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				analyticsRadioGroup.check(R.id.analytics_overall);
				
			}
		});
		
		overallSpinner.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				
				if(R.id.analytics_overall == analyticsRadioGroup.getCheckedRadioButtonId()) {		
					switch (getCityResId()) {
					case R.integer.id_city_bangalore:
						overallSpinner.setSelection(1);
						break;
					case R.integer.id_city_delhi:
						overallSpinner.setSelection(0);						
						break;

					default:
						break;
					}
					return false;
				} else {
					analyticsRadioGroup.check(R.id.analytics_overall);								
					return true;
				}
			}
		});
		
		electricityButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_electricity);
		roadButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_road);
		waterButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_water);
		transportationButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_transportation);
		lawButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_law);
		sewageButton = (IssueButton) getActivity().findViewById(R.id.main_analytics_sewage);
	}
	
	private void onAutoCompleteRadioClick() {
		
		if(autoCompleteTextView.getVisibility() == View.VISIBLE) {
			disableAutoComplete();
		} else {
			overlay.setVisibility(View.VISIBLE);
			if(null != lastSelectedLocation) {
				autoCompleteTextView.setText(lastSelectedLocation.getName());
			}
			autoCompleteTextView.setVisibility(View.VISIBLE);
			
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
	
	public void setAutoComplete() {
		parseLocations();
		
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
	
	public void parseLocations() {
		try {
			locations = ConstuencyParserHelper.readLocations(getActivity(), getCityResId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setLocation(Constituency loc) {
		lastSelectedLocation = loc;
		autoCompleteButton.setText(loc.getName());
	}
	
	public void onSewageClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.SEWAGE, analyticsMap.get(getResources().getInteger(R.integer.sewage)));
	}

	public void onTransportationClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.TRANSPORT, analyticsMap.get(getResources().getInteger(R.integer.transportation)));
	}

	public void onWaterClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.WATER, analyticsMap.get(getResources().getInteger(R.integer.water)));
	}

	public void onRoadClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.ROAD, analyticsMap.get(getResources().getInteger(R.integer.road)));
	}

	public void onElectricityClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.ELECTRICITY, analyticsMap.get(getResources().getInteger(R.integer.electricity)));
	}

	public void onLawAndOrderClick(View view) {
		openIssueActivity(ISSUE_CATEGORY.LAW, analyticsMap.get(getResources().getInteger(R.integer.lawandorder)));
	}

	private void openIssueActivity(ISSUE_CATEGORY issue, List<Analytics> analytics) {
		
			Intent intent = new Intent(getActivity(), IssueActivity.class);
			intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
			intent.putExtra(IssueActivity.EXTRA_IS_ANALYTICS, true);
			intent.putParcelableArrayListExtra(IssueActivity.EXTRA_ANALYTICS_LIST, (ArrayList<Analytics>) analytics);
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
					constituencyId = Integer.parseInt(mlaId);
					executeAnalyticsRequest();
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }
        };
    }
	
	private void executeAnalyticsRequest() {
		String url = "http://50.57.224.47/html/dev/micronews/get_summary.php?cid=" + constituencyId + "&time_frame=1w";
		JsonRequestWithCache request = new JsonRequestWithCache(Method.GET, url, null, createAnalyticsReqSuccessListener(), createMyReqErrorListener());
		mRequestQueue.add(request);		
		DialogFactory.showPleaseWaitProgressDialog(getActivity());
	}
	
	
	private Response.Listener<JSONObject> createAnalyticsReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {         		
            		if(isResumed) {
						Log.d(TAG, jsonObject.toString(2));
						parseJsonToAnalyticsMap(jsonObject);
            		}
            		DialogFactory.hideProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }
            
			
        };
    }
	private void parseJsonToAnalyticsMap(JSONObject jsonObject) throws JSONException {
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
			
			//Log.d(TAG, "analytics, array length: " + analyticsList.size());
			analyticsMap.put(keyInt, analyticsList);
			setViewsAccordingToAnalytics();
		}
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
	        for (Analytics analytics : pairs.getValue()) {
				totalCount += analytics.getCount();
			}	        
	    }
	    complaintCount = totalCount;
	    setCounts(issueCount, complaintCount);
	}
	
	private void setIssueCount(IssueButton issueButton, int categoryResId) {
		try {		
			int issuePercentage = 0;
			int issueCount = 0;
			
			Resources resources = getActivity().getResources();
			Integer issueCategory = resources.getInteger(categoryResId);
			Set<Integer> anylyticsSet = analyticsMap.keySet();
			for (Integer integer : anylyticsSet) {
				//Log.d(TAG, "KeySet: " + integer);
			}
			if(analyticsMap.containsKey(issueCategory)) {			
				issueCount = getComplaintCountForCategory(issueCategory);
				
			}
		
			//Log.d(TAG, "count: " + issueCount + ", complaintCount: " + complaintCount);
			if( complaintCount != 0) {
				issuePercentage = (issueCount * 100) / complaintCount;
			}
			//Log.d(TAG, "percentage: " + issuePercentage);
			issueButton.setPercentage(issuePercentage  );
		} catch (Exception e) {
			issueButton.setPercentage(0);
			e.printStackTrace();
		}
	}
	
	private int getComplaintCountForCategory(int issueCategory) {
		int count = 0;
		List<Analytics> analyticsList = analyticsMap.get(issueCategory);
		for (Analytics analytics : analyticsList) {
			count += analytics.getCount();
		}
		
		return count;
	}
	
	public void setCurrentCity() {		
 		if(null != JanSamparkApplication.getInstance().getLastKnownConstituency()) {
 			if(getCityResId() == -1 ) {
 				setCityResId(Constituency.getCityRefId(JanSamparkApplication.getInstance().getLastKnownConstituency().getAddress()));
 			}
			Log.d(TAG, "call fetch analytics from setCurrentCity");
			setAutoComplete();
			fetchCityAnalytics();
			
		} else {
			Log.e(TAG, "last known constituency is null");
		}	
	}
	
	private String requestTag = "tag";
	
	
	private void fetchCityAnalytics() {		
		mRequestQueue.cancelAll(requestTag);
		int id = getResources().getInteger(getCityResId());		
		String url = "http://50.57.224.47/html/dev/micronews/get_summary.php?cid=" + id + "&time_frame=1w";
		Log.d(TAG, "requesting city analytics for url: " + url);
		JsonRequestWithCache request = new JsonRequestWithCache(Method.GET, url, null, createCityDetailsReqSuccessListener(), createMyReqErrorListener());
		request.setTag(requestTag);
		mRequestQueue.add(request);		
		DialogFactory.showPleaseWaitProgressDialog(getActivity());
		
	}
	
	private Response.Listener<JSONObject> createCityDetailsReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {         		
            		if(R.id.analytics_overall == analyticsRadioGroup.getCheckedRadioButtonId()) {
	            		if(isResumed) {
							parseJsonToAnalyticsMap(jsonObject);						
	            		}
            		}
            		DialogFactory.hideProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }

        };
	} 
	
	private void executeCurrentMLAIdRequest()  {	
		Location lastKnownLocation = JanSamparkApplication.getInstance().getLastKnownLocation();
		if(null != lastKnownLocation) {
			double lat = lastKnownLocation.getLatitude();
			double lon = lastKnownLocation.getLongitude();
			String url = "http://50.57.224.47/html/dev/micronews/getmlaid.php?lat=" +lat + "&long=" + lon;		
			JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createCurrentMLAIDReqSuccessListener(), createMLAIdErrorListener());
			
			Log.d(TAG, "url: " + request.getUrl());
			mRequestQueue.add(request);
			DialogFactory.showPleaseWaitProgressDialog(getActivity());
		}
	}
		
	 private Response.ErrorListener createMLAIdErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {
	            	Log.d(TAG, "try again");
	            	DialogFactory.hideProgressDialog();
	            }
	        };
	  }
	 
	 private Listener<JSONObject>  createCurrentMLAIDReqSuccessListener() {
		 return new Response.Listener<JSONObject>() {
	            @Override
	            public void onResponse(JSONObject jsonObject) {
	            	try {
	            		Log.d(TAG, jsonObject.toString(2));
						String mlaId = jsonObject.getString("consti_id");
						constituencyId = Integer.parseInt(mlaId);
						
						executeAnalyticsRequest();
						executeMLADetailsRequest(mlaId);
					} catch (JSONException e) {
						e.printStackTrace();
					} 
	            }
	        };
	 }
	 
	 private void executeMLADetailsRequest(String mlaId) {
			String url = "http://50.57.224.47/html/dev/micronews/mla-info/" + mlaId;
			JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLADetailsReqSuccessListener(), createMLADetailsReqErrorListener());
			mRequestQueue.add(request);
			DialogFactory.showPleaseWaitProgressDialog(getActivity());
		}
		
		String MLAName;
		String MLAPic;
		
		private Response.Listener<JSONObject> createMLADetailsReqSuccessListener() {
	        return new Response.Listener<JSONObject>() {
	            @Override
	            public void onResponse(JSONObject jsonObject) {
	            	try {            	
							JSONObject node = jsonObject.getJSONArray("nodes").getJSONObject(0).getJSONObject("node");
							String constituency = node.getString("constituency");
							autoCompleteButton.setText(constituency);
							DialogFactory.hideProgressDialog();
					} catch (JSONException e) {
						e.printStackTrace();
					}        	
	            }
	        };
	    }	
		 private Response.ErrorListener createMLADetailsReqErrorListener() {
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
	 
	
}
