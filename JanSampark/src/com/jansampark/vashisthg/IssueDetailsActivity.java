package com.jansampark.vashisthg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.jansampark.vashisthg.CameraHelper.CameraUtilActivity;

public class IssueDetailsActivity extends CameraUtilActivity {

	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	private static final String SAVED_INSTANCE_IMAGE_PATH = "path";
	private IssueItem issueItem;
	
	private TextView categoryTV;
	private TextView nameTV;
	private TextView systemTV;
	
	private Button addDescription;
	private EditText descriptionET;
	private Button editDesctiption;
	
	
	private ViewGroup takeImageContainer;
	private ViewGroup imageTakenContainer;
	CameraHelper cameraHelper;
	private ImageView issueImageView;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_details);

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
		} else {
			issueItem = (IssueItem) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);
			
		}
		setCameraHelper();
		setViews();		
	}
	
	private void setCameraHelper() {
		cameraHelper = new CameraHelper(this);
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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
		descriptionET = (EditText) findViewById(R.id.issue_detail_description_text);
		
		
		categoryTV.setText(IssueFactory.getIssueCategoryName(this, issueItem.getIssueCategory()));
		nameTV.setText(issueItem.getIssueName());
		systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
		setDescription();
		initDescription();
		setIssueImageViews();
	}
	
	private void setIssueImageViews() {
		takeImageContainer = (ViewGroup) findViewById(R.id.take_photo_container);
		imageTakenContainer = (ViewGroup) findViewById(R.id.photo_taken_container);	
		issueImageView = (ImageView)  findViewById(R.id.chosen_pic);
		resetIssusImageView();
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
	
	private void initDescription() {
		descriptionET.setVisibility(View.GONE);
		descriptionET.setFocusable(false);
		descriptionET.setFocusableInTouchMode(false);
	}
	
	private void setDescription() {
		if(TextUtils.isEmpty(descriptionET.getText())) {
			addDescription.setVisibility(View.VISIBLE);
			editDesctiption.setVisibility(View.GONE);
		} else {
			addDescription.setVisibility(View.GONE);
			editDesctiption.setVisibility(View.VISIBLE);
		}
		
		addDescription.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				addDescription.setVisibility(View.INVISIBLE);
				editDesctiption.setVisibility(View.VISIBLE);
				descriptionET.setVisibility(View.VISIBLE);
				descriptionET.setFocusable(true);
				descriptionET.setFocusableInTouchMode(true);
			}
		});
		
		editDesctiption.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				descriptionET.setVisibility(View.VISIBLE);
				descriptionET.setFocusable(true);
				descriptionET.setFocusableInTouchMode(true);
				
			}
		});
		setListenerToDescriptionET();
	}
	
	private void setListenerToDescriptionET() {
		descriptionET.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int arg1, KeyEvent keyEvent) {
				descriptionET.setFocusable(true);
				descriptionET.setFocusableInTouchMode(true);
				return true;
			}
		});
	}


	public void takePhoto(View view) {
		cameraHelper.openOnlyCameraIntent();
	}
	
	public void choosePhoto(View view) {
		cameraHelper.openImageIntent();
	}
	
	public void retakePhoto(View view) {
		cameraHelper.openImageIntent();
	}
	
	public void removePhoto(View view) {
		cameraHelper.setImageName(null);
		resetIssusImageView();
	}
	
	@Override
	public void onCameraPicTaken() {				
		displayImage();
	}
	
	private void displayImage() {
		resetIssusImageView();
		new BitmapWorkerTask(issueImageView, 200).execute(cameraHelper.getImageName());
	}

	@Override
	public void onGalleryPicChosen() {
		displayImage();		
	}
	
	
}
