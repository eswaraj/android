package com.jansampark.vashisthg.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MessageDialog extends DialogFragment {

	private String mTitle;
	private String mPositiveBtnText = "Ok";
	private String mNegativeBtnText;
	private String mBody;
	private OnClickListener mPositiveButtonListener;
	private OnClickListener mNegativeButtonListener;
	
	public static MessageDialog create(String message){
		return create(null, message, "Ok", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { }
		});
	}
	
	public static MessageDialog create(String title, String message){
		return create(title, message, "Ok", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { }
		});
	}
	
	
	
	public static MessageDialog create(String title, String body, String positiveBtnTitle, OnClickListener positiveBtnListener) {
		MessageDialog dialog = new MessageDialog();
		dialog.mTitle  = title;
		dialog.mBody = body;
		dialog.mPositiveBtnText = positiveBtnTitle;
		dialog.mPositiveButtonListener = positiveBtnListener;
		return dialog;
	}
	
	public static MessageDialog create(String title, String body, String positiveBtnTitle, String negativeBtnTitle, 
	                                   OnClickListener positiveBtnListener, OnClickListener negativeBtnListener) 
	{
	    MessageDialog dialog = create(title, body, positiveBtnTitle, positiveBtnListener);
	    dialog.mNegativeBtnText = negativeBtnTitle;
	    dialog.mNegativeButtonListener = negativeBtnListener;
        return dialog;
    }
	
	public MessageDialog() {
		super();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		if (null != mTitle) b.setTitle(mTitle);
		if (null != mBody) b.setMessage(mBody);
		if (null != mNegativeBtnText && null != mNegativeButtonListener) {
		    b.setNegativeButton(mNegativeBtnText, mNegativeButtonListener);
		}
		b.setPositiveButton(mPositiveBtnText, mPositiveButtonListener);
		
        return b.create();
	}
}
