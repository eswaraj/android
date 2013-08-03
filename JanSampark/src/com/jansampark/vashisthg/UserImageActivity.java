package com.jansampark.vashisthg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jansampark.vashisthg.helpers.BitmapWorkerTask;
import com.jansampark.vashisthg.helpers.CameraHelper;
import com.jansampark.vashisthg.helpers.WindowAnimationHelper;
import com.jansampark.vashisthg.helpers.CameraHelper.CameraUtilActivity;
import com.jansampark.vashisthg.helpers.Utils;

public class UserImageActivity extends CameraUtilActivity {
	CameraHelper cameraHelper;
	private ViewGroup takeImageContainer;
	private ViewGroup imageTakenContainer;
	private ImageView issueImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_image);
		setCameraHelper();
		setIssueImageViews();
	}
	
	private void setCameraHelper() {
		cameraHelper = new CameraHelper(this);		
	}
	
	private void setIssueImageViews() {
		takeImageContainer = (ViewGroup) findViewById(R.id.take_photo_container_ref);
		imageTakenContainer = (ViewGroup) findViewById(R.id.photo_taken_container_ref);	
		issueImageView = (ImageView)  findViewById(R.id.chosen_pic);		
		displayImageIfAvailable();
	}
	
	private void displayImageIfAvailable() {
		resetIssusImageView();
		String path = Utils.getUserImage(this);
		if(isUserImageExists()) {
			new BitmapWorkerTask(issueImageView, 200).execute(path);
		}
	}
	
	private void resetIssusImageView() {
		if(!isUserImageExists()) {
			takeImageContainer.setVisibility(View.VISIBLE);
			imageTakenContainer.setVisibility(View.GONE);
		} else {
			takeImageContainer.setVisibility(View.GONE);
			imageTakenContainer.setVisibility(View.VISIBLE);			
		}
	}
	
	private boolean isUserImageExists() {
		String path = Utils.getUserImage(this);
		Log.d("image", "user Image: " + path);
		if(!TextUtils.isEmpty(path)) {			
			return true;
		}
		return false;
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
		Utils.removeUserImage(this);
		displayImageIfAvailable();	
	}
	
	public void onDoneClick(View view) {
		onBackPressed();
	}

	@Override
	public void onCameraPicTaken() {
		setUserImage();			
	}

	@Override
	public void onGalleryPicChosen() {
		setUserImage();		
	}
	
	private void setUserImage() {
		Utils.setUserImage(this, cameraHelper.getImageName());
		displayImageIfAvailable();
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
