package com.jansampark.vashisthg;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
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
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.jansampark.vashisthg.helpers.CameraHelper;
import com.jansampark.vashisthg.helpers.CameraHelper.CameraUtilActivity;
import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.models.IssueItem;
import com.jansampark.vashisthg.volley.MultipartRequest;

public class IssueDetailsActivity extends CameraUtilActivity {
	
	private static final String TAG = "IssueDetailsActivity";
	
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
	private TextView sendingText;
	private View darkOverlay;
	private Button postButton;
	
	CameraHelper cameraHelper;
		
	private RequestQueue mRequestQueue;
	
	Location lastKnownLocation;
	
	LocationRequest locationRequest;
    LocationClient locationClient;
	
	
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
		mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		setCameraHelper();		
		setViews();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startLocationTracking();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(null != locationClient) {
			locationClient.removeLocationUpdates(mLocationListener);
		}
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
		sendingText = (TextView) findViewById(R.id.issue_details_sending);
				
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
		executeMLAIdRequest();
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
		sendingText.setVisibility(View.VISIBLE);
	}
	
	private void hideSendingOverlay() {
		darkOverlay.setVisibility(View.GONE);
		sendingImage.setVisibility(View.GONE);
		sendingText.setVisibility(View.GONE);
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
	
	protected void startLocationTracking() {	
	    if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)) {
	       locationClient = new LocationClient(this, mConnectionCallbacks, mConnectionFailedListener);
	       locationClient.connect();
	    }
	    
	    
	}

	private ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks() {

	    @Override
	    public void onDisconnected() {
	    }

	    @Override
	    public void onConnected(Bundle arg0) {
	    	if(lastKnownLocation == null) {
				lastKnownLocation = locationClient.getLastLocation();
			}
	    	
	        LocationRequest locationRequest = LocationRequest.create();
	        locationRequest.setInterval(getResources().getInteger(R.integer.location_update_millis)).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	        locationClient.requestLocationUpdates(locationRequest, mLocationListener);
	    }
	};

	private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() {

	    @Override
	    public void onConnectionFailed(ConnectionResult arg0) {
	        //Log.e(TAG, "ConnectionFailed");
	    }
	};

	private LocationListener mLocationListener = new LocationListener() {
	    @Override
	        public void onLocationChanged(Location location) {	         
	    		lastKnownLocation = location;
	    }
	};	
	
	private void executeRequest()  {			
		if(null != lastKnownLocation) {
			postButton.setEnabled(false);
			String url = "http://50.57.224.47/html/dev/micronews/?q=phonegap/post";
			IssueDetail issueDetail = new IssueDetail();
			issueDetail.lat = lastKnownLocation.getLatitude() + "";
			issueDetail.lon = lastKnownLocation.getLongitude() + "";
			issueDetail.image = cameraHelper.getImageName();
			issueDetail.userImage = Utils.getUserImage(this);
			issueDetail.reporterId  = "123";
			issueDetail.description = descriptionET.getText().toString();
			issueDetail.issueItem = issueItem;
			
			MultipartRequest request = new MultipartRequest(url, createMyReqErrorListener(), createMyReqSuccessListener(),  issueDetail);
			request.setRetryPolicy(new DefaultRetryPolicy(
			        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
			        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
			        3));
			mRequestQueue.add(request);
		} else {
			Toast.makeText(this, "Fetching location", Toast.LENGTH_LONG).show();
		}
	}
	
	 private Response.ErrorListener createMyReqErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {	            	
	            	hideSendingOverlay();
	            	Toast.makeText(IssueDetailsActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
	            	if(null != error) {
	            		Log.e("Details", "" + error.getMessage());
	            	}
	            	postButton.setEnabled(true);
	            }
	        };
	  }
	 
	private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {            	
            	Log.d("details", "response: " + response);            	
            	
            }
        };
    }	
	
	private void executeMLAIdRequest()  {	
		if(null != lastKnownLocation) {
			double lat = lastKnownLocation.getLatitude();
			double lon = lastKnownLocation.getLongitude();
			String url = "http://50.57.224.47/html/dev/micronews/getmlaid.php?lat=" +lat + "&long=" + lon;		
			JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLAIDReqSuccessListener(), createMLAIdErrorListener());
			
			Log.d(TAG, "url: " + request.getUrl());
			mRequestQueue.add(request);
		}
	}
		
	 private Response.ErrorListener createMLAIdErrorListener() {
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
					e.printStackTrace();
				}        	
            }
        };
    }	
	
	private void executeMLADetailsRequest(String mlaId) {
		String url = "http://50.57.224.47/html/dev/micronews/mla-info/" + mlaId;
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLADetailsReqSuccessListener(), createMLADetailsReqErrorListener());
		mRequestQueue.add(request);
	}
	
	String MLAName;
	String MLAPic;
	
	private Response.Listener<JSONObject> createMLADetailsReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {            	
						JSONObject node = jsonObject.getJSONArray("nodes").getJSONObject(0).getJSONObject("node");
						String url = node.getString("image");					
						String name = node.getString("mla_name");
						String constituency = node.getString("constituency");
						nameTV.setText(name);
						
						Intent intent = new Intent(IssueDetailsActivity.this, IssueSummaryActivity.class);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_ISSUE_ITEM, issueItem);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_LOCATION, lastKnownLocation);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_CONSTITUENCY, constituency);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_MLA_NAME, name);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_MLA_PIC, url);
		            	startActivity(intent);   
		            	hideSendingOverlay();
		            	finish();
            		
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
	            	hideSendingOverlay();
	            	Toast.makeText(IssueDetailsActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
	            	if(null != error) {
	            		Log.e("Details", "" + error.getMessage());
	            	}
	            	postButton.setEnabled(true);
	            }
	        };
	  }
	
	
	public void onTitleBarLeftButtonClick(View view) {
		
	}
	
	public void onTitleBarRightButtonClick(View view) {
		
	}
	
}
