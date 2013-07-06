package com.jansampark.vashisthg.widget;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.view.MotionEvent;

import com.jansampark.vashisthg.ISSUES;
import com.jansampark.vashisthg.IssueUtils;

public class PieChartView extends GraphicalView {

	private PieChartView(Context context, AbstractChart arg1) {
		super(context, arg1);
	}

	public static GraphicalView getNewInstance(Context context, int[] values) {
		if (values.length != ISSUES.values().length) {
			throw new IllegalArgumentException(
					"values sent are not equal to issues");
		}
		return ChartFactory.getPieChartView(context,
				getDataSet(context, values), getRenderer(context));
	}

	private static DefaultRenderer getRenderer(Context context) {

		int[] colors = getColor(context);

		DefaultRenderer defaultRenderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(color);
			defaultRenderer.addSeriesRenderer(simpleRenderer);
		}
		defaultRenderer.setShowLabels(false);
		defaultRenderer.setShowLegend(false);
		return defaultRenderer;
	}

	private static int[] getColor(Context context) {
		int[] colors = new int[] {
				IssueUtils.getColorInt(context, ISSUES.ROAD),
				IssueUtils.getColorInt(context, ISSUES.WATER),
				IssueUtils.getColorInt(context, ISSUES.TRANSPORT),
				IssueUtils.getColorInt(context, ISSUES.ELECTRICITY),
				IssueUtils.getColorInt(context, ISSUES.LAW),
				IssueUtils.getColorInt(context, ISSUES.SEWAGE) };
		return colors;
	}

	private static CategorySeries getDataSet(Context context, int[] values) {
		CategorySeries series = new CategorySeries("Chart");

		for (int value : values) {
			series.add(value);
		}
		return series;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}