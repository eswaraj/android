package com.jansampark.vashisthg;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by chandramouli on 1/7/13.
 */
public class IssueActivity extends Activity {

    public static final String EXTRA_ISSUE = "issue";

    private MainActivity.ISSUES issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        if(null == savedInstanceState) {
            issue = (MainActivity.ISSUES)getIntent().getSerializableExtra(EXTRA_ISSUE);
        }

        setBannerImage();
    }

    private void setBannerImage() {
        switch (issue) {
            case WATER:
                break;
        }
    }



}
