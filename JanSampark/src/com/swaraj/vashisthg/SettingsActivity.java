package com.swaraj.vashisthg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.swaraj.vashisthg.R;
import com.swaraj.vashisthg.helpers.BitmapWorkerTask;
import com.swaraj.vashisthg.helpers.CameraHelper;
import com.swaraj.vashisthg.helpers.Utils;
import com.swaraj.vashisthg.helpers.WindowAnimationHelper;
import com.swaraj.vashisthg.helpers.YouTubeVideoHelper;
import com.swaraj.vashisthg.helpers.CameraHelper.CameraUtilActivity;

public class SettingsActivity extends CameraUtilActivity {
	CameraHelper cameraHelper;
	private ViewGroup takeImageContainer;
	private ViewGroup imageTakenContainer;
	private ImageView issueImageView;
	YouTubeVideoHelper youtubeHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setYouTubeHelper();
		setCameraHelper();
		setIssueImageViews();
	}
	
	private void setYouTubeHelper() {
		youtubeHelper = new YouTubeVideoHelper(this);
		youtubeHelper.downloadYouTubeLinks();
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
	
	public void onHowItWorksClick(View view) {
		
		Intent intent = new Intent(this, SplashActivity.class);
		intent.putExtra(SplashActivity.EXTRA_DONT_START_MAIN, true);
		startActivity(intent);
		finish();
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
	
	public void onVideoClick(View view) {
		String youtubeLink = youtubeHelper.getLinkForAll();
		if(!TextUtils.isEmpty(youtubeLink)) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse(youtubeHelper.getLinkForAll())));
		}
	}
	
	public void onSendYourFeedback(View view) {		
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.settings_feedback_email_subject));
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {getString(R.string.settings_feedback_email_id)});
        startActivityForResult(Intent.createChooser(emailIntent, "Send Feedback..."), 110);
	}
	
	@Override
    public void finish() {
        super.finish();
        WindowAnimationHelper.finish(this);
    }
	
}
