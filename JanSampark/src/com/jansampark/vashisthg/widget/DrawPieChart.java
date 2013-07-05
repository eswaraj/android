package com.jansampark.vashisthg.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;

public class DrawPieChart extends View {

	Paint paint = new Paint();
        //  Here you can Add number of different color according to max different colors to be displayed on Piechart
        int c[] = {Color.MAGENTA,Color.BLUE,Color.RED,Color.CYAN,Color.YELLOW,Color.GREEN};
	Canvas mCanvas = new Canvas();
	private int[] values;
	Context context;
	
	public DrawPieChart(Context context, int[] values) {
		super(context);
		paint = new Paint();
		this.values = values;
		// this.labels = labels;
		super.draw(mCanvas);
	}

 	public void draw(Canvas canvas) {
		int x = getWidth();
		int y = getHeight();
		float t = getTotal();
		paint.setColor(Color.parseColor("#78777D"));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		canvas.drawRect(0, 0, x - 1, y - 1, paint);
		int n = values.length;
		float cuurPos = -90;
		paint.setStyle(Style.FILL);
		RectF rect = new RectF(20,20,x-20,y-20);
		for (int i = 0; i < n; i++) {
			paint.setColor(c[i]);
			float thita =(t==0)?0: 360*values[i]/t;
			canvas.drawArc(rect, cuurPos, thita, true, paint);
			cuurPos = cuurPos+thita;
		}
	}

 	private float getTotal() {
		int total = 0;
		for (int i = 0; i < values.length; i++) {
			total = total + values[i];
		}
		return total;
	}
}
