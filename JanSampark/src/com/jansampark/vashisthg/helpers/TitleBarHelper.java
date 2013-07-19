package com.jansampark.vashisthg.helpers;

import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jansampark.vashisthg.R;

public class TitleBarHelper {

	private ViewGroup titleBar;
	private ImageButton leftButton;
	private ImageButton rightButton;

	public ViewGroup getTitleBar() {
		return titleBar;
	}

	public void setTitleBar(ViewGroup titleBar) {
		this.titleBar = titleBar;
		this.rightButton =  (ImageButton) titleBar.findViewById(R.id.title_bar_right_button);
		this.leftButton = (ImageButton) titleBar.findViewById(R.id.title_bar_left_button);
	}
	
	public ImageButton getRightButton() {
		return rightButton;
	}
	
	public ImageButton getLeftButton() {
		return leftButton;
	}
	
	public void setRightButtonIcon(int resId) {
		rightButton.setImageResource(resId);
	}
	
	public void setLeftButtonIcon(int resId) {
		leftButton.setImageResource(resId);
	}
	
	public void setOnRightButtonClick(OnClickListener onClickListener) {
		getRightButton().setOnClickListener(onClickListener);
	}
	
	public void setOnLeftButtonClick(OnClickListener onClickListener) {
		getLeftButton().setOnClickListener(onClickListener);
	}
	
	public ImageView getCenterImageView() {
		return (ImageView) titleBar.findViewById(R.id.title_bar_center_image);
	}

}
