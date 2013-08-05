package com.swaraj.vashisthg;

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
import com.swaraj.vashisthg.R;
import com.swaraj.vashisthg.helpers.DialogFactory;
import com.swaraj.vashisthg.helpers.IssueFactory;
import com.swaraj.vashisthg.helpers.LocationDataManager;
import com.swaraj.vashisthg.helpers.LruBitmapCache;
import com.swaraj.vashisthg.helpers.ReverseGeoCodingTask;
import com.swaraj.vashisthg.helpers.WindowAnimationHelper;
import com.swaraj.vashisthg.models.Constituency;
import com.swaraj.vashisthg.models.IssueItem;

public class IssueSummaryActivity extends FragmentActivity {
	
	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	public static final String EXTRA_LOCATION = "location";
	public static final String EXTRA_MLA_NAME = "name";
	public static final String EXTRA_MLA_PIC = "mla_pic";
	public static final String EXTRA_CONSTITUENCY = "constituency";
	public static final String EXTRA_DROP_BIT = "drop_bit";
	
	private IssueItem issueItem;
	private Location location;
	private String mlaName;
	private String mlaUrl;
	private String constituency;
	private int dropBit;
	
	
	private RequestQueue mRequestQueue;
	ImageLoader imageLoader;
	
	NetworkImageView mlaImageView;
	TextView nameTV;
	TextView constituencyTV;
	TextView categoryTV;
	TextView systemTV;
	TextView addressTV;
	TextView issueNameTV;
	
	boolean isResumed;
	boolean invalidShown;
	
	
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
			dropBit = getIntent().getIntExtra(EXTRA_DROP_BIT, 0);
		} else {
			issueItem = (IssueItem) savedInstanceState.getParcelable(EXTRA_ISSUE_ITEM);
			location = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
			mlaName = savedInstanceState.getString(EXTRA_MLA_NAME);
			mlaUrl = savedInstanceState.getString(EXTRA_MLA_PIC);
			constituency = savedInstanceState.getString(EXTRA_CONSTITUENCY);
			dropBit = savedInstanceState.getInt(EXTRA_DROP_BIT);
		}
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		imageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(4 * 1024 * 1024));
		setMLA();
		setIssueDetails();
		fetchAddress();
	}
	
	public void showInvalidConstituency(String constituency) {
		if(!invalidShown) {
			if( 1 == dropBit) {
				DialogFactory.createMessageDialog(getResources().getString(R.string.invalid_constituency_title), getResources().getString(R.string.invalid_constituency_post)).show(getSupportFragmentManager(), "FAIL");
				invalidShown = true;
			} 
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		isResumed = true;
		showInvalidConstituency(constituency);
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
		outState.putString(EXTRA_MLA_PIC, mlaUrl);
		outState.putInt(EXTRA_DROP_BIT, dropBit);
	}
	
	private void setView() {
		mlaImageView = (NetworkImageView) findViewById(R.id.issue_summary_mla_image);
		nameTV = (TextView) findViewById(R.id.issue_summary_mla);
		constituencyTV = (TextView) findViewById(R.id.issue_summary_mla_constituency);
		categoryTV = (TextView) findViewById(R.id.issue_summary_category);
		systemTV = (TextView) findViewById(R.id.issue_summary_system);
		addressTV = (TextView) findViewById(R.id.issue_summary_address);
		issueNameTV = (TextView) findViewById(R.id.issue_summary_name);
	}
	
	private void setIssueDetails() {
		categoryTV.setText(IssueFactory.getIssueCategoryName(this, issueItem.getIssueCategory()));
		systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
		issueNameTV.setText(issueItem.getIssueName());
	}	
	
	private void setMLA() {
		nameTV.setText(mlaName);
		mlaImageView.setImageUrl(mlaUrl, imageLoader);
		constituencyTV.setText("MLA, " + constituency);
	}
	
	
	private void fetchAddress() {		
		if(null == JanSamparkApplication.getInstance().getLastKnownConstituency()) {
			LocationDataManager locationManager = new LocationDataManager(this, geoCodingListener);
			locationManager.fetchAddress(location);
		} else {
			showConstituency(JanSamparkApplication.getInstance().getLastKnownConstituency());
		}
	}
	
	private void showConstituency(Constituency location) {
		try {
			addressTV.setText(location.getName());
		} catch( Exception e){
			addressTV.setText("");
		}	
	}
	
	private ReverseGeoCodingTask.GeoCodingTaskListener geoCodingListener = new ReverseGeoCodingTask.GeoCodingTaskListener() {
		
		@Override
		public void didReceiveGeoCoding(List<Constituency> locations) {
			if(isResumed) {
				if(!locations.isEmpty()) {
					showConstituency(locations.get(0));	
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
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		WindowAnimationHelper.startActivityWithSlideFromRight(this, intent);
		finish();
	}	
	
	
	public void onTitleBarLeftButtonClick(View view) {
		onBackPressed();
	}
	
	public void onTitleBarRightButtonClick(View view) {
		
	}
	
	@Override
	public void finish() {
		super.finish();
		WindowAnimationHelper.finish(this);
	}
	
}
