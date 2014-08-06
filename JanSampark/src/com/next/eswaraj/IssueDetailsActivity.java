package com.next.eswaraj;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
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
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.SaveComplaintRequestDto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.next.eswaraj.config.Constants;
import com.next.eswaraj.dialog.MessageDialog;
import com.next.eswaraj.helpers.BitmapWorkerTask;
import com.next.eswaraj.helpers.CameraHelper;
import com.next.eswaraj.helpers.CameraHelper.CameraUtilActivity;
import com.next.eswaraj.helpers.DialogFactory;
import com.next.eswaraj.helpers.Utils;
import com.next.eswaraj.helpers.WindowAnimationHelper;
import com.next.eswaraj.volley.IssuePostRequest;

public class IssueDetailsActivity extends CameraUtilActivity {
		
	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	public static final String EXTRA_LOCATION = "location";
	
	private TextView categoryTV;
	private TextView nameTV;
	private TextView nameLabelTV;
	private TextView otherDescription;
	
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
    int dropBit = 0;
    int banner = 0;
    
    private CategoryWithChildCategoryDto selectedSubCategory;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_details);
		
		if (null == savedInstanceState) {
			selectedSubCategory = (CategoryWithChildCategoryDto) getIntent().getSerializableExtra(EXTRA_ISSUE_ITEM);
			lastKnownLocation = (Location) getIntent().getParcelableExtra(EXTRA_LOCATION);
		} else {
			selectedSubCategory = (CategoryWithChildCategoryDto) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);		
			lastKnownLocation = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
		}
		Log.i("eswaraj", "issueItem = "+selectedSubCategory.getName() + " : " +selectedSubCategory.getId());
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
			if(locationClient.isConnected()) {
				locationClient.removeLocationUpdates(mLocationListener);
			}
		}
	}
	
	private void setCameraHelper() {
		cameraHelper = new CameraHelper(this);		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(EXTRA_ISSUE_ITEM, selectedSubCategory);
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
		nameLabelTV = (TextView) findViewById(R.id.issue_detail_name_label);
		nameTV = (TextView) findViewById(R.id.issue_detail_name_text);
		addDescription = (Button) findViewById(R.id.issue_detail_descption_add_btn);
		editDesctiption = (Button) findViewById(R.id.issue_detail_descption_edit_btn);
		descriptionET = (EditText) findViewById(R.id.issue_detail_description_edit_text);
		descriptionTextView = (TextView) findViewById(R.id.issue_detail_description_text_view);
		darkOverlay = findViewById(R.id.issue_details_overlay);
		sendingImage = (ImageView)findViewById(R.id.issue_details_sending_image);
		postButton = (Button) findViewById(R.id.issue_details_post);
		sendingText = (TextView) findViewById(R.id.issue_details_sending);
		otherDescription = (TextView) findViewById(R.id.issue_detail_other_description);
				
		categoryTV.setText(selectedSubCategory.getName());
		nameTV.setText(selectedSubCategory.getName());
		//systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
		setViewForOthers();
		setDescription();
		resetDescription();
		setIssueImageViews();		
	}
	
	private void setViewForOthers() {
		nameLabelTV.setVisibility(View.GONE);
		nameTV.setVisibility(View.GONE);
		otherDescription.setVisibility(View.VISIBLE);
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
		if(Utils.isLocationServicesEnabled(this)) {
		    executeRequest();
		    /*
			if(isDescriptionEmpty() ) {
				MessageDialog.create(getString(R.string.issue_details_enter_description)).show(getSupportFragmentManager(), "MESSAGE");
			} else {
				Log.i("eswaraj","Saving Complaint");
				executeRequest();
				
				//executeMLAIdRequest();
			}
			*/
		} else {
			DialogFactory.createMessageDialog("2. No Location Services Detected.", getString(R.string.no_location)).show(getSupportFragmentManager(), "NO_LOCATION");
		}
	}
	
	@SuppressLint("NewApi")
	private boolean isDescriptionEmpty() {
		boolean isEmpty;
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
			isEmpty = descriptionET.getText().toString().trim().isEmpty();
		} else {
			isEmpty = descriptionET.getText().toString().trim().length() == 0;
		}		
		return isEmpty;
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
	    	if(locationClient.isConnected()) {
		    	if(lastKnownLocation == null) {
					lastKnownLocation = locationClient.getLastLocation();
				}
		    	
		        LocationRequest locationRequest = LocationRequest.create();
		        locationRequest.setInterval(getResources().getInteger(R.integer.location_update_millis)).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		        locationClient.requestLocationUpdates(locationRequest, mLocationListener);
	    	}
	    }
	};

	private OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListener() {

	    @Override
	    public void onConnectionFailed(ConnectionResult arg0) {
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
			String imagePath = cameraHelper.getImageName();
			
			SaveComplaintRequestDto complaintDto = new SaveComplaintRequestDto();
			complaintDto.setCategoryId(selectedSubCategory.getId());
			complaintDto.setDescription(descriptionET.getText().toString());
			complaintDto.setTitle(descriptionET.getText().toString());
			String android_id = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
			complaintDto.setDeviceId(android_id);
			complaintDto.setDeviceTypeRef("Android");
			complaintDto.setLattitude(lastKnownLocation.getLatitude());
			complaintDto.setLongitude(lastKnownLocation.getLongitude());
			Log.i("eswaraj", "imagePath="+imagePath);
			try{
                showSendingOverlay();
                IssuePostRequest request = new IssuePostRequest(createMyReqErrorListener(), createMyReqSuccessListener(), complaintDto, imagePath);
				request.setRetryPolicy(new DefaultRetryPolicy(
				        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
				        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				        3));
				mRequestQueue.add(request);
				
			}catch(Exception ex){
				Log.e("eswaraj","Error while saving", ex);
			}
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
	            		Log.e("eswaraj", "" + error.getMessage(), error);
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
                hideSendingOverlay();
                Log.i("eswaraj", "Activity returning " + RESULT_OK + ", " + getParent());


                Intent intent = new Intent(IssueDetailsActivity.this, IssueSummaryActivity.class);
                intent.putExtra(IssueSummaryActivity.EXTRA_ISSUE_ITEM, selectedSubCategory);
                intent.putExtra(IssueSummaryActivity.EXTRA_LOCATION, lastKnownLocation);
                intent.putExtra(IssueSummaryActivity.EXTRA_CONSTITUENCY, "Bangalore");
                intent.putExtra(IssueSummaryActivity.EXTRA_MLA_NAME, "Ravi Sharma");
                intent.putExtra(IssueSummaryActivity.EXTRA_MLA_PIC, "https://fbcdn-sphotos-h-a.akamaihd.net/hphotos-ak-xfa1/t1.0-9/23780_381794843626_6042094_n.jpg");
                intent.putExtra(IssueSummaryActivity.EXTRA_DROP_BIT, dropBit);
                intent.putExtra(IssueSummaryActivity.EXTRA_BANNER, banner);
                intent.putExtra(IssueSummaryActivity.EXTRA_DESCRIPTION, descriptionET.getText().toString());
                WindowAnimationHelper.startActivityWithSlideFromRight(IssueDetailsActivity.this, intent);
                setResult(RESULT_OK);
                finish();

            }
        };
    }	
	
	private void executeMLAIdRequest()  {	
		if(null != lastKnownLocation) {
			showSendingOverlay();
			double lat = lastKnownLocation.getLatitude();
			double lon = lastKnownLocation.getLongitude();
			String url = "http://50.57.224.47/html/dev/micronews/getmlaid.php?lat=" +lat + "&long=" + lon;	
			
			JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLAIDReqSuccessListener(), createMLAIdErrorListener());
			mRequestQueue.add(request);
		} else {
			hideSendingOverlay();
			Toast.makeText(IssueDetailsActivity.this, "Could not fetch your location.", Toast.LENGTH_LONG).show();
			if(!Utils.isLocationNetworkProviderEnabled(this)) {
				DialogFactory.createMessageDialog(getString(R.string.enable_location_network_provider)).show(getSupportFragmentManager(), "ENABLE_NETWORK_LOCATION");
			}
		}
	}
		
	 private Response.ErrorListener createMLAIdErrorListener() {
	        return new Response.ErrorListener() {
	            @Override
	            public void onErrorResponse(VolleyError error) {
	            }
	        };
	  }
	 
	private Response.Listener<JSONObject> createMLAIDReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            	try {
            		Log.d("TAG_DETAILS", "response: " + jsonObject.toString());
					String mlaId = jsonObject.getString("consti_id");
					dropBit = dropBit(jsonObject);
					banner = banner(jsonObject);
					
					if(dropBit != Constants.DROPBIT_INVALID_CONSTITUENCY) {
						executeRequest();
					}
					
					executeMLADetailsRequest(mlaId);
										
				} catch (JSONException e) {
					e.printStackTrace();
				}        	
            }
        };
    }	
	
	private int dropBit(JSONObject jsonObject) {
		int dropBit = 0;
		
		try {
			dropBit = jsonObject.getInt("ol_drop_bit");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return dropBit;
	}
	
	private int banner(JSONObject jsonObject) {
		int banner = 0;
		
		try {
			banner = jsonObject.getInt("banner");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return banner;
	}
	
	
	
	private void executeMLADetailsRequest(String mlaId) {
		String url = "http://50.57.224.47/html/dev/micronews/mla-info/" + mlaId;
		JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null, createMLADetailsReqSuccessListener(), createMLADetailsReqErrorListener());
		mRequestQueue.add(request);
	}
	
	
	
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
		            	intent.putExtra(IssueSummaryActivity.EXTRA_ISSUE_ITEM, selectedSubCategory);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_LOCATION, lastKnownLocation);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_CONSTITUENCY, constituency);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_MLA_NAME, name);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_MLA_PIC, url);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_DROP_BIT, dropBit);
                    intent.putExtra(IssueSummaryActivity.EXTRA_BANNER, banner);
		            	intent.putExtra(IssueSummaryActivity.EXTRA_DESCRIPTION, descriptionET.getText().toString());
		            	WindowAnimationHelper.startActivityWithSlideFromRight(IssueDetailsActivity.this, intent);
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
