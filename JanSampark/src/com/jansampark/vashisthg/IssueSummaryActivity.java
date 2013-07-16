package com.jansampark.vashisthg;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.jansampark.vashisthg.IssueDetailsActivity.IssueDetail;
import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.volley.MultipartRequest;
import com.jansampark.vashisthg.volley.StringRequestWithHeaderAndParams;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class IssueSummaryActivity extends FragmentActivity {
	
	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	private IssueItem issueItem;
	
	private RequestQueue mRequestQueue;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_summary);

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
		} else {
			issueItem = (IssueItem) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);
		}
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		executeRequest();
	}
	
	private void executeRequest()  {
		

		String url = "http://50.57.224.47/html/dev/micronews/getmlaid.php?lat=12.88&long=77.655";
		


		
		StringRequestWithHeaderAndParams request = new StringRequestWithHeaderAndParams(url, createMLAIDReqSuccessListener(), createMyReqErrorListener(), null, null);
		
		
		mRequestQueue.add(request);
	}
	
	 private Response.ErrorListener createMyReqErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {
	            	Log.d("summary", "try again");
	            }
	        };
	  }
	 
	private Response.Listener<String> createMLAIDReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            	Log.d("summary", "response mla id : " + response);          	
            }
        };
    }	

}
