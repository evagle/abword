package com.souldak.abw;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.souldak.config.Configure;
import com.souldak.config.ConstantValue.STUDY_STATE;
import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.controler.StudyControler;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.tts.TTS;
import com.souldak.util.SharePreferenceHelper;
import com.souldak.util.TimeHelper;
import com.souldak.view.ChartDialog;

public class StudyActivity extends Activity implements ActivityInterface {
	private ActionBar actionBar;
	private LinearLayout contentBlock;
	private LinearLayout phrasesBlock;
	private LinearLayout buttonsBlock;
	private LinearLayout splitLine;
	private ProgressBar progressBar;
	private TextView tvWord;
	private TextView tvPhonogram;
	private TextView tvIgnore;
	private TextView tvPrases;
	private TextView tvSentences;
	private int screenWidth;
	private int screenHeight;
	private int marginPixels;
	private int buttonHeight;
	private int reciteCount = 0;
	private static int RECITE_PERIOD = 5;
	private Typeface dejaVuSans;
	private StudyControler controler;
	private STUDY_TYPE studyType;
	private STUDY_STATE studyState;
	private WordItem current;
	private Date startDate;
	private StudyTheme currentThemeStyle;
	private TTS tts;
	private long lastExitTime = 0;
	public static String STUDY_LAST_DICT = "study_last_dict";
	public static String STUDY_LAST_UNIT = "study_last_unit";
	public static final String SAVED_THEME_STYLE = "saved_theme_style";

	public void onCreate(Bundle savedInstanceState) {
		setTheme(savedInstanceState);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_study);

		String dictName = getIntent().getExtras().getString("dictName");
		int unitId = getIntent().getExtras().getInt("unitId");
		String study_type = getIntent().getExtras().getString("STUDY_TYPE");
		if (study_type.equals(STUDY_TYPE.LEARN_NEW.toString())) {
			studyType = STUDY_TYPE.LEARN_NEW;
		} else {
			studyType = STUDY_TYPE.REVIEW;
		}
		studyState = STUDY_STATE.SHOW_ANSWER;
		tts = new TTS(this);

		if (savedInstanceState != null
				&& savedInstanceState.containsKey("unit")) {
			Log.d("Load from savedInstanceState", "savedInstanceState");
			Gson g = new Gson();
			Unit lastUnit = g.fromJson(savedInstanceState.getString("unit"),
					Unit.class);
			controler = new StudyControler(this, dictName, lastUnit);
			controler.setShowedPosition(savedInstanceState
					.getInt("showdPosition"));
		} else {
			controler = new StudyControler(this, dictName, unitId);
			controler.loadCurrentUnit(true);
		}

		Log.i("StudyActivity", "Unit word num ="
				+ controler.getUnit().getTotalWordCount());
		findViews();
		initCompenents();
		initListeners();
		showNextWord();
		onStateChange();
		initButtons();
		saveLastState();

	}

	private void saveLastState() {
		SharePreferenceHelper.savePreferences(STUDY_LAST_DICT, controler
				.getUnit().getDictName(), this);
		SharePreferenceHelper.savePreferences(STUDY_LAST_UNIT, controler
				.getUnit().getUnitId() + "", this);
		SharePreferenceHelper.savePreferences(SAVED_THEME_STYLE,
				Configure.THEME_STYLE, this);

	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Gson g = new Gson();
		String unitStr = g.toJson(controler.getUnit());
		outState.putString("unit", unitStr);
		outState.putInt("showdPosition", controler.getShowedPosition());
		outState.putString(SAVED_THEME_STYLE, Configure.THEME_STYLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - lastExitTime > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次返回主界面",
						Toast.LENGTH_SHORT).show();
				lastExitTime = System.currentTimeMillis();
			} else {
				onPause();
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		saveLastState();
		controler.saveCurrentUnitToFile();
		controler.close();
		tts.close();
	}

	@Override
	public void onPause() {
		super.onPause();
		controler.saveCurrentUnitToFile();
		saveLastState();
	}

	@SuppressWarnings("unused")
	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// ABActionBar.getABActionBar(StudyActivity.this, menu);
		this.getMenuInflater().inflate(R.menu.menu_study, menu);
		MenuItem setting = menu.findItem(R.id.menu_settings);
		MenuItem stats = menu.findItem(R.id.menu_stats);
		MenuItem edit = menu.findItem(R.id.menu_edit);
		MenuItem showChart = menu.findItem(R.id.menu_show_chart);
		showChart
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						ChartDialog chartDialog = new ChartDialog(
								StudyActivity.this, R.style.chart_dialog,
								controler.getUnit());
						chartDialog.show();
						return true;
					}
				});
		stats.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				ChartDialog chartDialog = new ChartDialog(StudyActivity.this,
						R.style.chart_dialog);
				chartDialog.show();
				return true;
			}
		});
		edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				List<String> phrases = current.paraphrasesList();
				if (current != null && current.getWord() != null) {
					if (current.getParaphrases().size() == 0)
						return false;
					View view = LayoutInflater.from(StudyActivity.this)
							.inflate(R.layout.edit_dialog_layout, null);
					final EditText et = (EditText) view
							.findViewById(R.id.edit_dialog_edittext);

					et.setText(current.paraphrasesToString());
					AlertDialog.Builder builder = new AlertDialog.Builder(
							StudyActivity.this);
					builder.setTitle("EDIT: " + current.getWord());
					builder.setView(view);
					builder.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									String edittedText = et.getText()
											.toString();
									current.updateParaphrases(edittedText);
									tvPrases.setText(current
											.paraphrasesToString());
									arg0.dismiss();
								}
							});
					builder.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
								}
							});
					AlertDialog dialog = builder.create();
					dialog.show();

				}
				return false;
			}
		});

		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
				ActionBar.DISPLAY_HOME_AS_UP);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressLint("InlinedApi")
	public void initThemes() {

		ThemeDay = new StudyTheme();
		ThemeDay.contentBlockBg = R.color.android_light_yellow;
		ThemeDay.buttonStyle = R.drawable.grey_button;
		ThemeDay.ignoreTextColor = R.color.soft_grey;
		ThemeDay.wordTextColor = R.color.android_black;
		ThemeDay.phonogramTextColor = R.color.android_dark_grey;
		ThemeDay.phrasesTextColor = R.color.android_black;
		ThemeDay.horizontalSplitLineColor = android.R.color.holo_blue_light;
		ThemeDay.verticleSplitLineColor = R.color.android_light_white;
		ThemeDay.sentsTextColor = R.color.soft_grey;

		ThemeNight = new StudyTheme();
		ThemeNight.contentBlockBg = R.color.android_light_black;
		ThemeNight.buttonStyle = R.drawable.grey_button;
		ThemeNight.ignoreTextColor = R.color.android_light_white;
		ThemeNight.wordTextColor = R.color.android_light_white;
		ThemeNight.phonogramTextColor = R.color.android_light_white;
		ThemeNight.phrasesTextColor = R.color.android_light_white;
		ThemeNight.horizontalSplitLineColor = R.color.android_light_green;
		ThemeNight.verticleSplitLineColor = R.color.android_dark_grey;

		ThemeNight.sentsTextColor = R.color.soft_grey;

		currentThemeStyle = ThemeDay;
	}

	public void findViews() {
		contentBlock = (LinearLayout) findViewById(R.id.study_contentblock);
		phrasesBlock = (LinearLayout) findViewById(R.id.study_phrases);
		buttonsBlock = (LinearLayout) findViewById(R.id.study_buttons);
		tvWord = (TextView) findViewById(R.id.study_word);
		tvPhonogram = (TextView) findViewById(R.id.study_phonogram);
		tvIgnore = (TextView) findViewById(R.id.study_tv_ignore);
		tvPrases = (TextView) findViewById(R.id.study_edit_phrases);
		splitLine = (LinearLayout) findViewById(R.id.study_split_line);
		progressBar = (ProgressBar) findViewById(R.id.study_progress);
		tvSentences = (TextView) findViewById(R.id.study_sentences);

		// progressBar = (ProgressBar) findViewById(R.id.study_progressbar);
	}

	@SuppressLint("NewApi")
	public void initCompenents() {
		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;
		marginPixels = (int) (0.5f + TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 32, getResources()
						.getDisplayMetrics()));
		buttonHeight = 90;
		dejaVuSans = Typeface.createFromAsset(getAssets(),
				"fonts/DejaVuSans.ttf");


		LayoutParams params = new LayoutParams(screenWidth - marginPixels,
				screenHeight - buttonHeight - marginPixels * 4 + 40);
		params.setMargins(marginPixels / 2, marginPixels / 2, marginPixels / 2,
				marginPixels / 4);
		contentBlock.setLayoutParams(params);

		tvWord.setTypeface(dejaVuSans);
		tvPhonogram.setTypeface(dejaVuSans);

	}

	public void initListeners() {
		contentBlock.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (studyState.equals(STUDY_STATE.LEARNING)) {
					togglePhrasesViews();
					onStateChange();
				} else {
					togglePhrasesViews();
				}

			}
		});
		tvIgnore.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				new android.app.AlertDialog.Builder(StudyActivity.this)
						// Context
						.setTitle("忽略单词" + current.getWord() + ",以后不再出现？")
						.setIcon(android.R.drawable.ic_dialog_alert)

						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@SuppressLint("NewApi")
									public void onClick(DialogInterface arg0,
											int arg1) {
										current.setIngnore(1);
										double timeDelta = TimeHelper
												.getDiffSec(new Date(),
														startDate);
										controler.finishMemoWord(current,
												startDate, timeDelta, "");
										ObjectAnimator
												.ofFloat(contentBlock,
														"rotationY", 0, 180)
												.setDuration(400).start();
										ObjectAnimator
												.ofFloat(contentBlock,
														"rotationY", 180, 360)
												.setDuration(400).start();
										showNextWord();
										onStateChange();
										removePhrasesViews(false);
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).show();
			}
		});
		tvPhonogram.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tts.speak(current.getWord());
			}
		});
		tvWord.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						StudyActivity.this);
				builder.setTitle("Last 3 words");
				builder.setMessage(controler.getLastNWords(3, false));
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
	}

	public void onStateChange() {
		if (studyState.equals(STUDY_STATE.LEARNING)) {
			studyState = STUDY_STATE.SHOW_ANSWER;
			setProgress();
			// changeButtons(STUDY_STATE.SHOW_ANSWER);

		} else if (studyState.equals(STUDY_STATE.SHOW_ANSWER)) {
			studyState = STUDY_STATE.LEARNING;
			setProgress();
			// changeButtons(STUDY_STATE.LEARNING);
		}
	}

	public void changeStyle() {

		startDate = new Date();
		tvWord.setTextColor(getResources().getColor(
				currentThemeStyle.wordTextColor));
		tvPhonogram.setTextColor(getResources().getColor(
				currentThemeStyle.phonogramTextColor));
		tvIgnore.setTextColor(getResources().getColor(
				currentThemeStyle.ignoreTextColor));
		splitLine.setBackgroundColor(getResources().getColor(
				currentThemeStyle.horizontalSplitLineColor));

	}

	@SuppressLint("NewApi")
	public void showNextWord() {
		current = controler.next(studyType);
		reciteCount++;
		changeStyle();
		if (current != null) {
			tvWord.setText(current.getWord());
			tvPhonogram.setText(current.getPhonogram());
			if (current != null && Configure.IS_SHOW_RICITE_TIMES)
				tvIgnore.setText(Html.fromHtml(current.getMemoList().size()
						+ " " + "<u>" + "Ignore" + "</u>"));
			else
				tvIgnore.setText(Html.fromHtml("<u>" + "Ignore" + "</u>"));
		} else {
			if (controler.currentUnitFinished()) {
				new android.app.AlertDialog.Builder(StudyActivity.this)
						// Context
						.setTitle("Congratulations！This unit is over.")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@SuppressLint("NewApi")
									public void onClick(DialogInterface arg0,
											int arg1) {
										StudyActivity.this.onDestroy();
									}
								}).show();
			} else {
				if (controler.getUnit().getMemoedCount() == 0
						&& studyType == STUDY_TYPE.REVIEW) {
					Toast.makeText(this, "没有可复习单词，开始背诵新单词", Toast.LENGTH_SHORT)
							.show();
					studyType = STUDY_TYPE.LEARN_NEW;
					showNextWord();
				} else if (studyType == STUDY_TYPE.LEARN_NEW) {
					Toast.makeText(this, "新单词已经背完,进入复习模式", Toast.LENGTH_SHORT)
							.show();
					studyType = STUDY_TYPE.REVIEW;
					controler.resetMemodList();
					showNextWord();
				} else if (studyType == STUDY_TYPE.REVIEW) {

					new android.app.AlertDialog.Builder(StudyActivity.this)
							// Context
							.setTitle("本轮复习完毕，请选择：")
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setOnCancelListener(new OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									Toast.makeText(StudyActivity.this,
											"开始下一轮复习", Toast.LENGTH_SHORT)
											.show();
									studyType = STUDY_TYPE.REVIEW;
									controler.resetMemodList();
									showNextWord();
								}
							})
							.setPositiveButton("下一轮复习",
									new DialogInterface.OnClickListener() {
										@SuppressLint("NewApi")
										public void onClick(
												DialogInterface arg0, int arg1) {
											studyType = STUDY_TYPE.REVIEW;
											controler.resetMemodList();
											showNextWord();
										}
									})
							.setNegativeButton("学习新单词",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											studyType = STUDY_TYPE.LEARN_NEW;
											showNextWord();
										}
									}).show();

				}
			}

		}
		if (reciteCount % RECITE_PERIOD == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					StudyActivity.this);
			builder.setTitle("REVIEW");
			builder.setMessage(controler.getLastNWords(RECITE_PERIOD, false));
			builder.setPositiveButton("TRANSLATE",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							AlertDialog d = (AlertDialog) dialog;
							d.setMessage(controler.getLastNWords(RECITE_PERIOD,
									true));
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								// 设置mShowing值，欺骗android系统
								field.set(dialog, false);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			builder.setNegativeButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								// 设置mShowing值，欺骗android系统
								field.set(dialog, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
			 
			dialog.setCanceledOnTouchOutside(true);
		}
	}

	public void setProgress() {

		progressBar.setProgress(controler.getProgress(studyType));
		if (studyType == STUDY_TYPE.REVIEW) {
			progressBar.setProgressDrawable(getResources().getDrawable(
					R.drawable.progressbar_yellow));
		} else {
			progressBar.setProgressDrawable(getResources().getDrawable(
					R.drawable.progressbar_blue));
		}
	}

	public void initButtons() {
		setProgress();

		buttonsBlock.removeAllViews();
		buttonsBlock.addView(getButton("GOOD", R.color.android_green,
				(screenWidth - marginPixels) / 3, buttonHeight, 0, 0));
		buttonsBlock.addView(getSpliteBar());
		buttonsBlock.addView(getButton("PASS", R.color.android_green,
				(screenWidth - marginPixels) / 3, buttonHeight, 0, 0));
		buttonsBlock.addView(getSpliteBar());
		buttonsBlock.addView(getButton("BAD", R.color.android_green,
				(screenWidth - marginPixels) / 3, buttonHeight, 0, 0));

	}

	public LinearLayout getSpliteBar() {
		LinearLayout spliter = new LinearLayout(this);
		LayoutParams parms = new LayoutParams(2, buttonHeight - 4);
		parms.setMargins(2, 2, 2, 2);
		spliter.setLayoutParams(parms);
		spliter.setBackgroundColor(getResources().getColor(
				currentThemeStyle.verticleSplitLineColor));
		return spliter;
	}

	public int dpToPixel(int dp) {
		return (int) (0.5f + TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, dp, getResources()
						.getDisplayMetrics()));
	}

	@SuppressLint("NewApi")
	public void togglePhrasesViews() {
		if (tvPrases.getVisibility() == View.GONE) {
			showPhrasesViews();
		} else {
			removePhrasesViews(true);
		}
	}

	@SuppressLint("NewApi")
	public void removePhrasesViews(boolean needsAnimator) {
		if (needsAnimator) {
			ObjectAnimator alpha = ObjectAnimator.ofFloat(phrasesBlock,
					"alpha", 1f, 0f);
			alpha.setDuration(300);
			alpha.start();
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		tvPrases.setVisibility(View.GONE);
		tvSentences.setVisibility(View.GONE);
	}

	@SuppressWarnings("unused")
	@SuppressLint("NewApi")
	public void showPhrasesViews() {
		// phrasesBlock.removeAllViews();
		tvPrases.setText(current.paraphrasesToString());
		tvPrases.setTypeface(dejaVuSans);
		tvPrases.setTextSize(20);
		tvPrases.setTextColor(getResources().getColor(
				currentThemeStyle.phrasesTextColor));
		tvPrases.setVisibility(View.VISIBLE);
		tvPrases.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				togglePhrasesViews();
			}
		});
		if (current.sentencesString(false) != null && Configure.SHOW_SENTENCES) {
			tvSentences.setText(current.sentencesString(true));
			tvSentences.setTypeface(dejaVuSans);
			tvSentences.setTextSize(18);
			tvSentences.setTextColor(getResources().getColor(
					currentThemeStyle.sentsTextColor));
			tvSentences.setVisibility(View.VISIBLE);
			tvSentences.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					togglePhrasesViews();
				}
			});
		}
		ObjectAnimator alpha = ObjectAnimator.ofFloat(phrasesBlock, "alpha",
				0f, 1f);
		alpha.setDuration(300);
		alpha.start();
	}

	@SuppressLint("NewApi")
	public Button getButton(String text, int color, int width, int height,
			int marginLeft, int marginRight) {
		Button button = new Button(StudyActivity.this);
		Drawable background = (Drawable) getResources().getDrawable(
				currentThemeStyle.buttonStyle);
		background.setAlpha(225);
		button.setText(text);
		button.setBackground(background);
		// button.setBackgroundColor(Color.TRANSPARENT);
		LayoutParams lp = new LayoutParams(width, height);
		lp.setMargins(marginLeft, 0, marginRight, 0);
		button.setLayoutParams(lp);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Button button = (Button) v;
				double timeDelta = TimeHelper.getDiffSec(new Date(), startDate);
				controler.finishMemoWord(current, startDate, timeDelta, button
						.getText().toString());
				ObjectAnimator.ofFloat(contentBlock, "rotationY", 0, 180)
						.setDuration(400).start();
				ObjectAnimator.ofFloat(contentBlock, "rotationY", 180, 360)
						.setDuration(400).start();

				showNextWord();
				onStateChange();
				removePhrasesViews(false);
			}
		});
		return button;
	}

	@SuppressLint("InlinedApi")
	private void setTheme(Bundle savedInstanceState) {
		initThemes();

		String lastTheme = (String) SharePreferenceHelper.getPreferences(
				SAVED_THEME_STYLE, StudyActivity.this);
		if (lastTheme != null && savedInstanceState != null) {
			Configure.THEME_STYLE = lastTheme;
		}
		if (Configure.THEME_STYLE.equals(Configure.THEME_STYLE_NIGHT)) {
			currentThemeStyle = ThemeNight;
			this.setTheme(android.R.style.Theme_Holo);
		} else {
			currentThemeStyle = ThemeDay;
			this.setTheme(R.style.Theme_ABW);
		}
	}

//	private static StudyTheme ThemeYellow;
//	private static StudyTheme ThemeGrey;
//	private static StudyTheme ThemeGreen;
	private static StudyTheme ThemeDay;
	private static StudyTheme ThemeNight;

	class StudyTheme {
		public int contentBlockBg;
		public int ignoreTextColor;
		public int wordTextColor;
		public int phonogramTextColor;
		public int phrasesTextColor;
		public int sentsTextColor;
		public int buttonColor;
		public int buttonStyle;
		public int horizontalSplitLineColor;
		public int verticleSplitLineColor;
	}
}
