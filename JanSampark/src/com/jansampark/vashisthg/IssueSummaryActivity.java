package com.jansampark.vashisthg;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jansampark.vashisthg.helpers.LruBitmapCache;
import com.jansampark.vashisthg.helpers.ReverseGeoCodingTask;
import com.jansampark.vashisthg.models.Constituency;
import com.jansampark.vashisthg.models.IssueItem;

public class IssueSummaryActivity extends FragmentActivity {
	
	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	public static final String EXTRA_LOCATION = "location";
	public static final String EXTRA_MLA_NAME = "name";
	public static final String EXTRA_MLA_PIC = "mla_pic";
	public static final String EXTRA_CONSTITUENCY = "constituency";
	
	private IssueItem issueItem;
	private Location location;
	private String mlaName;
	private String mlaUrl;
	private String constituency;
	
	
	private RequestQueue mRequestQueue;
	ImageLoader imageLoader;
	
	NetworkImageView mlaImageView;
	TextView nameTV;
	TextView constituencyTV;
	TextView categoryTV;
	TextView systemTV;
	TextView addressTV;
	
	boolean isResumed;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_summary);
		setView();

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
			location = (Location) getIntent().getParcelableExtra(EXTRA_LOCATION);
			mlaName = getIntent().getStringExtra(EXTRA_MLA_NAME);
			mlaUrl = getIntent().getStringExtra(EXTRA_MLA_PIC);
			constituency = getIntent().getStringExtra(EXTRA_CONSTITUENCY);
		} else {
			issueItem = (IssueItem) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);
			location = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
			mlaName = savedInstanceState.getString(EXTRA_MLA_NAME);
			mlaUrl = savedInstanceState.getString(EXTRA_MLA_PIC);
			constituency = savedInstanceState.getString(EXTRA_CONSTITUENCY);
		}
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		imageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(4 * 1024 * 1024));
		//executeMLAIdRequest();
		setMLA();
		setCategoryAndSystem();
		fetchAddress();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isResumed = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isResumed = false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_ISSUE_ITEM, issueItem);
		outState.putParcelable(EXTRA_LOCATION, location);
		outState.putString(EXTRA_MLA_NAME, mlaName);
		outState.putString(EXTRA_MLA_PIC, EXTRA_MLA_PIC);
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
	
	private void setMLA() {
		nameTV.setText(mlaName);
		mlaImageView.setImageUrl(mlaUrl, imageLoader);
		constituencyTV.setText(constituency);
	}
	
	
	private void fetchAddress() {
		ReverseGeoCodingTask geocodingTask = ReverseGeoCodingTask.newInstance(this, geoCodingListener, location);
		geocodingTask.execute();
	}
	
	private ReverseGeoCodingTask.GeoCodingTaskListener geoCodingListener = new ReverseGeoCodingTask.GeoCodingTaskListener() {
		
		@Override
		public void didReceiveGeoCoding(List<Constituency> locations) {
			if(isResumed) {
				try {
					addressTV.setText(locations.get(0).getName());
				} catch( Exception e){
					addressTV.setText("");
				}			
			}
		}
		
		@Override
		public void didFailReceivingGeoCoding() {
			if(isResumed) {
				addressTV.setText("");	
			}
		}
	};

	public void onAnotherComplaintClick(View view) {
		startMainActivity();
	}
	
	public void onDoneClick(View view) {
		startMainActivity();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startMainActivity();
	}
	
	
	public void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MainActivity.EXTRA_LOCATION, location);
		startActivity(intent);
		finish();
	}	
	
	
	public void onTitleBarLeftButtonClick(View view) {
		
	}
	
	public void onTitleBarRightButtonClick(View view) {
		
	}
	
}
