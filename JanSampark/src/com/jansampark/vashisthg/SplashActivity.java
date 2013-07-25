package com.jansampark.vashisthg;



import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.jansampark.vashisthg.helpers.Utils;
import com.jansampark.vashisthg.widget.ViewPagerCustomDuration;

public class SplashActivity extends FragmentActivity {

	
	private com.jansampark.vashisthg.widget.ViewPagerCustomDuration pager;
	private RadioGroup radioGroup;
	private TextPagerAdapter adapter;
	Timer timer;
	String[] splashStrings;
	
	private static final int RADIO_BUTTON_STARTING_ID = 0x100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startMainActivityIfRequired();
        initSplashStrings();
        setView();
        setUpPager();
        setUpRadioGroup();    
        startAnimatingGallery();
    }

    private void setView() {
    	pager = (ViewPagerCustomDuration)  findViewById(R.id.gallery_view_pager);
    	radioGroup = (RadioGroup) findViewById(R.id.gallery_radio_group);
    }
    
    private void setUpPager() {
    	pager = (ViewPagerCustomDuration) findViewById(R.id.gallery_view_pager);
    	pager.setScrollDurationFactor(4);
		adapter = new TextPagerAdapter(getSupportFragmentManager(), this, 3);
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
	
	private void startAnimatingGallery() {
		if (null != timer) {
			timer.cancel();
		}
		timer = new Timer();
		startTimer();
	}
	
	private void startTimer() {
		
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				SplashActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						int currentItem = pager.getCurrentItem();
						
						if (currentItem == splashStrings.length - 1) {
							Log.d("Gallery", "show first item, current:" + currentItem);
							pager.setCurrentItem(0, true);
						} else {
							Log.d("Gallery", "show next item, current:" + currentItem);
							pager.setCurrentItem(currentItem + 1, true);
						}				
					}
				});
			}			
		}, 2000, 2000);	
	}
	
	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			Log.d("Gallery", "page changed, position" + position);
			radioGroup.check(position + RADIO_BUTTON_STARTING_ID);			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	
	private void startMainActivityIfRequired() {
		if(Utils.isFirstTimeBoot(getApplicationContext())) {
			
		} else {
			startMainActivity();
		}
		
	}
	
	public void onDoneClick(View view) {
		startMainActivity();
	}

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    
    private void initSplashStrings() {
    	splashStrings = getResources().getStringArray(R.array.splash_text);
    }
    
    private void setUpRadioGroup() {
		radioGroup = (RadioGroup) findViewById(R.id.gallery_radio_group);
		radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
		LayoutInflater inflater = getLayoutInflater();
		
		for(int i = 0; i< splashStrings.length ; i++) {
			RadioButton radio = (RadioButton) inflater.inflate(R.layout.splash_radio, null);
			radio.setId(RADIO_BUTTON_STARTING_ID + i);
			radio.setEnabled(false);
		    radioGroup.addView(radio);
		}
		radioGroup.check(RADIO_BUTTON_STARTING_ID);
		radioGroup.setEnabled(false);
	}
    
}
