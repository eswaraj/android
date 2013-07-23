package com.jansampark.vashisthg.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jansampark.vashisthg.R;



public class IssueButton extends LinearLayout {
	String tertiaryText;
	String bottomText;
	Drawable image;
	
	public IssueButton(Context context) {
		this(context, null);
	}	
	
//	public IssueButton(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		initView(context, attrs);		
//	}
	
	public IssueButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context, attrs);
	}

	
	private void initView(Context context, AttributeSet attrs) {
//		View.inflate(getContext(), R.layout.main_button_layout, this);
		LayoutInflater.from(getContext()).inflate(R.layout.main_button_layout, this, true);
		if(null != attrs) {
			TypedArray a = context.obtainStyledAttributes(attrs,
			        R.styleable.IssueButton, 0, 0);
			tertiaryText = a.getString(R.styleable.IssueButton_tertiaryText);
			bottomText = a.getString(R.styleable.IssueButton_bottomText);
			    
		     image = a.getDrawable(R.styleable.IssueButton_image);
			a.recycle();
			
			
		}
		
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		try {
			((TextView)findViewById(R.id.issue_button_bottom_text)).setText(bottomText != null ? bottomText : "");
			((TextView)findViewById(R.id.issue_button_tertiary_text)).setText(tertiaryText != null ? tertiaryText : "");
			((ImageView)findViewById(R.id.issue_button_image)).setImageDrawable(image);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	    super.onLayout(changed, left, top, right, bottom);
	    int itemWidth = (right-left)/getChildCount();
	     for(int i=0; i< this.getChildCount(); i++){
	         View v = getChildAt(i);
		 v.layout(itemWidth*i, top, (i+1)*itemWidth, bottom);
	     }
	}

}
