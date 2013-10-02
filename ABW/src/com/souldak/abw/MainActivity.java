package com.souldak.abw;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.souldak.config.Configure;
import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.controler.DictManager;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Dict;
import com.souldak.model.Unit;
import com.souldak.model.Unit.UNIT_STATE;
import com.souldak.util.SharePreferenceHelper;
import com.souldak.view.ABScrollView;
import com.souldak.view.BoxView;
import com.souldak.view.BoxView.BOX_TYPE;
import com.souldak.view.ChartDialog;

public class MainActivity extends Activity implements ActivityInterface {
	private ActionBar actionBar;
	private DictManager dictManager;
	@SuppressWarnings("rawtypes")
	private ArrayAdapter actionAdapter;
	private List<String> dictNameList;
	private Dict selectedDict;
	private WordDBHelper wordDBHelper;
	private ABScrollView scrollView;
	public static String LAST_DICT = "last_dict";
	public static final String SAVED_THEME_STYLE = "saved_theme_style";

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		InstallDB installation = new InstallDB(this);
		installation.install();
		initCompenents();
		initListeners();
		
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem setting = menu.findItem(R.id.menu_settings);
		MenuItem loadDicts = menu.findItem(R.id.menu_load_dicts);
		MenuItem theme = menu.findItem(R.id.menu_theme);
		MenuItem showStats = menu.findItem(R.id.menu_stats);
		setting.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				return false;
			}
		});
		loadDicts
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(MainActivity.this,
								LoadDictsActivity.class);
						startActivity(intent);
						return true;
					}
				});
		showStats
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						ChartDialog chartDialog = new ChartDialog(
								MainActivity.this, R.style.chart_dialog);
						chartDialog.show();
						return true;
					}
				});

		theme.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {

				if (Configure.THEME_STYLE.equals(Configure.THEME_STYLE_DAY)) {
					Configure.THEME_STYLE = Configure.THEME_STYLE_NIGHT;
					SharePreferenceHelper.savePreferences(SAVED_THEME_STYLE, Configure.THEME_STYLE, MainActivity.this);
					item.setTitle("NIGHT");
				} else {
					Configure.THEME_STYLE = Configure.THEME_STYLE_DAY;
					SharePreferenceHelper.savePreferences(SAVED_THEME_STYLE, Configure.THEME_STYLE, MainActivity.this);
					item.setTitle("DAY");
				}

				return true;
			}
		});
		Object lastTheme = SharePreferenceHelper.getPreferences(SAVED_THEME_STYLE, MainActivity.this);
		if(lastTheme!=null)
			Configure.THEME_STYLE = (String)lastTheme;
		if (Configure.THEME_STYLE.equals(Configure.THEME_STYLE_DAY))
			theme.setTitle("DAY");
		else
			theme.setTitle("NIGHT");
		return true;
	}

	@SuppressLint("NewApi")
	public void initCompenents() {
		scrollView = (ABScrollView) findViewById(R.id.scroll_container);

		dictManager = new DictManager();

		initActionBar();
	}

	public void initListeners() {
		scrollView.getScrollY();
	}

	@TargetApi(11)
	public void initActionBar() {
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		actionBar.setDisplayShowTitleEnabled(false);
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

			public boolean onNavigationItemSelected(int position, long itemId) {
				SharePreferenceHelper.savePreferences(LAST_DICT,
						dictNameList.get(position), MainActivity.this);
				selectedDict = new Dict(MainActivity.this,
						dictNameList.get(position));
				if (selectedDict == null
						|| selectedDict.getCurrentUnit() == null) {
					return false;
				}
				SharePreferenceHelper.savePreferences(
						dictNameList.get(position), selectedDict
								.getCurrentUnit().getUnitId() + "",
						MainActivity.this);

				LinearLayout containerlayout = (LinearLayout) MainActivity.this
						.findViewById(R.id.fragment_container);
				containerlayout.removeAllViews();

				List<BoxView> boxList = new ArrayList<BoxView>();
				List<Unit> needShowUnitList = selectedDict.getUnitList();
				for (int i = 0; i < needShowUnitList.size(); i++) {
					int color;
					BOX_TYPE boxtype;
					if (needShowUnitList.get(i).getUnitState() == UNIT_STATE.NOT_START) {
						color = getResources().getColor(R.color.android_blue);
						boxtype = BOX_TYPE.BOX_NOT_START;
					} else if (needShowUnitList.get(i).getUnitState() == UNIT_STATE.LEARNING
							|| needShowUnitList.get(i).getUnitState() == UNIT_STATE.LEARNED_ONE_TIME) {
						color = getResources().getColor(R.color.android_yellow);
						boxtype = BOX_TYPE.BOX_LEARNING;
					} else {
						color = getResources().getColor(R.color.android_green);
						boxtype = BOX_TYPE.BOX_FINISHED;
					}
					BoxView box = generateBox(needShowUnitList.get(i), color,
							boxtype, i + 1);
					boxList.add(box);

				}
				for (int i = 0; i < boxList.size(); i += 2) {
					LinearLayout row = new LinearLayout(MainActivity.this);
					row.addView(boxList.get(i));
					if (i + 1 < boxList.size())
						row.addView(boxList.get(i + 1));
					containerlayout.addView(row);
				}

				wordDBHelper = new WordDBHelper(needShowUnitList.get(0)
						.getDictName());
				for (int i = 0; i < boxList.size(); i++) {
					final BoxView box = boxList.get(i);
					if (box.getType().equals(BOX_TYPE.BOX_LOCKED)) {
						box.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								BoxView box = (BoxView) v;
								box.randomWord(wordDBHelper);
								ObjectAnimator.ofFloat(v, "rotationY", 0, 180)
										.setDuration(500).start();
								ObjectAnimator
										.ofFloat(v, "rotationY", 180, 360)
										.setDuration(500).start();

							}
						});
					} else {
						box.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								final BoxView box = (BoxView) v;
								box.randomWord(wordDBHelper);

								new android.app.AlertDialog.Builder(
										MainActivity.this)
										// Context
										.setTitle("模式选择")
										.setIcon(
												android.R.drawable.ic_dialog_alert)
										.setPositiveButton(
												"新单词背诵",
												new DialogInterface.OnClickListener() {
													@SuppressLint("NewApi")
													public void onClick(
															DialogInterface arg0,
															int arg1) {
														startStudyActivity(
																STUDY_TYPE.LEARN_NEW,
																(BoxView) box);
													}
												})
										.setNegativeButton(
												"复习旧单词",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														startStudyActivity(
																STUDY_TYPE.REVIEW,
																(BoxView) box);
													}
												}).show();

							}
						});
					}
				}

				return false;
			}
		};

		dictNameList = dictManager.getDictList();
		Object lastDaict = SharePreferenceHelper
				.getPreferences(LAST_DICT, this);
		if (lastDaict != null && dictNameList.contains((String) lastDaict)) {
			int pos = dictNameList.indexOf((String) lastDaict);
			dictNameList.remove(pos);
			dictNameList.add(0, (String) lastDaict);
		}
		actionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, dictNameList);
		actionBar.setListNavigationCallbacks(actionAdapter,
				mOnNavigationListener);

	}

	private void startStudyActivity(STUDY_TYPE studyType, BoxView box) {
		Intent intent = new Intent(MainActivity.this, StudyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("dictName", box.getUnit().getDictName());
		bundle.putInt("unitId", box.getUnit().getUnitId());
		bundle.putString("STUDY_TYPE", studyType.toString());
		intent.putExtras(bundle);
		startActivity(intent);

	}

	private BoxView generateBox(Unit iunit, int color, BOX_TYPE type, int num) {
		int margin = 48;
		int marginHalf = 24;
		if (num == 1) {
			return new BoxView(MainActivity.this, iunit, margin, margin,
					marginHalf, marginHalf, color, type);
		} else if (num == 2) {
			return new BoxView(MainActivity.this, iunit, marginHalf, margin,
					margin, marginHalf, color, type);
		} else if (num % 2 == 1) {
			return new BoxView(MainActivity.this, iunit, margin, marginHalf,
					marginHalf, marginHalf, color, type);
		} else {
			return new BoxView(MainActivity.this, iunit, marginHalf,
					marginHalf, margin, marginHalf, color, type);
		}
	}

}
