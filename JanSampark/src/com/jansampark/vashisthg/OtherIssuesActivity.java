package com.jansampark.vashisthg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.jansampark.vashisthg.helpers.WindowAnimationHelper;
import com.jansampark.vashisthg.models.IssueItem;

public class OtherIssuesActivity extends FragmentActivity {
	
	public final static String EXTRA_ISSUE_CATEGORY_ID = "issue_cat";
	
	private int issueCategory;
	private int[] otherTemplateId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_issues);
		otherTemplateId = getResources().getIntArray(R.array.other_template_id);
		
		if(null == savedInstanceState) {
			issueCategory = getIntent().getIntExtra(EXTRA_ISSUE_CATEGORY_ID, 0);
		} else {
			issueCategory = savedInstanceState.getInt(EXTRA_ISSUE_CATEGORY_ID);
		}
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(EXTRA_ISSUE_CATEGORY_ID, issueCategory);
	}
	
	public void onInfraClick(View view) {
		startIssueDetailActivity(getIssueItem(otherTemplateId[0]));
	}
	
	public void onMainteinanceClick(View view) {
		startIssueDetailActivity(getIssueItem(otherTemplateId[1]));
	}
	
	public void onQualityClick(View view) {
		startIssueDetailActivity(getIssueItem(otherTemplateId[2]));
	}
	
	public void onPricingClick(View view) {
		startIssueDetailActivity(getIssueItem(otherTemplateId[3]));
	}
	
	public void onAwarenessClick(View view) {
		startIssueDetailActivity(getIssueItem(otherTemplateId[4]));
	}
	
	
	private IssueItem getIssueItem(int templateId) {
		IssueItem item = new IssueItem();
		item.setIssueCategory(issueCategory);
		item.setIssueName(getString(R.string.others));
		item.setTemplateId(templateId);	
		return item;
	}
	
	
	private void startIssueDetailActivity(IssueItem item) {
		Intent intent = new Intent(this, IssueDetailsActivity.class);
		intent.putExtra(IssueDetailsActivity.EXTRA_ISSUE_ITEM, item);
		intent.putExtra(IssueDetailsActivity.EXTRA_LOCATION, JanSamparkApplication.getInstance().getLastKnownLocation());
		WindowAnimationHelper.startActivityWithSlideFromRight(this, intent);
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
