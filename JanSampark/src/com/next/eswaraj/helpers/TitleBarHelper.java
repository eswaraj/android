package com.next.eswaraj.helpers;

import com.next.eswaraj.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TitleBarHelper {

	private ViewGroup titleBar;
	private ImageButton leftButton;
	private ImageButton rightButton;
	private ProgressBar progressBar;
	private TextView titleBarText;
	private View titleBarImage;

	public ViewGroup getTitleBar() {
		return titleBar;
	}

	public void setTitleBar(ViewGroup titleBar) {
		this.titleBar = titleBar;
		this.rightButton = (ImageButton) titleBar
				.findViewById(R.id.title_bar_right_button);
		this.leftButton = (ImageButton) titleBar
				.findViewById(R.id.title_bar_left_button);
		this.progressBar = (ProgressBar) titleBar
				.findViewById(R.id.title_bar_progress);
		this.titleBarText = (TextView) titleBar
				.findViewById(R.id.title_bar_text);
		this.titleBarImage = titleBar.findViewById(R.id.title_bar_center_image);
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

	public void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
	}

	public void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
	}

	public void setTitleBarText(int resId) {
		titleBarText.setText(resId);
		titleBarImage.setVisibility(View.GONE);
	}

}
