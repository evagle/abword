package com.souldak.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.souldak.abw.LoadDictsActivity;
import com.souldak.abw.R;
import com.souldak.model.MemoRecord;
import com.souldak.model.WordItem;

public class ABActionBar {
	public static void getABActionBar(final Context context,Menu menu){
		((Activity) context).getMenuInflater().inflate(R.menu.activity_main, menu);
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
						Intent intent = new Intent(context,
								LoadDictsActivity.class);
						context.startActivity(intent);
						return false;
					}
				});
	}
}
