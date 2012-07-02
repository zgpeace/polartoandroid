package de.unihro.es.pta;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class Chart {

	XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
	XYSeries series = new XYSeries("heartrate");
	XYMultipleSeriesDataset categorySeries = new XYMultipleSeriesDataset();

	public Intent execute(Context context, Chart chart) {

		chart.categorySeries.addSeries(chart.series);

		chart.renderer.setAxisTitleTextSize(16);
		chart.renderer.setChartTitleTextSize(20);
		chart.renderer.setLabelsTextSize(15);
		chart.renderer.setLegendTextSize(15);
		chart.renderer.setMargins(new int[] {20, 30, 15, 0});
		chart.renderer.setAxesColor(Color.YELLOW);

		chart.xyRenderer.setColor(Color.RED);
		chart.renderer.addSeriesRenderer(chart.xyRenderer);


		return ChartFactory.getLineChartIntent(context, chart.categorySeries, chart.renderer);
	}

	void addValues (int x, int y){
		series.add(x,y);

	}
}


