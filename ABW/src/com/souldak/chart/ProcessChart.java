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
import org.achartengine.renderer.DefaultRenderer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.souldak.abw.R;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;

public class ProcessChart extends AbstractDemoChart {
	private Unit unit;
	private int nonMemoCount;
	private int memorizingCount;
	private int memoedCount;

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Process chart";
	}

	public void setData(Object unit) {
		this.unit = (Unit) unit;
		initValues();
	}

	private void initValues() {
		for (WordItem w : unit.getWords()) {
			if (w.getIngnore() == 1 || w.getMemoEffect() >= 1) {
				memoedCount++;
			} else if (w.getMemoList().size() == 0) {
				nonMemoCount++;
			} else {
				memorizingCount++;
			}
		}
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The process of current unit (pie chart)";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public View execute(Context context) {
		List<Double> values = new ArrayList<Double>();
		values.add((double)nonMemoCount);
		values.add((double)memorizingCount);
		values.add((double)memoedCount);
		List<Integer> colors = new ArrayList<Integer>();
		colors.add(context.getResources().getColor(R.color.android_blue));
		colors.add(context.getResources().getColor(R.color.android_yellow));
		colors.add(context.getResources().getColor(R.color.android_green));
		for(int i=values.size()-1;i>=0;i--){
			if(values.get(i)==0){
				colors.remove(i);
			}
		}
		List<String> categories = new ArrayList<String>();
		categories.add(" New  ");
		categories.add(" Memorizing  ");
		categories.add(" Finished  ");
		
		DefaultRenderer renderer = buildCategoryRenderer(colors);
		renderer.setChartTitle("Unit "+unit.getUnitId()+" 背诵进度");
		
		renderer.setZoomEnabled(true);
		renderer.setLabelsColor( context.getResources().getColor(R.color.android_dark_grey));
		renderer.setChartTitleTextSize(40);
		renderer.setLabelsTextSize(20);
		renderer.setLegendTextSize(30);
		renderer.setZoomEnabled(true);
		renderer.setAxesColor(Color.BLUE);

		renderer.setBackgroundColor(context.getResources().getColor(
				R.color.bg_yellow));
		//
		renderer.setApplyBackgroundColor(true);
		renderer.setShowGrid(true);

		renderer.setZoomRate(1.1f);

		renderer.setZoomButtonsVisible(false);
		return ChartFactory.getPieChartView(context,
				buildCategoryDataset("Unit "+unit.getUnitId(),categories, values), renderer);

	}

}
