package com.jansampark.vashisthg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jansampark.vashisthg.helpers.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

public class CameraHelper {
	public static interface CameraHelperCallback  {
		public void onCameraPicTaken();
		public void onGalleryPicChosen();
	}
	
	public static abstract class CameraUtilActivity extends FragmentActivity implements CameraHelperCallback {
		
	}
	
	

	public static final int PICTURE_REQUEST_CODE = 0x10;
	//private static final Log logger = new Log();

	private Uri outputFileUri;
	private String imagePath;
	private int overLayType;
	private boolean showGallery;

	public int getOverLayType() {
		return overLayType;
	}
	private static final String IMAGE_NAME_INSTANCE = "image_name";
	private static final String OVERLAY_TYPE_INSTANCE = "overlay_type_instance";


	public void setOverLayType(int overLayType) {
		this.overLayType = overLayType;
	}

	private CameraUtilActivity activity;

	public CameraHelper(CameraUtilActivity activity) {
		this.activity = activity;
		setShowGallery(true);
	}


	public void setImageName(String imageName) {
		this.imagePath = imageName;
	}

	public Uri getOutputFileUri() {
		return outputFileUri;
	}

	public String getImageName() {
		return imagePath;
	}

    public  void openImageIntent( ) {
        setOutputImageUri();
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = activity.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        
        Intent chooserIntent =  Intent.createChooser(galleryIntent, "Select Source");
        
        
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
        
        activity.startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
        
        
    }
    
    public void openOnlyCameraIntent() {
    	 setOutputImageUri();
         // Camera.
         final List<Intent> cameraIntents = new ArrayList<Intent>();
         final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
         final PackageManager packageManager = activity.getPackageManager();
         final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
         for (ResolveInfo res : listCam) {
             final String packageName = res.activityInfo.packageName;
             final Intent intent = new Intent(captureIntent);
             intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
             intent.setPackage(packageName);
             intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
             cameraIntents.add(intent);
         }
         Intent intent = cameraIntents.remove(cameraIntents.size() -1);
         Intent chooserIntent =  Intent.createChooser(intent, "Select Source");
         
         
         // Add the camera options.
         chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
         
         activity.startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
    }

    public  void setOutputImageUri() {
        // Determine Uri of camera image to save.
        File root;
        root = new File(Environment.getExternalStorageDirectory() + File.separator + "PictureEquality" + File.separator);
        root.mkdirs();
        final String fname = Utils.getUniqueImageFilename() + ".png";
        final File sdImageMainDirectory = new File(root, fname);
        imagePath = sdImageMainDirectory.getAbsolutePath();
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        //logger.d("Output file Uri" + outputFileUri.getPath() + ", path :" + imagePath);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CameraHelper.PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = getOutputFileUri();
                    onCameraPicTaken();
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    setImageName(Utils.getRealPathFromURI(activity, selectedImageUri));                                      
                    if(TextUtils.isEmpty(getImageName())) {
                    } else {
                    	onGalleryPicChosen();
                    }
                }
            }
        }
    }
    
    public void onCameraPicTaken() {
    	((CameraHelperCallback)activity).onCameraPicTaken();
    }
    
    public void onGalleryPicChosen() {
    	((CameraHelperCallback)activity).onGalleryPicChosen();
    }
    
    

  
    	
	public void onSaveInstanceState(Bundle outState) {		
		outState.putString(IMAGE_NAME_INSTANCE, imagePath);
		outState.putInt(OVERLAY_TYPE_INSTANCE, overLayType);
	}	

	public void onRestoreInstanceState(Bundle savedInstanceState) {		
		imagePath = savedInstanceState.getString(IMAGE_NAME_INSTANCE);
		overLayType = savedInstanceState.getInt(OVERLAY_TYPE_INSTANCE);
	}

	public boolean isShowGallery() {
		return showGallery;
	}

	public void setShowGallery(boolean showGallery) {
		this.showGallery = showGallery;
	}
}