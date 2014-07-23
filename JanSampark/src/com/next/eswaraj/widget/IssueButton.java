package com.next.eswaraj.widget;




public class IssueButton {
	/*
	TextView tertiaryTextView;
	TextView bottomTextView;
	ImageView imageView;
	
	public IssueButton(Activity activity) {
		super(context);
		tertiaryTextView = (TextView)activity.findViewById(id)
	}	
		

	
	private void initView(Context context, AttributeSet attrs) {

		LayoutInflater.from(getContext()).inflate(R.layout.main_button_layout, this, true);
		if(null != attrs) {
			TypedArray a = context.obtainStyledAttributes(attrs,
			        R.styleable.IssueButton, 0, 0);
			tertiaryText = a.getString(R.styleable.IssueButton_tertiaryText);
			bottomText = a.getString(R.styleable.IssueButton_bottomText);
			    
		     image = a.getDrawable(R.styleable.IssueButton_image);
			//image = a.getDrawable(imageId);
			a.recycle();
			
			
		}
		
	}
	public void setImage(int imageId){
		this.imageId = imageId;
		image = getContext().getResources().getDrawable(imageId);
	}
	public void setBottomText(String bottomText){
		this.bottomText = bottomText;
		if(bottomTextView != null){
			bottomTextView.setText(bottomText != null ? bottomText : "");	
		}
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		try {
			bottomTextView = ((TextView)findViewById(R.id.issue_button_bottom_text));
			bottomTextView.setText(bottomText != null ? bottomText : "");
			tertiaryTextView = ((TextView)findViewById(R.id.issue_button_tertiary_text));
			tertiaryTextView.setText(tertiaryText != null ? tertiaryText : "");
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
	
	public void setPercentage(int number) {
		tertiaryTextView.setText(number + "%");
	}
	*/

}
