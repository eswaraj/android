package com.jansampark.vashisthg;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jansampark.vashisthg.helpers.LruBitmapCache;
import com.jansampark.vashisthg.helpers.ReverseGeoCodingTask;
import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.models.Constituency;

public class IssueSummaryActivity extends FragmentActivity {
	private static final String TAG = "SUMMARY";
	
	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	public static final String EXTRA_LOCATION = "location";
	private IssueItem issueItem;
	private Location location;
	
	private RequestQueue mRequestQueue;
	ImageLoader imageLoader;
	
	NetworkImageView mlaImageView;
	TextView nameTV;
	TextView constituencyTV;
	TextView categoryTV;
	TextView systemTV;
	TextView addressTV;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_summary);
		setView();

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
			location = (Location) getIntent().getParcelableExtra(EXTRA_LOCATION);
		} else {
			issueItem = (IssueItem) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);
			location = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
		}
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		imageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(4 * 1024 * 1024));
		executeMLAIdRequest();
		setCategoryAndSystem();
		fetchAddress();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_ISSUE_ITEM, issueItem);
		outState.putParcelable(EXTRA_LOCATION, location);
	}
	
	private void setView() {
		mlaImageView = (NetworkImageView) findViewById(R.id.issue_summary_mla_image);
		nameTV = (TextView) findViewById(R.id.issue_summary_mla);
		constituencyTV = (TextView) findViewById(R.id.issue_summary_mla_constituency);
		categoryTV = (TextView) findViewById(R.id.issue_summary_category);
		systemTV = (TextView) findViewById(R.id.issue_summary_system);
		addressTV = (TextView) findViewById(R.id.issue_summary_address);
	}
	
	private void setCategoryAndSystem() {
		categoryTV.setText(IssueFactory.getIssueCategoryName(this, issueItem.getIssueCategory()));
		systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
	}
	
	private void executeMLAIdRequest()  {	
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		String url = "http://50.57.224.47/html/dev/micronews/getmlaid.php?lat=" +lat + "&long=" + lon;		
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLAIDReqSuccessListener(), createMyReqErrorListener());
		
		Log.d(TAG, "url: " + request.getUrl());
		mRequestQueue.add(request);
	}
	
	
	
	 private Response.ErrorListener createMyReqErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {
	            	Log.d(TAG, "try again");
	            }
	        };
	  }
	 
	private Response.Listener<JSONObject> createMLAIDReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {
            		Log.d(TAG, jsonObject.toString(1));
					String mlaId = jsonObject.getString("consti_id");
					Log.d(TAG, "consti_id: " + mlaId);
					executeMLADetailsRequest(mlaId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}        	
            }
        };
    }	
	
	private void executeMLADetailsRequest(String mlaId) {
		String url = "http://50.57.224.47/html/dev/micronews/mla-info/" + mlaId;
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLADetailsReqSuccessListener(), createMyReqErrorListener());
		mRequestQueue.add(request);
	}
	
	String MLAName;
	String MLAPic;
	
	private Response.Listener<JSONObject> createMLADetailsReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {
					Log.d(TAG, jsonObject.toString(2));
					JSONObject node = jsonObject.getJSONArray("nodes").getJSONObject(0).getJSONObject("node");
					String url = node.getString("image");
					mlaImageView.setImageUrl(url, imageLoader);					
					String name = node.getString("mla_name");
					String constituency = node.getString("constituency");
					nameTV.setText(name);
					constituencyTV.setText(constituency);
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }
        };
    }	
	
	
	private void fetchAddress() {
		ReverseGeoCodingTask geocodingTask = ReverseGeoCodingTask.newInstance(this, geoCodingListener, location);
		geocodingTask.execute();
	}
	
	private ReverseGeoCodingTask.GeoCodingTaskListener geoCodingListener = new ReverseGeoCodingTask.GeoCodingTaskListener() {
		
		@Override
		public void didReceiveGeoCoding(List<Constituency> locations) {
			try {
				addressTV.setText(locations.get(0).getName());
			} catch( Exception e){
				addressTV.setText("");
			}
		}
		
		@Override
		public void didFailReceivingGeoCoding() {
			addressTV.setText("");			
		}
	};

}
