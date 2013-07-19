package com.jansampark.vashisthg;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jansampark.vashisthg.CameraHelper.CameraUtilActivity;
import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.volley.MultipartRequest;

public class IssueDetailsActivity extends CameraUtilActivity implements LocationListener {
	
	public static class IssueDetail {
		public String lat;
		public String lon;
		public IssueItem issueItem;
		public String image;
		public String userImage;
		public String reporterId = "123";
		public String description;
	}	

	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	public static final String EXTRA_LOCATION = "location";
	private IssueItem issueItem;
	
	private TextView categoryTV;
	private TextView nameTV;
	private TextView systemTV;
	
	private Button addDescription;
	private EditText descriptionET;
	private TextView descriptionTextView;
	private Button editDesctiption;
	
	
	private ViewGroup takeImageContainer;
	private ViewGroup imageTakenContainer;
	private ImageView issueImageView;
	private ImageView sendingImage;
	private View darkOverlay;
	private Button postButton;
	
	CameraHelper cameraHelper;
	
	
	private RequestQueue mRequestQueue;
	
	private LocationManager locationManager;
	Location lastKnownLocation;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_details);

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
			lastKnownLocation = (Location) getIntent().getParcelableExtra(EXTRA_LOCATION);
		} else {
			issueItem = (IssueItem) savedInstanceState.getParcelable(EXTRA_ISSUE_ITEM);		
			lastKnownLocation = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
		}
		initLocation();
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		setCameraHelper();		
		setViews();		
	}
	
	private void initLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String locationProvider = LocationManager.NETWORK_PROVIDER;
		lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria,true);
		
		locationManager.requestLocationUpdates(provider, 20000, 0,this);
	}
	
	@Override
	protected void onDestroy() {
		locationManager.removeUpdates(this);
		super.onDestroy();
	}
	
	private void setCameraHelper() {
		cameraHelper = new CameraHelper(this);		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_ISSUE_ITEM, issueItem);
		outState.putParcelable(EXTRA_LOCATION, lastKnownLocation);
		cameraHelper.onSaveInstanceState(outState);
	}
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		cameraHelper.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);		
		cameraHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	private void setViews() {
		categoryTV = (TextView) findViewById(R.id.issue_detail_category_text);
		nameTV = (TextView) findViewById(R.id.issue_detail_name_text);
		systemTV = (TextView) findViewById(R.id.issue_detail_system_text);
		addDescription = (Button) findViewById(R.id.issue_detail_descption_add_btn);
		editDesctiption = (Button) findViewById(R.id.issue_detail_descption_edit_btn);
		descriptionET = (EditText) findViewById(R.id.issue_detail_description_edit_text);
		descriptionTextView = (TextView) findViewById(R.id.issue_detail_description_text_view);
		darkOverlay = findViewById(R.id.issue_details_overlay);
		sendingImage = (ImageView)findViewById(R.id.issue_details_sending_image);
		postButton = (Button) findViewById(R.id.issue_details_post);
		
		
		categoryTV.setText(IssueFactory.getIssueCategoryName(this, issueItem.getIssueCategory()));
		nameTV.setText(issueItem.getIssueName());
		systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
		setDescription();
		resetDescription();
		setIssueImageViews();
		
	}
	
	@Override
	public void onBackPressed() {
		if(descriptionET.hasFocus()) {
			resetDescription();
		} else {
			super.onBackPressed();
		}
	}
	
	
	
	private void setIssueImageViews() {
		takeImageContainer = (ViewGroup) findViewById(R.id.take_photo_container_ref);
		imageTakenContainer = (ViewGroup) findViewById(R.id.photo_taken_container_ref);	
		issueImageView = (ImageView)  findViewById(R.id.chosen_pic);		
		displayImageIfAvailable();
	}
	
	private void resetIssusImageView() {
		if(TextUtils.isEmpty(cameraHelper.getImageName())) {
			takeImageContainer.setVisibility(View.VISIBLE);
			imageTakenContainer.setVisibility(View.GONE);
		} else {
			takeImageContainer.setVisibility(View.GONE);
			imageTakenContainer.setVisibility(View.VISIBLE);			
		}
	}
	
	private void resetDescription() {
		if(TextUtils.isEmpty(descriptionET.getText().toString().trim())) {
			showDescriptionButton();
		}  else {
			showDescriptionTV();
		}
	}
	
	
	
	private void setDescription() {		
		addDescription.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDescriptionEditText();
			}
		});
		
		editDesctiption.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDescriptionEditText();
			}
		});
		setListenerToDescriptionET();
	}
	
	private void showDescriptionTV() {
		addDescription.setVisibility(View.INVISIBLE);
		descriptionTextView.setVisibility(View.VISIBLE);
		descriptionET.setVisibility(View.INVISIBLE);
		editDesctiption.setVisibility(View.VISIBLE);
		
		descriptionTextView.setText(descriptionET.getText());
		Utils.hideKeyboard(this, descriptionTextView);
	}
	
	private void showDescriptionButton() {
		addDescription.setVisibility(View.VISIBLE);
		descriptionET.setVisibility(View.INVISIBLE);
		editDesctiption.setVisibility(View.INVISIBLE);
		descriptionTextView.setVisibility(View.INVISIBLE);
		Utils.hideKeyboard(this, descriptionTextView);
	}
	
	private void showDescriptionEditText() {
		addDescription.setVisibility(View.INVISIBLE);
		
		editDesctiption.setVisibility(View.INVISIBLE);
		descriptionTextView.setVisibility(View.INVISIBLE);
		descriptionET.requestFocus();		
		descriptionET.setVisibility(View.VISIBLE);
		Utils.showKeyboard(this, descriptionET);
	}	
	private void setListenerToDescriptionET() {
		descriptionET.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {		
				resetDescription();				
			    return true;
			}
		});
	}

	public void takePhoto(View view) {
		cameraHelper.openOnlyCameraIntent();
	}
	
	public void choosePhoto(View view) {
		cameraHelper.openOnlyGalleryIntent();
	}
	
	public void retakePhoto(View view) {
		cameraHelper.openImageIntent();
	}
	
	public void removePhoto(View view) {
		cameraHelper.setImageName(null);
		resetIssusImageView();
	}
	
	public void onPostClick(View view) {
		showSendingOverlay();
		executeRequest();			
	}
	
	private void showSendingOverlay() {
		darkOverlay.setVisibility(View.VISIBLE);
		darkOverlay.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		sendingImage.setVisibility(View.VISIBLE);
		sendingImage.setBackgroundResource(R.drawable.running_man);
		AnimationDrawable frameAnimation = (AnimationDrawable) sendingImage.getBackground();
		frameAnimation.start();
	}
	
	private void hideSendingOverlay() {
		darkOverlay.setVisibility(View.INVISIBLE);
		sendingImage.setVisibility(View.GONE);
	}
	
	@Override
	public void onCameraPicTaken() {				
		displayImageIfAvailable();
	}
	
	private void displayImageIfAvailable() {
		resetIssusImageView();
		if(!TextUtils.isEmpty(cameraHelper.getImageName())) {
			new BitmapWorkerTask(issueImageView, 200).execute(cameraHelper.getImageName());
		}
	}

	@Override
	public void onGalleryPicChosen() {
		displayImageIfAvailable();		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.d("Details", "location changed");
		lastKnownLocation = location;
		locationManager.removeUpdates((android.location.LocationListener) this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}
	
	
	
	private void executeRequest()  {			
		postButton.setEnabled(false);
		String url = "http://50.57.224.47/html/dev/micronews/?q=phonegap/post";
		IssueDetail issueDetail = new IssueDetail();
		issueDetail.lat = lastKnownLocation.getLatitude() + "";
		issueDetail.lon = lastKnownLocation.getLongitude() + "";
		issueDetail.image = cameraHelper.getImageName();
		issueDetail.userImage = Utils.getUserImage();
		issueDetail.reporterId  = "123";
		issueDetail.description = descriptionET.getText().toString();
		issueDetail.issueItem = issueItem;
		
		MultipartRequest request = new MultipartRequest(url, createMyReqErrorListener(), createMyReqSuccessListener(),  issueDetail);
		request.setRetryPolicy(new DefaultRetryPolicy(
		        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
		        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
		        2));
		mRequestQueue.add(request);
	}
	
	 private Response.ErrorListener createMyReqErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {	            	
	            	hideSendingOverlay();
	            	Toast.makeText(IssueDetailsActivity.this, "Could not connect to server.", Toast.LENGTH_LONG).show();
	            	Log.e("Details", error.getMessage());
	            	postButton.setEnabled(true);
	            }
	        };
	  }
	 
	private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {            	
            	Log.d("details", "response: " + response);            	
            	Intent intent = new Intent(IssueDetailsActivity.this, IssueSummaryActivity.class);
            	intent.putExtra(IssueSummaryActivity.EXTRA_ISSUE_ITEM, issueItem);
            	intent.putExtra(IssueSummaryActivity.EXTRA_LOCATION, lastKnownLocation);
            	startActivity(intent);   
            	hideSendingOverlay();
            	finish();
            }
        };
    }	
}
