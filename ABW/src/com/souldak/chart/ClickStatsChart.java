package com.souldak.chart;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.souldak.abw.R;
import com.souldak.db.ClickStatsDBHelper;
import com.souldak.model.ClickStatsItem;
import com.souldak.util.TimeHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

public class ClickStatsChart extends AbstractDemoChart {
	public static int MAX_SHOW_DAY_NUM = 30;
	public String getName() {
		return "Click Stats Chart";
	}

	public String getDesc() {
		return "Click stats";
	}

	public View execute(Context context) {
		List<String> titleList = new ArrayList<String>();
		titleList.add("Total");
		titleList.add("Good");
		titleList.add("Pass");
		titleList.add("Bad");
				 
		List<List<Date>> x = new ArrayList<List<Date>>();
		ClickStatsDBHelper helper = new ClickStatsDBHelper();
		List<ClickStatsItem> list = helper.queryRange(
				TimeHelper.subDate(new Date(),MAX_SHOW_DAY_NUM), new Date());
		
		Date today = TimeHelper.floorDate(new Date());

		if (list.size() == 0) {
			list.add(new ClickStatsItem(new Date()));
		}
		for (int i = 0; i < titleList.size(); i++) {
			List<Date> dates = new ArrayList<Date>();
			for (int j = list.size() - 1; j >= 0; j--) {
				dates.add(TimeHelper.subDate(today, j));
			}
			x.add(dates);
		}
		List<Double> total = new ArrayList<Double>();
		List<Double> good = new ArrayList<Double>();
		List<Double> pass = new ArrayList<Double>();
		List<Double> bad = new ArrayList<Double>();
		int maxY=0;
		for (int i = 0; i < list.size(); i++) {
			total.add((double) list.get(i).getTotal());
			if(list.get(i).getTotal() > maxY)
				maxY = list.get(i).getTotal();
			good.add((double) list.get(i).getGood());
			pass.add((double) list.get(i).getPass());
			bad.add((double) list.get(i).getBad());
		}
		if(maxY < 4)
			maxY = 4;
		else
			maxY = maxY*5/4;
		List<List<Double>> values = new ArrayList<List<Double>>();
		values.add(total);
		values.add(good);
		values.add(pass);
		values.add(bad);
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN,
				Color.RED };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}
		long oneday = new Date().getTime() - TimeHelper.subDate(new Date()).getTime();
		
		setChartSettings(renderer, "点击统计", "Days", "Times", x.get(0).get(0).getTime(),
				x.get(0).get(list.size()-1).getTime()+oneday, 0, 
				maxY,
				Color.GRAY,
				context.getResources().getColor(R.color.android_dark_grey));
//		setChartSettings(renderer, "Average temperature", "Month",
//				"Temperature", 0.5, 12.5, -10, 40, Color.LTGRAY, Color.LTGRAY);

		// renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		// renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

		renderer.setChartTitleTextSize(40);

		renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setLabelsTextSize(20);
		renderer.setChartValuesTextSize(20);
		renderer.setAxisTitleTextSize(30);
		renderer.setLegendTextSize(30);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPanEnabled(true, false);
		renderer.setZoomEnabled(true);
		renderer.setAxesColor(Color.BLUE);
		// 设置4边留白透明
		//
		// renderer.setMargins(new int[] {0, 50, 0,100});
		renderer.setMarginsColor(Color.argb(100, 0xff, 0xff, 0xff));

		renderer.setBackgroundColor(Color.TRANSPARENT);
		//
		renderer.setApplyBackgroundColor(true);
		renderer.setShowGrid(true);

		renderer.setZoomRate(1.1f);
		renderer.setBarSpacing(0.5f);

		renderer.setZoomButtonsVisible(false);
		GraphicalView chart = ChartFactory.getTimeChartView(context,
				buildDateDataset(titleList, x, values), renderer, "MMM-dd");
//		GraphicalView chart = ChartFactory.getLineChartView (context,
//				buildDateDataset(titleList, x, values), renderer );
		chart.setBackgroundColor(context.getResources().getColor(
				R.color.bg_yellow));
		return chart;
	}

}
