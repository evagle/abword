package com.souldak.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.souldak.abw.ActivityInterface;
import com.souldak.abw.R;
import com.souldak.chart.ProcessChart;
import com.souldak.chart.ReviewPlanChart;
import com.souldak.model.Unit;

public class ChartDialog extends Dialog implements ActivityInterface {
	private Context context;
	private Button showReviewPlanBtn;
	private Button showProcessBtn;
	// private Button closeDialog;
	private LinearLayout chartContainer;
	private LinearLayout mainLayout;
	private int screenWidth;
	private int screenHeight;
	private Unit unit;

	public ChartDialog(Context context, int theme,Unit unit) {
		super(context, theme);
		this.context = context;
		this.unit = unit;
		setContentView(R.layout.chart_dialog);

		// this.setCanceledOnTouchOutside(false);
		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		screenHeight = context.getResources().getDisplayMetrics().heightPixels;

		initCompenents();
		initListeners();

	}
	public void initCompenents() {
		showReviewPlanBtn = (Button) findViewById(R.id.chart_dialog_show_review_plan);
		showProcessBtn = (Button) findViewById(R.id.chart_dialog_show_process);
		// closeDialog = (Button) findViewById(R.id.chart_dialog_close);
		chartContainer = (LinearLayout) findViewById(R.id.chart_dialog_chart);
		mainLayout = (LinearLayout) findViewById(R.id.chart_dialog_main);

		LayoutParams params = new LayoutParams(
				(screenWidth - dpToPixel(30)) / 2, LayoutParams.WRAP_CONTENT);
		showReviewPlanBtn.setLayoutParams(params);
		showProcessBtn.setLayoutParams(params);
	}

	public void initListeners() {

		showReviewPlanBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				adjustLayout();
				ReviewPlanChart chart = new ReviewPlanChart();
				chart.setData(unit);
				View view = chart.execute(context);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(params);
				chartContainer.addView(view);
			}
		});
		showProcessBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				adjustLayout();
				ProcessChart chart = new ProcessChart();
				chart.setData(unit);
				View view = chart.execute(context);
				LayoutParams params = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				view.setLayoutParams(params);
				chartContainer.addView(view);
			}
		});
		// closeDialog.setOnClickListener(new View.OnClickListener() {
		//
		// public void onClick(View v) {
		// ChartDialog.this.dismiss();
		// }
		// });
	}
	private void adjustLayout(){
		if (((Activity) context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			params.setMargins(0, dpToPixel(32), 0, dpToPixel(32));
			mainLayout.setLayoutParams(params);
		} else {
			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			params.setMargins(0, dpToPixel(8), 0, dpToPixel(8));
			mainLayout.setLayoutParams(params);
		}
	}
	private int dpToPixel(int dp) {
		return (int) (0.5f + TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
						.getDisplayMetrics()));
	}
}
