/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.souldak.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;

import com.souldak.abw.R;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;

/**
 * Sales demo bar chart.
 */
public class ReviewPlanChart extends AbstractDemoChart {
	private Unit unit;
	private List<Double> xValues;
	private List<Double> yValues;
	private int maxX;
	private int maxY;
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Sales stacked bar chart";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The review schedule (stacked bar chart)";
	}

	public void setData(Object unit) {
		this.unit = (Unit)unit;
		initValues();
	}
	
	public void initValues(){
		int max = 0;
		for(WordItem w:unit.getWords()){
			int interval=(int)w.getInterval();
			max = max>interval?max:interval;
		}
		maxX = max*3/2;
		xValues = new ArrayList<Double>();
		yValues = new ArrayList<Double>();
		for(int i=0;i<max;i++){
			xValues.add((double)(i+1));
			yValues.add(0d);
		}
		for(WordItem w:unit.getWords()){
			int interval=(int)w.getInterval();
			if(interval>0)
				yValues.set(interval-1,yValues.get(interval-1)+1);
		}
		for(Double d:yValues){
			maxY = maxY > d.intValue()?maxY:d.intValue();
		}
		maxY = maxY*3/2;
		
	}
	
	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built View
	 */
	public View execute(Context context) {
		String[] titles = new String[] { "Unit "+unit.getUnitId() };
		List<String> titleList = new ArrayList<String>();
		titleList.add("Unit "+unit.getUnitId() );
		
		List<List<Double>> Xs = new ArrayList<List<Double>>();
		Xs.add(xValues);
		List<List<Double>> Ys = new ArrayList<List<Double>>();
		Ys.add(yValues);
		XYMultipleSeriesDataset dataset = buildDataset(titleList, Xs, Ys);
		
		int[] colors = new int[] { context.getResources().getColor(R.color.android_green) };
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		setChartSettings(renderer, "Unit "+unit.getUnitId()+" 复习计划",
				"Days", "Words", 0, maxX, 0, maxY, Color.GRAY,
				 context.getResources().getColor(R.color.android_dark_grey));
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

		GraphicalView chart = ChartFactory.getBarChartView(context,
				dataset, renderer, Type.STACKED);
		chart.setBackgroundColor(context.getResources().getColor(
				R.color.bg_yellow));
		return chart;
		// return ChartFactory.getBarChartIntent(context,
		// buildBarDataset(titles, values), renderer,
		// Type.STACKED);
	}

}
