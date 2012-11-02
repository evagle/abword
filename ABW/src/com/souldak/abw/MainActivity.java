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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.controler.DictManager;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Dict;
import com.souldak.model.Unit;
import com.souldak.util.SharePreferenceHelper;
import com.souldak.util.TimeHelper;
import com.souldak.view.ABScrollView;
import com.souldak.view.BoxView;
import com.souldak.view.BoxView.BOX_TYPE;

public class MainActivity extends Activity implements ActivityInterface{
	private ActionBar actionBar;
	private DictManager dictManager;
	private Dict currentDict;
	private ArrayAdapter actionAdapter;
	private List<String> dictNameList;
	private Dict selectedDict;
	private WordDBHelper wordDBHelper;
	private ABScrollView scrollView;

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
		scrollView = (ABScrollView) findViewById(R.id.scroll_container);
		
		dictManager = new DictManager(this);
		
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
				String unitIdStr = ((String)SharePreferenceHelper.getPreferences(dictNameList.get(position),
						MainActivity.this));
				//if(unitIdStr == null){
					selectedDict = new Dict(MainActivity.this,
							dictNameList.get(position));
					SharePreferenceHelper.savePreferences(dictNameList.get(position), 
							selectedDict.getCurrentUnit().getUnitId()+"", MainActivity.this);
				//}else{
				//	int unitid = Integer.parseInt(unitIdStr);
				//	
				//}
				
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

				for (int i = 0; i < needShowUnitList.size(); i += 2) {
					row = new LinearLayout(MainActivity.this);
					for(int col = i;col<=i+1&&col<needShowUnitList.size();col++){
						BoxView box = generateBox(needShowUnitList.get(col),
								colorList.get(col), boxTypeList.get(col), col + 1);
						row.addView(box);
						boxList.add(box);
					}
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
					} else if (box.getType().equals(BOX_TYPE.BOX_CURR)) {
						box.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								final BoxView box = (BoxView)v;
								box.randomWord(wordDBHelper);
//								ObjectAnimator alpha = ObjectAnimator.ofFloat(
//										box, "alpha", 1f, 0f);
//								alpha.setRepeatMode(ObjectAnimator.REVERSE);
//								alpha.setRepeatCount(1);
//								alpha.setDuration(800);
//								alpha.start();
								
								new android.app.AlertDialog.Builder(MainActivity.this)//Context
								.setTitle("模式选择")
								.setIcon(android.R.drawable.ic_dialog_alert) 
								.setPositiveButton("新单词背诵", new DialogInterface.OnClickListener() { 
									@SuppressLint("NewApi")
									public void onClick(DialogInterface arg0, int arg1) {
										startStudyActivity(STUDY_TYPE.LEARN_NEW,(BoxView)box);
									}
								})
								.setNegativeButton("复习旧单词", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										startStudyActivity(STUDY_TYPE.REVIEW,(BoxView)box);
									}
								})
								.show(); 
								
								
								 
								
								
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
	private void startStudyActivity(STUDY_TYPE studyType, BoxView box){
		Intent intent = new Intent(MainActivity.this,
				StudyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("dictName", box.getUnit()
				.getDictName());
		bundle.putInt("unitId", box.getUnit()
				.getUnitId());
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
