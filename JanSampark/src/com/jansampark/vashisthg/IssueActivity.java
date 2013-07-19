package com.jansampark.vashisthg;

import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.models.IssueItem;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by gaurav vashisth on 1/7/13.
 */
public class IssueActivity extends Activity {

    public static final String EXTRA_ISSUE = "issue";
    public static final String EXTRA_LOCATION = "location";
    
    private View issueBanner;
    private TextView issueNameTV;
    private ListView issueList;
    private TextView numIssuesTV;
    private TextView numViewsTV;
    

    private ISSUE_CATEGORY issue;
    private Location location;
    IssueAdapter adapter;
    
    
    public static final String EXTRA_IS_ANALYTICS = "isAnalytics";
    public boolean isAnalytics;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        setViews();
        if(null == savedInstanceState) {
        	isAnalytics = getIntent().getBooleanExtra(EXTRA_IS_ANALYTICS, false);
            issue = (ISSUE_CATEGORY) getIntent().getSerializableExtra(EXTRA_ISSUE);
            location = (Location) getIntent().getParcelableExtra(EXTRA_LOCATION);
        } else {
        	isAnalytics = savedInstanceState.getBoolean(EXTRA_IS_ANALYTICS);
        	issue = (ISSUE_CATEGORY) savedInstanceState.getSerializable(EXTRA_ISSUE);
        	location = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
        }

        setIssueBannerAndText();
        setListView();
        setNumTV();
    }
    
    private void setViews() {
    	issueBanner = findViewById(R.id.issue_banner);
    	issueNameTV = (TextView) findViewById(R.id.issue_name);
    	numIssuesTV = (TextView) findViewById(R.id.issue_issues);
    	numViewsTV = (TextView) findViewById(R.id.issue_views);
    }

    private void setIssueBannerAndText() {
    	int bannerResource = 0;
    	int nameResource = 0;
        switch (issue) {
            case WATER:
            	bannerResource = R.drawable.banner_water;
            	nameResource = R.string.main_water;
                break;
            case ELECTRICITY:
            	bannerResource = R.drawable.banner_electricity;
            	nameResource = R.string.main_electricity;
            	break;
            case LAW:
            	bannerResource = R.drawable.banner_law;
            	nameResource = R.string.main_law_and_order;
            	break;
            case ROAD:
            	bannerResource = R.drawable.banner_road;
            	nameResource = R.string.main_road;
            	break;
            case SEWAGE:
            	bannerResource = R.drawable.banner_sewage;
            	nameResource = R.string.main_sewage;
            	break;
            case TRANSPORT:
            	bannerResource = R.drawable.banner_transport;
            	nameResource = R.string.main_transportation;
            	break;
            default:
            	throw new IllegalArgumentException();
            	
        }
        issueBanner.setBackgroundResource(bannerResource);
        issueNameTV.setText(nameResource);
    }
    
    private void setListView() {
    	issueList = (ListView) findViewById(R.id.issue_list);
    	if(isAnalytics) {
    		adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue, R.layout.issue_analytics_row);
    	} else {
    		adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue, R.layout.issue_row);
    	}
    	issueList.setAdapter(adapter);
    	issueList.setOnItemClickListener(listItemClickListener);
    }
    private void setNumTV() {
    	if(isAnalytics) {
    		
    	} else {
    		numIssuesTV.setText(String.format(getResources().getString(R.string.issue_issues), adapter.getCount()));
    		numIssuesTV.setVisibility(View.VISIBLE);
    		numViewsTV.setVisibility(View.INVISIBLE);
    	}
    	
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putSerializable(EXTRA_ISSUE, issue);
    	outState.putParcelable(EXTRA_LOCATION, location);
    	outState.putBoolean(EXTRA_IS_ANALYTICS, isAnalytics);
    }
    
    private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			if(!isAnalytics) {
				Intent intent = new Intent(IssueActivity.this, IssueDetailsActivity.class);
				intent.putExtra(IssueDetailsActivity.EXTRA_ISSUE_ITEM, (IssueItem) adapter.getItem(position));
				intent.putExtra(IssueDetailsActivity.EXTRA_LOCATION, location);
				startActivity(intent);			
			}
		}
	};
}
