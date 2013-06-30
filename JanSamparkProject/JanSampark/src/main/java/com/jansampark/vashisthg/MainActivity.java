package com.jansampark.vashisthg;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    public static enum ISSUES {
        WATER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onSewageClick(View view) {

    }

    public void onTransportationClick(View view) {

    }

    public void onWaterClick(View view) {
        openIssueActivity(ISSUES.WATER);
    }

    public void onRoadClick(View view) {

    }

    public void onElectricityClick(View view) {

    }

    public void onLawAndOrderClick(View view) {

    }

    private void openIssueActivity(ISSUES issue) {
        Intent intent = new Intent(this, IssueActivity.class);
        intent.putExtra(IssueActivity.EXTRA_ISSUE, issue);
        startActivity(intent);
    }
}
