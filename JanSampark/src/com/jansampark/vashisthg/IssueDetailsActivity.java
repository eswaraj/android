package com.jansampark.vashisthg;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class IssueDetailsActivity extends FragmentActivity {

	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	private IssueItem issueItem;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_issue_details);

		if (null == savedInstanceState) {
			issueItem = (IssueItem) getIntent().getParcelableExtra(EXTRA_ISSUE_ITEM);
		} else {
			issueItem = (IssueItem) savedInstanceState.getSerializable(EXTRA_ISSUE_ITEM);
		}
	}
}
