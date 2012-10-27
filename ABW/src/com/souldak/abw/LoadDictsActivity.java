package com.souldak.abw;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.souldak.adapter.LoadDictAdapter;
import com.souldak.config.Configure;
import com.souldak.controler.DictManager;
import com.souldak.controler.DictToDB;
import com.souldak.model.LoadDictAdapterModel;
import com.souldak.util.ABFileHelper;

public class LoadDictsActivity extends Activity implements ActivityInterface{
	private ListView dictListView;
	private DictManager dictManager;
	private DictToDB dictToDB;
	private LoadDictAdapter adapter;
	private ActionBar actionBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_dict);
		dictManager = new DictManager(this);
		dictToDB = new DictToDB(this);
		initCompenents();
		initListeners();
	}
	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu) {
		actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP); 
        getMenuInflater().inflate(R.menu.activity_main, menu);
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
	public void initCompenents() {
		dictListView = (ListView) this.findViewById(R.id.load_dict_list_dicts);
	}
	public void initListeners() {
		adapter=new LoadDictAdapter(this,R.layout.adapter_load_dict_row,getDictList());
		dictListView.setAdapter(adapter);
		dictListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 
			}
		});
	}
	public List<LoadDictAdapterModel> getDictList(){
		Set<String> loadedList=dictToDB.getAllLoadedFiles();
		List<String> dictList = ABFileHelper.list(Configure.APP_DICTS_PATH);
		List<LoadDictAdapterModel> list = new ArrayList<LoadDictAdapterModel>();
		for(String s:loadedList){
			LoadDictAdapterModel model= new LoadDictAdapterModel();
			model.setDictPath(s);
			model.setWordNum(ABFileHelper.getLineCount(s));
			model.setLoaded(true);
			list.add(model);
		}
		for(String s:dictList){
			if(loadedList.contains(s))
				continue;
			LoadDictAdapterModel model= new LoadDictAdapterModel();
			model.setDictPath(s);
			model.setWordNum(ABFileHelper.getLineCount(s));
			model.setLoaded(false);
			list.add(model);
		}
		return list;
	}
}
