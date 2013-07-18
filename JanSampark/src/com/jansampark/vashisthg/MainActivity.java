package com.jansampark.vashisthg;

import com.jansampark.vashisthg.helpers.TitleBarHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

public class MainActivity extends FragmentActivity  {
	public static final String TAG = "Main";

    MainAnalyticsFragment analyticsFragment;
    MainIssueFragment issueFragment;
    private MyPagerAdapter adapter;
    
	private ViewPager viewPager;
	private RadioGroup footerTab;
	private ViewGroup titleBar;
	TitleBarHelper titleBarHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        footerTab = (RadioGroup) findViewById(R.id.main_tabs);
        titleBar = (ViewGroup) findViewById(R.id.main_title_bar);
                
        if(null == savedInstanceState) {
        	initFragments();
        }
        initViewPagingAndTabs();
        setTitleBar();
    }
    
    private void setTitleBar() {
    	titleBarHelper = new TitleBarHelper();
    	titleBarHelper.setTitleBar(titleBar);
    	titleBarHelper.setLeftButtonIcon(R.drawable.ic_info);
    	titleBarHelper.setRightButtonIcon(R.drawable.ic_edit);
    	titleBarHelper.setOnRightButtonClick(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, UserImageActivity.class));
				
			}
		});
    }
    
    
    private void initFragments() {
		analyticsFragment = MainAnalyticsFragment.newInstance(null);
		issueFragment = MainIssueFragment.newInstance(null);
	}
    
    private void initViewPagingAndTabs() {
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(mOnPageChangeListener);
		footerTab.setOnCheckedChangeListener(onCheckedChangeListener);	
	}    
    
    public class MyPagerAdapter extends FragmentStatePagerAdapter {

		public MyPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			 if (0 == position) {
				 return issueFragment;
			 } else if (1 == position) {
				 return analyticsFragment;
			 }
			 return null;
		}
	}
    
	private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.tab_issue) {				
				viewPager.setCurrentItem(0);
			} else if (checkedId == R.id.tab_analytics) {
				viewPager.setCurrentItem(1);
			}
		}
	};
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		public void onPageScrollStateChanged(int arg0) {	 }
		public void onPageScrolled(int arg0, float arg1, int arg2) {	}
		public void onPageSelected(int position) {						
			if (0 == position) {
				footerTab.check(R.id.tab_issue);
			} else {
				footerTab.check(R.id.tab_analytics);
				if(null != analyticsFragment) {
					analyticsFragment.onFragmentShown();
				}
			}	
		}
	}; 
	
	
}
