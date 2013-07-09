package com.jansampark.vashisthg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by chandramouli on 1/7/13.
 */
public class IssueActivity extends Activity {

    public static final String EXTRA_ISSUE = "issue";
    
    private View issueBanner;
    private TextView issueNameTV;
    private ListView issueList;

    private ISSUES issue;
    IssueAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        setViews();
        if(null == savedInstanceState) {
            issue = (ISSUES) getIntent().getSerializableExtra(EXTRA_ISSUE);
        } else {
        	issue = (ISSUES) savedInstanceState.getSerializable(EXTRA_ISSUE);
        }

        setIssueBannerAndText();
        setListView();
    }
    
    private void setViews() {
    	issueBanner = findViewById(R.id.issue_banner);
    	issueNameTV = (TextView) findViewById(R.id.issue_name);
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
    	adapter = IssueAdapter.newInstance(this.getApplicationContext(), issue);
    	issueList.setAdapter(adapter);
    	issueList.setOnItemClickListener(listItemClickListener);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putSerializable(EXTRA_ISSUE, issue);
    }
    
    private OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			Intent intent = new Intent(IssueActivity.this, IssueSummaryActivity.class);
			intent.putExtra(IssueSummaryActivity.EXTRA_ISSUE_ITEM, (IssueItem) adapter.getItem(position));
			startActivity(intent);			
		}
	};
}
