package com.jansampark.vashisthg.helpers;

import com.jansampark.vashisthg.R;

import android.app.Activity;
import android.app.ProgressDialog;



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
}
