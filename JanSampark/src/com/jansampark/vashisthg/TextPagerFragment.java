package com.jansampark.vashisthg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextPagerFragment extends Fragment {
	private static final String TEXT = "resId";
	private TextView mTextView;

	static TextPagerFragment newInstance(String text) {
		final TextPagerFragment f = new TextPagerFragment();
		final Bundle args = new Bundle();
		args.putString(TEXT, text);
		f.setArguments(args);
		return f;
	}

	public TextPagerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.splash_pager_text, container, false);
		mTextView = (TextView) v.findViewById(R.id.splash_text);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);			
		mTextView.setText(getArguments().getString(TEXT));
	}
}