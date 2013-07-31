package com.jansampark.vashisthg;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.jansampark.vashisthg.models.Analytics;
import com.jansampark.vashisthg.models.ISSUE_CATEGORY;
import com.jansampark.vashisthg.models.IssueItem;

/**
 * Created by gaurav vashisth on 1/7/13.
 */
public class IssueActivity extends Activity {

    public static final String EXTRA_ISSUE = "issue";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_ANALYTICS_LIST = "analytics";
    
    private View issueBanner;
    private TextView issueNameTV;
    private ListView issueList;
    private TextView numIssuesTV;
    private TextView numComplaintsTV;
    

    private ISSUE_CATEGORY issue;
    private Location location;
    IssueAdapter adapter;
    
    private View headerView;
    
    
    public static final String EXTRA_IS_ANALYTICS = "isAnalytics";
    private boolean isAnalytics;
    private ArrayList<Analytics> analyticsList;
    

    public ArrayList<Analytics> getAnalyticsList() {
    	if(null == analyticsList) {
    		analyticsList = new ArrayList<Analytics>();
    	}
		return analyticsList;
	}

	public void setAnalyticsList(ArrayList<Analytics> analyticsList) {
		this.analyticsList = analyticsList;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        setExtrasAndSavedInstance(savedInstanceState); 
        initListView();
        setHeaderView();
        setListView();
        setIssueBannerAndText();
        setNumTV();
    }
	
	private void initListView() {
		issueList = (ListView) findViewById(R.id.issue_list);
	}
    
    private void setExtrasAndSavedInstance(Bundle savedInstanceState) {
        if(null == savedInstanceState) {
        	isAnalytics = getIntent().getBooleanExtra(EXTRA_IS_ANALYTICS, false);
            issue = (ISSUE_CATEGORY) getIntent().getSerializableExtra(EXTRA_ISSUE);
            location = (Location) getIntent().getParcelableExtra(EXTRA_LOCATION);
            analyticsList =  getIntent().getParcelableArrayListExtra(EXTRA_ANALYTICS_LIST);
        } else {
        	isAnalytics = savedInstanceState.getBoolean(EXTRA_IS_ANALYTICS);
        	issue = (ISSUE_CATEGORY) savedInstanceState.getSerializable(EXTRA_ISSUE);
        	location = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
        	analyticsList = savedInstanceState.getParcelableArrayList(EXTRA_ANALYTICS_LIST);
        }
    }
    
    
    
    private void setHeaderView() {
    	headerView = getLayoutInflater().inflate(R.layout.issue_banner, issueList, false);
    	issueBanner = headerView.findViewById(R.id.issue_banner);
    	issueNameTV = (TextView) headerView.findViewById(R.id.issue_name);
    	numIssuesTV = (TextView) headerView.findViewById(R.id.issue_issues);
    	numComplaintsTV = (TextView) headerView.findViewById(R.id.issue_complaints);
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
    	
    	if(isAnalytics) {
    		adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue, R.layout.issue_analytics_row, getAnalyticsList());
    	} else {
    		adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue, R.layout.issue_row, null);
    	}
    	issueList.addHeaderView(headerView, null, false);
    	issueList.setAdapter(adapter);
    	issueList.setOnItemClickListener(listItemClickListener);
    }
    private void setNumTV() {
    	if(isAnalytics) {
    		numIssuesTV.setText(String.format(getResources().getString(R.string.issue_issues), adapter.getCount()));    		
    		numIssuesTV.setVisibility(View.VISIBLE);
    		numComplaintsTV.setVisibility(View.VISIBLE);
    		numComplaintsTV.setText(String.format(getResources().getString(R.string.issue_complaints), getTotalComplaints()));
    	} else {
    		numIssuesTV.setText(String.format(getResources().getString(R.string.issue_issues), adapter.getCount()));
    		numIssuesTV.setVisibility(View.VISIBLE);
    		numComplaintsTV.setVisibility(View.GONE);
    	}
    	
    }
    
    private int getTotalComplaints() {
    	int count = 0;
    	for (Analytics analytics : getAnalyticsList()) {
			count += analytics.getCount();
		}
    	return count;
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putSerializable(EXTRA_ISSUE, issue);
    	outState.putParcelable(EXTRA_LOCATION, location);
    	outState.putBoolean(EXTRA_IS_ANALYTICS, isAnalytics);
    	outState.putParcelableArrayList(EXTRA_ANALYTICS_LIST, getAnalyticsList());
    }
    
    private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			if(!isAnalytics) {
				if(position != adapterView.getCount() - 1) {
					Intent intent = new Intent(IssueActivity.this, IssueDetailsActivity.class);
					intent.putExtra(IssueDetailsActivity.EXTRA_ISSUE_ITEM, (IssueItem) adapter.getItem(position - 1));
					intent.putExtra(IssueDetailsActivity.EXTRA_LOCATION, location);
					startActivity(intent);	
				} else {
					Intent intent = new Intent(IssueActivity.this, OtherIssuesActivity.class);
					intent.putExtra(OtherIssuesActivity.EXTRA_ISSUE_CATEGORY_ID, IssueFactory.getIssueId(IssueActivity.this, issue));
					startActivity(intent);
				}
			}
		}
	};
	
	public void onTitleBarLeftButtonClick(View view) {
		
	}
	
	public void onTitleBarRightButtonClick(View view) {
		
	}	
}
