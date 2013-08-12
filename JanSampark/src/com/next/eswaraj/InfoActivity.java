package com.next.eswaraj;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.next.eswaraj.helpers.WindowAnimationHelper;

public class InfoActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_info);
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
