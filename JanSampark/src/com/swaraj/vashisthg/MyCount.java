package com.swaraj.vashisthg;

import android.os.CountDownTimer;
import android.widget.TextView;

public class MyCount extends CountDownTimer {
	int number;
	int counter = 0;
	TextView textView;

	public static MyCount newInstance(int number, TextView textView) {

		long countDownInterval = 1;
		long millisInFuture = number * countDownInterval;
		return new MyCount(number * millisInFuture, countDownInterval, number, textView);
	}

	private MyCount(long millisInFuture, long countDownInterval, int number, TextView textView) {
		super(millisInFuture, countDownInterval);
		this.number = number;
		this.textView = textView;
	}

	@Override
	public void onFinish() {
		textView.setText("" + number);
		// complaintsNumTV.setText("" + number);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		if (!(counter > number)) {
			textView.setText("" + counter++);

		}
	}
}