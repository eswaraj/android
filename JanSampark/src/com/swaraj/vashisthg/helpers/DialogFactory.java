package com.swaraj.vashisthg.helpers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;

import com.swaraj.vashisthg.R;
import com.swaraj.vashisthg.dialog.MessageDialog;



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
	
	public static MessageDialog createMessageDialog(String title, String message, OnClickListener onOKClickListener) {
		return MessageDialog.create(title, message, "OK", onOKClickListener);
	}
}
