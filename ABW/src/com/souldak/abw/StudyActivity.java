package com.souldak.abw;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.souldak.config.Configure;
import com.souldak.config.ConstantValue.STUDY_STATE;
import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.controler.StudyControler;
import com.souldak.db.UnitDBHelper;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.ABFileHelper;
import com.souldak.util.TimeHelper;
import com.souldak.view.ABActionBar;
import com.souldak.view.ChartDialog;

public class StudyActivity extends Activity implements ActivityInterface {
	private ActionBar actionBar;
	private LinearLayout contentBlock;
	private LinearLayout phrasesBlock;
	private LinearLayout buttonsBlock;
	private TextView tvWord;
	private TextView tvPhonogram;
	private TextView tvIgnore;
	private int screenWidth;
	private int screenHeight;
	private int marginPixels;
	private int buttonHeight;
	// private ProgressBar progressBar;
	private Typeface dejaVuSans;
	private Unit unit;
	private WordDBHelper wordDBHelper;
	private UnitDBHelper unitDBHelper;
	private StudyControler controler;
	private STUDY_TYPE studyType;
	private STUDY_STATE studyState;
	private WordItem current;
	private Date startDate;
	private HashMap<String, Integer> effectMap = new HashMap<String, Integer>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_study);
		String dictName = getIntent().getExtras().getString("dictName");
		int unitId = getIntent().getExtras().getInt("unitId");

		studyType = STUDY_TYPE.LEARN_NEW;
		studyState = STUDY_STATE.SHOW_ANSWER;

		unitDBHelper = new UnitDBHelper(dictName);
		wordDBHelper = new WordDBHelper(dictName);
		unit = unitDBHelper.getUnit(unitId, dictName);
		unit.setWords(wordDBHelper.getTotalUnitWords(unitId));
		unit.initWordsList();

		controler = new StudyControler(unit, wordDBHelper, unitDBHelper);
		effectMap.put("GOOD", 5);
		effectMap.put("PASS", 3);
		effectMap.put("BAD", 0);
		Log.w("StudyActivity", "Unit word num =" + unit.getWords().size());
		findViews();
		initCompenents();
		initListeners();
		onStateChange();

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//ABActionBar.getABActionBar(StudyActivity.this, menu);
		this.getMenuInflater().inflate(R.menu.menu_study, menu);
		MenuItem setting = menu.findItem(R.id.menu_settings);
		MenuItem showChart = menu.findItem(R.id.menu_show_chart);
		showChart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				ChartDialog chartDialog=new ChartDialog(StudyActivity.this, R.style.chart_dialog,unit);
				chartDialog.show();
				return true;
			}
		});
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in Action Bar clicked; go home
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void findViews() {
		contentBlock = (LinearLayout) findViewById(R.id.study_contentblock);
		phrasesBlock = (LinearLayout) findViewById(R.id.study_phrases);
		buttonsBlock = (LinearLayout) findViewById(R.id.study_buttons);
		tvWord = (TextView) findViewById(R.id.study_word);
		tvPhonogram = (TextView) findViewById(R.id.study_phonogram);
		tvIgnore = (TextView) findViewById(R.id.study_tv_ignore);
		// progressBar = (ProgressBar) findViewById(R.id.study_progressbar);

	}

	public void initCompenents() {
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		marginPixels = (int) (0.5f + TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 64, getResources()
						.getDisplayMetrics()));
		buttonHeight = 75;
		dejaVuSans = Typeface.createFromAsset(getAssets(),
				"fonts/DejaVuSans.ttf");

		GradientDrawable background = (GradientDrawable) getResources()
				.getDrawable(R.drawable.rounded_rect);
		background.setColor(getResources().getColor(
				R.color.android_light_yellow));
		background.setAlpha(225);
		background.setCornerRadius(4);
		contentBlock.setBackgroundDrawable(background);
		LayoutParams params = new LayoutParams(screenWidth - marginPixels,
				screenHeight - buttonHeight * 3 / 2 - marginPixels * 2);
		params.setMargins(marginPixels / 2, marginPixels / 2, marginPixels / 2,
				marginPixels / 4);
		contentBlock.setLayoutParams(params);

		// changeButtons(STUDY_STATE.SHOW_ANSWER);

		tvWord.setTypeface(dejaVuSans);

		BufferedReader br = ABFileHelper.open(Configure.APP_SD_ROOT_PATH
				+ "gre_phonogram1.txt");
		String[] xx = null;
		try {
			xx = br.readLine().trim().split("[ \t]");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tvPhonogram.setText(xx[1]);
		tvPhonogram.setTypeface(dejaVuSans);
		showNextWord();
		
		tvIgnore.setText(Html.fromHtml("<u>"+"Ignore"+"</u>"));

	}
	public void initListeners() {
		contentBlock.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (studyState.equals(STUDY_STATE.LEARNING)) {
//					ObjectAnimator.ofFloat(v, "rotationY", 0, 180)
//							.setDuration(400).start();
//					ObjectAnimator.ofFloat(v, "rotationY", 180, 360)
//							.setDuration(400).start();
					togglePhrasesViews();
					onStateChange();
				} else {
					// ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha",
					// 1f, 0f);
					// alpha.setRepeatMode(ObjectAnimator.REVERSE);
					// alpha.setRepeatCount(1);
					// alpha.setDuration(500);
					// alpha.start();
					togglePhrasesViews();
				}

			}
		});
		tvIgnore.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				new android.app.AlertDialog.Builder(StudyActivity.this)//Context
				.setTitle("忽略单词"+current.getWord()+",以后不再出现？")
				.setIcon(android.R.drawable.ic_dialog_alert) 
				
				.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
					@SuppressLint("NewApi")
					public void onClick(DialogInterface arg0, int arg1) {
						current.setIngnore(1);
						double timeDelta = TimeHelper.getDiffSec(new Date(), startDate);
						controler.finishMemoWord(current, startDate, timeDelta,5);
						ObjectAnimator.ofFloat(contentBlock, "rotationY", 0, 180).setDuration(400)
								.start();
						ObjectAnimator.ofFloat(contentBlock, "rotationY", 180, 360)
								.setDuration(400).start();
						showNextWord();
						onStateChange();
						removePhrasesViews();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show(); 
			}
		});
		
	}
	public void onStateChange() {
		if (studyState.equals(STUDY_STATE.LEARNING)) {
			studyState = STUDY_STATE.SHOW_ANSWER;
			changeButtons(STUDY_STATE.SHOW_ANSWER);

		} else if (studyState.equals(STUDY_STATE.SHOW_ANSWER)) {
			studyState = STUDY_STATE.LEARNING;
			changeButtons(STUDY_STATE.LEARNING);
		}
	}

	public void showNextWord() {
		current = controler.next(studyType);
		startDate = new Date();
		if (current != null) {
			tvWord.setText(current.getWord());
			tvPhonogram.setText(current.getPhonogram());
		} else {
			// ///////
			// ADD dialog
			// //////
		}
	}

	public void changeButtons(STUDY_STATE state) {
		if (state.equals(STUDY_STATE.SHOW_ANSWER)) {
			// progressBar.setVisibility(View.GONE);
			buttonsBlock.removeAllViews();
			buttonsBlock.addView(getButton("GOOD", R.color.android_green,
					(screenWidth - marginPixels - 32) / 3, buttonHeight, 0, 8));
			buttonsBlock.addView(getButton("PASS", R.color.android_green,
					(screenWidth - marginPixels - 32) / 3, buttonHeight, 8, 8));
			buttonsBlock.addView(getButton("BAD", R.color.android_green,
					(screenWidth - marginPixels - 32) / 3, buttonHeight, 8, 0));
		} else if (state.equals(STUDY_STATE.LEARNING)) {
			// buttonsBlock.addView(getButton("SHOW ANSWER",R.color.android_green,(screenWidth-marginPixels),buttonHeight,0,0));
			buttonsBlock.removeAllViews();
			ProgressBar bar = new ProgressBar(this, null,
					android.R.attr.progressBarStyleHorizontal);
			LayoutParams parms = new LayoutParams(LayoutParams.MATCH_PARENT,
					dpToPixel(3));
			bar.setLayoutParams(parms);
			bar.setProgress(unit.getMemoedCount() * 100
					/ unit.getTotalWordCount());
			bar.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					ChartDialog chartDialog=new ChartDialog(StudyActivity.this, R.style.chart_dialog,unit);
					chartDialog.show();
				}
			});
			buttonsBlock.addView(bar);

		}
	}

	public int dpToPixel(int dp) {
		return (int) (0.5f + TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
						.getDisplayMetrics()));
	}

	

	@SuppressLint("NewApi")
	public void togglePhrasesViews() {
		if (phrasesBlock.getChildCount() == 0) {
			showPhrasesViews();
		} else {
			removePhrasesViews();
		}
	}

	@SuppressLint("NewApi")
	public void removePhrasesViews() {
		ObjectAnimator alpha = ObjectAnimator.ofFloat(phrasesBlock, "alpha",
				1f, 0f);
		alpha.setDuration(500);
		alpha.start();
		// ObjectAnimator.ofFloat(contentBlock, "rotationY", 0, 180)
		// .setDuration(500).start();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		phrasesBlock.removeAllViews();
		// ObjectAnimator.ofFloat(contentBlock, "rotationY", 180, 360)
		// .setDuration(500).start();

	}

	@SuppressLint("NewApi")
	public void showPhrasesViews() {
		phrasesBlock.removeAllViews();
		for (String phrase : current.getParaphrases()) {
			TextView view = new TextView(StudyActivity.this);
			view.setText(phrase);
			view.setTypeface(dejaVuSans);
			view.setTextSize(20);

			phrasesBlock.addView(view);
		}
		ObjectAnimator alpha = ObjectAnimator.ofFloat(phrasesBlock, "alpha",
				0f, 1f);
		alpha.setDuration(500);
		alpha.start();
		// ObjectAnimator.ofFloat(contentBlock, "rotationY", 0, 180)
		// .setDuration(500).start();
		// ObjectAnimator.ofFloat(contentBlock, "rotationY", 180, 360)
		// .setDuration(500).start();

	}

	@SuppressLint("NewApi")
	public Button getButton(String text, int color, int width, int height,
			int marginLeft, int marginRight) {
		Button button = new Button(StudyActivity.this);
		GradientDrawable background = (GradientDrawable) getResources()
				.getDrawable(R.drawable.button);
		// background.setColor(getResources().getColor(color));
		background.setAlpha(225);
		// background.setGradientRadius(4);
		button.setText(text);
		button.setBackground(background);
		LayoutParams lp = new LayoutParams(width, height);
		lp.setMargins(marginLeft, 0, marginRight, 0);
		button.setLayoutParams(lp);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Button button = (Button) v;
				double timeDelta = TimeHelper.getDiffSec(new Date(), startDate);
				controler.finishMemoWord(current, startDate, timeDelta,
						effectMap.get(button.getText()));
				ObjectAnimator.ofFloat(contentBlock, "rotationY", 0, 180).setDuration(400)
						.start();
				ObjectAnimator.ofFloat(contentBlock, "rotationY", 180, 360)
						.setDuration(400).start();
				showNextWord();
				onStateChange();
				removePhrasesViews();
			}
		});
		return button;
	}

}
