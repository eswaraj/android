package com.jansampark.vashisthg.helpers;

import android.app.Activity;
import android.app.ProgressDialog;

import com.jansampark.vashisthg.R;
import com.jansampark.vashisthg.dialog.MessageDialog;



public class DialogFactory {

	private static ProgressDialog mProgressDialog;
	
	
	public static void showPleaseWaitProgressDialog(Activity context) {
		showProgressDialog(context, R.string.dialog_please_wait);
	}
	
	public static void showProgressDialog(Activity context, int resId) {				
		hideProgressDialog();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(mProgressDialog.getContext().getString(resId));		
		mProgressDialog.show();		
	}
	
	public static void hideProgressDialog() {
		try {
			if (null != mProgressDialog) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MessageDialog createMessageDialog(String message) {
		return MessageDialog.create(message);
	}
	
	public static MessageDialog createMessageDialog(String title, String message) {
		return MessageDialog.create(title, message);
	}
}
