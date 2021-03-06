package com.next.eswaraj;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.next.eswaraj.adapters.IssueAdapter;
import com.next.eswaraj.helpers.WindowAnimationHelper;
import com.next.eswaraj.helpers.YouTubeVideoHelper;
import com.next.eswaraj.models.Analytics;
import com.next.eswaraj.models.ISSUE_CATEGORY;

/**
 * Created by gaurav vashisth on 1/7/13.
 */
public class IssueActivity extends Activity {

    public static final String EXTRA_ISSUE = "issue";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_ANALYTICS_LIST = "analytics";
    public static final String EXTRA_CATEGORY = "EC";
    
    private View issueBanner;
    private TextView issueNameTV;
    private ListView issueList;
    private TextView numIssuesTV;
    private TextView numComplaintsTV;
    

    private ISSUE_CATEGORY issue;
    int issueId;
    private Location location;
    IssueAdapter adapter;
    
    private View headerView;
    
    
    
    
    public static final String EXTRA_IS_ANALYTICS = "isAnalytics";
    private boolean isAnalytics;
    private ArrayList<Analytics> analyticsList;
    private List<CategoryWithChildCategoryDto> subCategories;
    private CategoryWithChildCategoryDto selectedCategory;
    YouTubeVideoHelper youtubeHelper;

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
        youtubeHelper = new YouTubeVideoHelper(this);
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
            selectedCategory = (CategoryWithChildCategoryDto)getIntent().getSerializableExtra(EXTRA_CATEGORY);
            subCategories = selectedCategory.getChildCategories();
        } else {
        	isAnalytics = savedInstanceState.getBoolean(EXTRA_IS_ANALYTICS);
        	issue = (ISSUE_CATEGORY) savedInstanceState.getSerializable(EXTRA_ISSUE);
        	location = (Location) savedInstanceState.getParcelable(EXTRA_LOCATION);
        	analyticsList = savedInstanceState.getParcelableArrayList(EXTRA_ANALYTICS_LIST);
        	selectedCategory = (CategoryWithChildCategoryDto)savedInstanceState.getSerializable(EXTRA_CATEGORY);
        	subCategories = selectedCategory.getChildCategories();
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
    	issue = ISSUE_CATEGORY.WATER;
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
        issueNameTV.setText(selectedCategory.getName());
    }
    
    private void setListView() {
    	
    	if(isAnalytics) {
    		adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue, R.layout.issue_analytics_row, getAnalyticsList(), subCategories);
    	} else {
    		adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue, R.layout.issue_row, null, subCategories);
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
                Toast.makeText(IssueActivity.this, "Clicked on " + ((CategoryWithChildCategoryDto) adapter.getItem(position - 1)).getName(), Toast.LENGTH_SHORT);
                Intent intent = new Intent(IssueActivity.this, IssueDetailsActivity.class);
                intent.putExtra(IssueDetailsActivity.EXTRA_ISSUE_ITEM, (CategoryWithChildCategoryDto) adapter.getItem(position - 1));
                intent.putExtra(IssueDetailsActivity.EXTRA_LOCATION, location);
                WindowAnimationHelper.startActivityWithSlideFromRight(IssueActivity.this, intent);
			}
		}
	};
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("eswaraj", "Activity returned " + requestCode + ", " + resultCode);
        finish();
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
	
	public void onVideoClick(View view) {
		String link = selectedCategory.getVideoUrl();
		if(!TextUtils.isEmpty(link)) {
            youtubeHelper.startYouTubeVideo(this, link);
		}
	}
}
