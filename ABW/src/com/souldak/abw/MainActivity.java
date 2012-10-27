package com.souldak.abw;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.souldak.controler.DictManager;
import com.souldak.controler.GlobalData;
import com.souldak.db.WordDBHelper;
import com.souldak.fragment.WordFragment;
import com.souldak.model.Dict;
import com.souldak.model.DictOld;
import com.souldak.model.MemoRecord;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.ABFileHelper;
import com.souldak.view.BoxView;
import com.souldak.view.BoxView.BOX_TYPE;

public class MainActivity extends Activity implements ActivityInterface {
	private ActionBar actionBar;
	private DictManager dictManager;
	private DictOld currentDict;
	private ArrayAdapter actionAdapter;
	private List<String> dictNameList;
	private String learningType;
	private WordFragment wordFragment;
	private Dict selectedDict;
	private WordDBHelper wordDBHelper;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initCompenents();
		initListeners();

	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.activity_main, menu);
		MenuItem setting = menu.findItem(R.id.menu_settings);
		MenuItem loadDicts = menu.findItem(R.id.menu_load_dicts);
		setting.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				MemoRecord m = new MemoRecord();
				m.setStartTime(new Date());
				m.setTimedelta(10.3d);
				m.setGrade(5);
				WordItem word = new WordItem();
				word.setId(123);
				word.setWord("hello");
				word.setMemoEffect(2d);
				List<MemoRecord> l = new ArrayList<MemoRecord>();
				l.add(m);
				word.setMemoList(l);
				Gson gson = new Gson();
				String s = gson.toJson(word);
				WordItem x = gson.fromJson(s, WordItem.class);
				// tv.setText(ABFileHelper.list(Environment.getExternalStorageDirectory().getPath()+"/baidu/ime/skink").toString());
				return false;
			}
		});
		loadDicts
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(MainActivity.this,
								LoadDictsActivity.class);
						startActivity(intent);
						return false;
					}
				});
		return true;
	}

	@SuppressLint("NewApi")
	public void initCompenents() {
		dictManager = new DictManager(this);
		wordFragment = new WordFragment("", "");

		// LinearLayout row=new LinearLayout(this);
		// FragmentTransaction ft = getFragmentManager().beginTransaction();
		// ft.add(row.getId(), wordFragment);
		// ft.add(row.getId(), new WordFragment("", ""));
		// ft.commit();
		// containerlayout.addView(row);
		// row=new LinearLayout(this);
		// ft = getFragmentManager().beginTransaction();
		// ft.add(row.getId(), wordFragment);
		// ft.add(row.getId(), new WordFragment("", ""));
		// ft.commit();

		initActionBar();
	}

	public void initListeners() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("rawtypes")
	@TargetApi(11)
	public void initActionBar() {
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);// You
																	// should
																	// perform
																	// this
																	// during
																	// your
																	// activity's
																	// onCreate()
																	// method.
		actionBar.setDisplayShowTitleEnabled(false);
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() {

			public boolean onNavigationItemSelected(int position, long itemId) {
				selectedDict = new Dict(MainActivity.this,
						dictNameList.get(position));
				int currentUnitPos = -1;
				LinearLayout containerlayout = (LinearLayout) MainActivity.this
						.findViewById(R.id.fragment_container);
				containerlayout.removeAllViews();
				List<Unit> needShowUnitList = new ArrayList<Unit>();
				List<Integer> colorList = new ArrayList<Integer>();
				List<BoxView> boxList = new ArrayList<BoxView>();
				List<BOX_TYPE> boxTypeList = new ArrayList<BoxView.BOX_TYPE>();
				if (selectedDict.getMemoedList().size() > 0) {
					needShowUnitList.add(selectedDict.getMemoedList().get(0));
					colorList.add(getResources()
							.getColor(R.color.android_green));
					boxTypeList.add(BOX_TYPE.BOX_MEMOED);
				}
				if (selectedDict.getCurrentUnit() != null) {
					currentUnitPos = needShowUnitList.size();
					needShowUnitList.add(selectedDict.getCurrentUnit());
					colorList.add(getResources().getColor(
							R.color.android_yellow));
					boxTypeList.add(BOX_TYPE.BOX_CURR);
				}
				needShowUnitList.addAll(selectedDict.getNonMemoList());
				if (needShowUnitList.size() == 0)
					return false;
				for (int i = 0; i < selectedDict.getNonMemoList().size(); i++) {
					colorList
							.add(getResources().getColor(R.color.android_blue));
					boxTypeList.add(BOX_TYPE.BOX_LOCKED);
				}

				LinearLayout row = new LinearLayout(MainActivity.this);
				if (needShowUnitList.size() <= 2) {
					BoxView box = new BoxView(MainActivity.this,
							needShowUnitList.get(0), 32, 32, 16, 16,
							colorList.get(0), boxTypeList.get(0));
					row.addView(box);
					boxList.add(box);
					if (needShowUnitList.size() == 2) {
						box = new BoxView(MainActivity.this,
								needShowUnitList.get(1), 16, 32, 32, 16,
								colorList.get(1), boxTypeList.get(1));
						row.addView(box);
						boxList.add(box);
					}
					containerlayout.addView(row);
				} else {
					// First two
					BoxView box = new BoxView(MainActivity.this,
							needShowUnitList.get(0), 32, 32, 16, 16,
							colorList.get(0), boxTypeList.get(0));
					row.addView(box);
					boxList.add(box);
					box = new BoxView(MainActivity.this,
							needShowUnitList.get(1), 16, 32, 32, 16,
							colorList.get(1), boxTypeList.get(1));
					row.addView(box);
					boxList.add(box);
					containerlayout.addView(row);
					// third,fourth....
					int i = 2;
					for (i = 2; i + 1 < needShowUnitList.size(); i += 2) {
						row = new LinearLayout(MainActivity.this);
						box = new BoxView(MainActivity.this,
								needShowUnitList.get(i), 32, 16, 16, 16,
								getResources().getColor(R.color.android_blue),
								boxTypeList.get(i));
						row.addView(box);
						boxList.add(box);
						box = new BoxView(MainActivity.this,
								needShowUnitList.get(i + 1), 16, 16, 32, 16,
								getResources().getColor(R.color.android_blue),
								boxTypeList.get(i));
						row.addView(box);
						boxList.add(box);
						containerlayout.addView(row);
					}
					// the last single one
					if (i < needShowUnitList.size()) {
						row = new LinearLayout(MainActivity.this);
						box = new BoxView(MainActivity.this,
								needShowUnitList.get(i), 32, 16, 16, 16,
								getResources().getColor(R.color.android_blue),
								boxTypeList.get(i));
						row.addView(box);
						boxList.add(box);
						containerlayout.addView(row);
					}
				}

				wordDBHelper = new WordDBHelper(needShowUnitList.get(0)
						.getDictName());
				for (int i = 0; i < boxList.size(); i++) {
					final BoxView box = boxList.get(i);
					final Unit iunit = needShowUnitList.get(i);
					if (box.getType().equals(BOX_TYPE.BOX_LOCKED)) {
						box.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
//								if (iunit.getWords() == null)
//									iunit.setWords(wordDBHelper
//											.getTotalUnitWords(iunit
//													.getUnitId()));
//								box.setText(iunit.getUnitId() + "  "
//										+ iunit.getTotalWordCount());
								BoxView box= (BoxView)v;
								box.randomWord(wordDBHelper);
								ObjectAnimator.ofFloat(v, "rotationY", 0, 180)
								.setDuration(500).start();
								ObjectAnimator.ofFloat(v, "rotationY", 180, 360)
								.setDuration(500).start();
//								ObjectAnimator alpha = ObjectAnimator.ofFloat(
//										box, "alpha", 1f, 0f);
//								alpha.setRepeatMode(ObjectAnimator.REVERSE);
//								alpha.setRepeatCount(1);
//								alpha.setDuration(800);
//								alpha.start();
							}
						});
					}else if(box.getType().equals(BOX_TYPE.BOX_CURR)){
						box.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
							  
								BoxView box= (BoxView)v;
								box.randomWord(wordDBHelper);
								ObjectAnimator alpha = ObjectAnimator.ofFloat(
										box, "alpha", 1f, 0f);
								alpha.setRepeatMode(ObjectAnimator.REVERSE);
								alpha.setRepeatCount(1);
								alpha.setDuration(800);
								alpha.start();
								Intent intent = new Intent(MainActivity.this,StudyActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("dictName", box.getUnit().getDictName());
								bundle.putInt("unitId", box.getUnit().getUnitId());
								intent.putExtras(bundle);
								startActivity(intent);
//								
							}
						});
					}
				}

				return false;
			}
		};

		dictNameList = dictManager.getDictList();
		actionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, dictNameList);
		actionBar.setListNavigationCallbacks(actionAdapter,
				mOnNavigationListener);

	}

}
