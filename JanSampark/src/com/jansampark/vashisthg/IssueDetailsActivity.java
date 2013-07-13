package com.jansampark.vashisthg;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class IssueDetailsActivity extends FragmentActivity {

	public static final String EXTRA_ISSUE_ITEM = "issueItem";
	private IssueItem issueItem;
	
	private TextView categoryTV;
	private TextView nameTV;
	private TextView systemTV;
	
	private Button addDescription;
	private EditText descriptionET;
	private Button editDesctiption;
	
	
	
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
		addDescription = (Button) findViewById(R.id.issue_detail_descption_add_btn);
		editDesctiption = (Button) findViewById(R.id.issue_detail_descption_edit_btn);
		descriptionET = (EditText) findViewById(R.id.issue_detail_description_text);
		
		
		categoryTV.setText(IssueFactory.getIssueCategoryName(this, issueItem.getIssueCategory()));
		nameTV.setText(issueItem.getIssueName());
		systemTV.setText(IssueFactory.getIssueTypeString(this, issueItem.getTemplateId()));
		setDescription();
		initDescription();
	}
	
	private void initDescription() {
		descriptionET.setVisibility(View.GONE);
		descriptionET.setFocusable(false);
		descriptionET.setFocusableInTouchMode(false);
	}
	
	private void setDescription() {
		if(TextUtils.isEmpty(descriptionET.getText())) {
			addDescription.setVisibility(View.VISIBLE);
			editDesctiption.setVisibility(View.GONE);
		} else {
			addDescription.setVisibility(View.GONE);
			editDesctiption.setVisibility(View.VISIBLE);
		}
		
		addDescription.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				addDescription.setVisibility(View.INVISIBLE);
				editDesctiption.setVisibility(View.VISIBLE);
				descriptionET.setVisibility(View.VISIBLE);
				descriptionET.setFocusable(true);
				descriptionET.setFocusableInTouchMode(true);
			}
		});
		
		editDesctiption.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				descriptionET.setVisibility(View.VISIBLE);
				descriptionET.setFocusable(true);
				descriptionET.setFocusableInTouchMode(true);
				
			}
		});
		setListenerToDescriptionET();
	}
	
	private void setListenerToDescriptionET() {
		descriptionET.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView textView, int arg1, KeyEvent keyEvent) {
				descriptionET.setFocusable(true);
				descriptionET.setFocusableInTouchMode(true);
				return true;
			}
		});
	}
	
	
}
