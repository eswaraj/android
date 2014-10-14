package com.next.eswaraj;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eswaraj.web.dto.RegisterDeviceRequest;
import com.eswaraj.web.dto.UserDto;
import com.next.eswaraj.adapters.TextPagerAdapter;
import com.next.eswaraj.helpers.DialogFactory;
import com.next.eswaraj.helpers.MobileSessionHelper;
import com.next.eswaraj.helpers.Utils;
import com.next.eswaraj.util.DeviceUtil;
import com.next.eswaraj.util.FacebookLoginUtil;
import com.next.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.next.eswaraj.widget.ViewPagerCustomDuration;

public class SplashActivity extends FragmentActivity {

    private com.next.eswaraj.widget.ViewPagerCustomDuration pager;
    private RadioGroup radioGroup;
    private TextPagerAdapter adapter;
    Timer timer;
    String[] splashStrings;

    public static final String EXTRA_DONT_START_MAIN = "dont_start_main";
    private boolean dontStartMain = false;

    private static final int RADIO_BUTTON_STARTING_ID = 0x100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (null == savedInstanceState) {
            dontStartMain = getIntent().getBooleanExtra(EXTRA_DONT_START_MAIN, false);
        } else {
            dontStartMain = savedInstanceState.getBoolean(EXTRA_DONT_START_MAIN);
        }

        initSplashStrings();
        setView();
        setUpPager();
        setUpRadioGroup();
        registerUserAndMobile();

    }

    private void registerUserAndMobile() {
        UserDto userDto = MobileSessionHelper.getLoggedInUser(this);
        if (userDto == null) {
            String android_id = DeviceUtil.getDeviceid(this);
            RegisterDeviceRequest registerDeviceRequest = new RegisterDeviceRequest();
            registerDeviceRequest.setDeviceId(android_id);
            registerDeviceRequest.setDeviceTypeRef(DeviceUtil.getDeviceTypeRef());

            try {
                RegisterUserAndDeviceRequest registerUserAndDeviceRequest = new RegisterUserAndDeviceRequest(createMyReqErrorListener(), createMyReqSuccessListener(), registerDeviceRequest);
                RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
                registerUserAndDeviceRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, 3));
                mRequestQueue.add(registerUserAndDeviceRequest);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, R.string.network_error, Toast.LENGTH_LONG).show();
                if (null != error) {
                    Log.e("eswaraj", "" + error.getMessage(), error);
                }
            }
        };
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("details", "response: " + response);
                Log.i("eswaraj", "Activity returning " + RESULT_OK + ", " + getParent());
                MobileSessionHelper.setLoggedInUser(SplashActivity.this, response);
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_DONT_START_MAIN, dontStartMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startMainActivityIfRequired();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAnimatingGallery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    private void setView() {
        pager = (ViewPagerCustomDuration) findViewById(R.id.gallery_view_pager);
        radioGroup = (RadioGroup) findViewById(R.id.gallery_radio_group);
    }

    private void setUpPager() {
        pager = (ViewPagerCustomDuration) findViewById(R.id.gallery_view_pager);
        pager.setScrollDurationFactor(10);
        adapter = new TextPagerAdapter(getSupportFragmentManager(), this, 3);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(2);
        pager.setOnPageChangeListener(onPageChangeListener);
        pager.setCurrentItem(0);
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // int pos = (checkedId - RADIO_BUTTON_STARTING_ID);
        }
    };

    private void startAnimatingGallery() {
        if (null != timer) {
            timer.cancel();
        }
        timer = new Timer();
        startTimer();
    }

    private void startTimer() {

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                SplashActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int currentItem = pager.getCurrentItem();

                        if (currentItem == splashStrings.length - 1) {
                            Log.d("Gallery", "show first item, current:" + currentItem);
                            pager.setCurrentItem(0, true);
                        } else {
                            Log.d("Gallery", "show next item, current:" + currentItem);
                            pager.setCurrentItem(currentItem + 1, true);
                        }
                    }
                });
            }
        }, 4000, 4000);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            Log.d("Gallery", "page changed, position" + position);
            radioGroup.check(position + RADIO_BUTTON_STARTING_ID);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    private void startMainActivityIfRequired() {
        if (Utils.isFirstTimeBoot(getApplicationContext())) {

        } else {
            if (dontStartMain) {

            } else {
                // checkLocationAndInternetAndStartMainActivity();
            }
        }

    }

    public void onDoneClick(View view) {
        checkLocationAndInternetAndStartMainActivity();
    }

    public void onLoginClick(View view) {
        FacebookLoginUtil.startFacebookLogin(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookLoginUtil.onActivityResult(this, requestCode, resultCode, data);
    }

    private void checkLocationAndInternetAndStartMainActivity() {
        if (checkLocationAndInternetAccess()) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private boolean checkLocationAndInternetAccess() {
        boolean internetAccess = Utils.isOnline(this);
        boolean locationAccess = Utils.isLocationServicesEnabled(this);

        if (!(internetAccess || locationAccess)) {
            DialogFactory.createMessageDialog("No Internet and Location Services Detected", getString(R.string.no_internet) + " " + getString(R.string.no_location), onOKClickListener).show(
                    getSupportFragmentManager(), "INTERNET_LOCATION");
        } else if (!internetAccess) {
            DialogFactory.createMessageDialog("No Internet connection Detected", getString(R.string.no_internet), onOKClickListener).show(getSupportFragmentManager(), "INTERNET");
        } else if (!locationAccess) {
            DialogFactory.createMessageDialog("No Location Services Detected.", getString(R.string.no_location), onOKClickListener).show(getSupportFragmentManager(), "LOCATION");
        }

        return internetAccess && locationAccess;
    }

    private OnClickListener onOKClickListener = new OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            startMainActivity();

        }
    };

    private void initSplashStrings() {
        splashStrings = getResources().getStringArray(R.array.splash_text);
    }

    private void setUpRadioGroup() {
        radioGroup = (RadioGroup) findViewById(R.id.gallery_radio_group);
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);
        LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < splashStrings.length; i++) {
            RadioButton radio = (RadioButton) inflater.inflate(R.layout.splash_radio, null);
            radio.setId(RADIO_BUTTON_STARTING_ID + i);
            radio.setEnabled(false);
            radioGroup.addView(radio);
        }
        radioGroup.check(RADIO_BUTTON_STARTING_ID);
        radioGroup.setEnabled(false);
    }

}
