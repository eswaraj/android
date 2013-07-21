package com.jansampark.vashisthg;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.jansampark.vashisthg.widget.ViewPagerCustomDuration;

public class SplashActivity extends Activity {

	
	private com.jansampark.vashisthg.widget.ViewPagerCustomDuration pager;
	private RadioGroup radioGroup;
	private TextPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startMainActivityAfterDelay();
        setView();
        setUpPager();
    }

    private void setView() {
    	pager = (ViewPagerCustomDuration)  findViewById(R.id.gallery_view_pager);
    	radioGroup = (RadioGroup) findViewById(R.id.gallery_radio_group);
    }
    
    private void setUpPager() {
    	pager = (ViewPagerCustomDuration) findViewById(R.id.gallery_view_pager);
    	pager.setScrollDurationFactor(4);
		adapter = new TextPagerAdapter(this);
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(2);
		pager.setOnPageChangeListener(onPageChangeListener);
		pager.setCurrentItem(0);
	}
    
private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			//int pos = (checkedId - RADIO_BUTTON_STARTING_ID);						
		}
	};
	
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			Log.d("Gallery", "page changed, position" + position);
			//mRadioGroup.check(position + RADIO_BUTTON_STARTING_ID);			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	
	

    private void startMainActivityAfterDelay() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGalleryActivity();
                        finish();
                    }
                }, 2000);

            }
        });
    }

    private void startGalleryActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
    
}
