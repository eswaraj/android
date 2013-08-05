package com.swaraj.vashisthg;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class YouTubeVideoActivity extends FragmentActivity {

	public static final String EXTRA_YOUTUBE_LINK = "youtubeLink";
	private WebView mWebView;
	private ProgressBar progressBar;
	private String youtubeLinkParameter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtube);
		initYouTubeLink(savedInstanceState);
		initWebView();

	}

	private void initYouTubeLink(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			Uri uri = Uri.parse(getIntent().getStringExtra(EXTRA_YOUTUBE_LINK));
			youtubeLinkParameter = uri.getQueryParameter("v");
		} else {
			youtubeLinkParameter = savedInstanceState
					.getString(EXTRA_YOUTUBE_LINK);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(EXTRA_YOUTUBE_LINK, youtubeLinkParameter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			mWebView.onResume();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			mWebView.onPause();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = (WebView) findViewById(R.id.web_view);
		progressBar = (ProgressBar) findViewById(R.id.web_progress);

		// WebView
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setAllowFileAccess(true);
		settings.setPluginState(PluginState.ON);

		mWebView.setWebChromeClient(new WebChromeClient() {					
		});
		
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.GONE);
			}
		});
		mWebView.loadData(getHTML(), "text/html", null);		
	}

	public String getHTML() {
		String html = "<iframe class=\"youtube-player\" style=\"border: 0; width: 100%; height: 95%; padding:0px; margin:0px\" id=\"ytplayer\" type=\"text/html\" src=\"http://www.youtube.com/embed/"
				+ youtubeLinkParameter
				+ "?fs=0\" frameborder=\"0\">\n"
				+ "</iframe>\n";
		return html;
	}

	public void onTitleBarLeftButtonClick(View view) {
		onBackPressed();
	}
	
	public void onTitleBarRightButtonClick(View view) {
		//do nothing
	}		
}
