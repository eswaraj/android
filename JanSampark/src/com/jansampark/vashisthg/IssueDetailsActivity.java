package com.jansampark.vashisthg;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class IssueDetailsActivity extends FragmentActivity {

	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	private IssueItem issueItem;
	
	private TextView categoryTV;
	private TextView nameTV;
	private TextView systemTV;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_details);

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
		} else {
			issueItem = (IssueItem) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);
		}
		
		setViews();
	}
	
	private void setViews() {
		categoryTV = (TextView) findViewById(R.id.issue_detail_category_text);
		nameTV = (TextView) findViewById(R.id.issue_detail_name_text);
		systemTV = (TextView) findViewById(R.id.issue_detail_system_text);
		
		categoryTV.setText(issueItem.getIssueId() + "");
		nameTV.setText(issueItem.getIssueName());
		systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
	}
}
