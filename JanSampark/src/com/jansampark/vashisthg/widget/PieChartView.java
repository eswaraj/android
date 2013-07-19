package com.jansampark.vashisthg.widget;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.jansampark.vashisthg.helpers.IssueUtils;
import com.jansampark.vashisthg.models.ISSUE_CATEGORY;

public class PieChartView extends GraphicalView {

	private PieChartView(Context context, AbstractChart arg1) {
		super(context, arg1);
	}

	public static GraphicalView getNewInstance(Context context, int[] values) {
		if (values.length != ISSUE_CATEGORY.values().length) {
			throw new IllegalArgumentException(
					"values sent are not equal to issues");
		}
		GraphicalView pieChartView = ChartFactory.getPieChartView(context,
				getDataSet(context, values), getRenderer(context));
		
		pieChartView.zoomIn();
		return pieChartView;
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
		Log.d(VIEW_LOG_TAG, "scale: " + defaultRenderer.getScale() + "");
		Log.d(VIEW_LOG_TAG, "original_scale: " + defaultRenderer.getOriginalScale() +"");
		defaultRenderer.setScale(1.33f);
		defaultRenderer.setSelectableBuffer(0);
		return defaultRenderer;
	}

	private static int[] getColor(Context context) {
		int[] colors = new int[] {
				IssueUtils.getColorInt(context, ISSUE_CATEGORY.ROAD),
				IssueUtils.getColorInt(context, ISSUE_CATEGORY.WATER),
				IssueUtils.getColorInt(context, ISSUE_CATEGORY.TRANSPORT),
				IssueUtils.getColorInt(context, ISSUE_CATEGORY.ELECTRICITY),
				IssueUtils.getColorInt(context, ISSUE_CATEGORY.LAW),
				IssueUtils.getColorInt(context, ISSUE_CATEGORY.SEWAGE) };
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