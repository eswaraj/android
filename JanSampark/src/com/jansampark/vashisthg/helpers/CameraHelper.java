package com.jansampark.vashisthg.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileUrl);
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        
        Intent chooserIntent =  Intent.createChooser(galleryIntent, "Select Source");
        
        
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
        
        activity.startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
        
        
    }
    
    public void openOnlyGalleryIntent() {
    	setOutputImageUri();
    	 final Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	 activity.startActivityForResult(galleryIntent, PICTURE_REQUEST_CODE);
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
             intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageFileUrl);
             cameraIntents.add(intent);
         }
         Intent intent = cameraIntents.remove(cameraIntents.size() -1);
         Intent chooserIntent =  Intent.createChooser(intent, "Select Source");
         
         
         // Add the camera options.
         chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[] {}));
         
         activity.startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
    }
    
    private Uri tempImageFileUrl;

    public  void setOutputImageUri() {
        // Determine Uri of camera image to save.
        File root;
        root = new File(Environment.getExternalStorageDirectory() + File.separator + "JanSampark" + File.separator);
        root.mkdirs();
        final String name = Utils.getUniqueImageFilename();
        final String fname = name + ".png";
        final File sdImageMainDirectory = new File(root, fname);
        imagePath = sdImageMainDirectory.getAbsolutePath();
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        
        final String tempName = name + "_temp.png" ;
        
        final File sdImageTemp = new File(root, tempName);
        
        tempImageFileUrl = Uri.fromFile(sdImageTemp);
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
                    selectedImageUri = tempImageFileUrl;
                    compressAndSaveImage(Utils.getRealPathFromURI(activity, selectedImageUri), getOutputFileUri().getPath());
                    setImageName(getOutputFileUri().getPath());
                    onCameraPicTaken();
                } else {
                    selectedImageUri = data == null ? null : data.getData();  
                	compressAndSaveImage(Utils.getRealPathFromURI(activity, selectedImageUri), getOutputFileUri().getPath());
                    setImageName(getOutputFileUri().getPath());
                	if(TextUtils.isEmpty(getImageName())) {
                    } else {
                    	onGalleryPicChosen();
                    }
                }
            }
        }
    }
        
    private static void compressAndSaveImage(String selectedImage, String imagePath) {    	
    	OutputStream stream = null;
    	try {
    		stream = new FileOutputStream(imagePath);
    		Bitmap bmp = BitmapFactory.decodeFile(selectedImage);        	
        	bmp.compress(CompressFormat.JPEG, 10, stream);
    	} catch (FileNotFoundException e) {
    	    e.printStackTrace();
    	}   	
    	try {
    	    stream.close();
    	    stream = null;
    	} catch (IOException e) {
    	    e.printStackTrace();
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
	}	

	public void onRestoreInstanceState(Bundle savedInstanceState) {		
		imagePath = savedInstanceState.getString(IMAGE_NAME_INSTANCE);
	}

	public boolean isShowGallery() {
		return showGallery;
	}

	public void setShowGallery(boolean showGallery) {
		this.showGallery = showGallery;
	}
}