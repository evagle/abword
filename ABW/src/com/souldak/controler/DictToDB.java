package com.souldak.controler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.souldak.config.ConstantValue;
import com.souldak.db.UnitDBHelper;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.ABFileHelper;
import com.souldak.util.SharePreferenceHelper;
import com.souldak.util.TimeHelper;

public class DictToDB {
	private Context context;
	private String STORE_NAME = "loaded_dicts";
	private SharedPreferences loadedDicts = null;
	private DictManager dictManager;
	private Gson gson = new Gson();
	private WordDBHelper wordDBHelper;
	private UnitDBHelper unitDBHelper;
	public DictToDB(Context context) {
		this.context = context;
		dictManager = new DictManager(context);
		 
	}

	/**
	 * SD�� Environment.getExternalStorageDirectory().getPath()
	 * 
	 * @param filePath
	 */
	public String loadDictFromFile(String filePath) {
		Date s=new Date();
		Log.d("DictLoader", "start to LoadDictFromFile . file:"
				+ filePath );
		BufferedReader reader = ABFileHelper.open(filePath);
		
		if (reader == null) {
			Log.e("DictLoader", "LoadDictFromFile failed. Open file:"
					+ filePath + " failed.");
			return null;
		}
		String ret = "";
		String tmp = null;
		List<WordItem> list=new ArrayList<WordItem>();

		HashMap<Integer,Integer> unitWordCount =new HashMap<Integer, Integer>();
		try {
			while ((tmp = reader.readLine()) != null) {
				if(tmp.equals(""))
					continue;
				WordItem w = gson.fromJson(tmp,WordItem.class);
				list.add(w);
				if(unitWordCount.containsKey(w.getUnit())){
					unitWordCount.put(w.getUnit(), unitWordCount.get(w.getUnit())+1);
				}else{
					unitWordCount.put(w.getUnit(),1);
				}
				//ConstantValue.loadingProcess.put(filePath, count);
				//Log.d("DictLoader", "load word: " + w.getWord());
				Log.d("DictLoader", "load word: " + tmp);
			}
			reader.close();
		} catch (IOException e) {
			Log.e("DictLoader",
					"LoadDictFromFile error while readLine. error message:"
							+ e.getMessage());
			e.printStackTrace();
		}
		//
		//
		if(list.size()==0)
			return null;
		String dictName = list.get(0).getDict();
		boolean hasDict = dictManager.hasDict(dictName);
		//Add dict to dictMap
		if(!hasDict)
			dictManager.addDict(dictName,unitWordCount.size());
		
		wordDBHelper = new WordDBHelper(dictName);
		unitDBHelper = new UnitDBHelper(dictName);
		 
		//save to database
		int count=1;
		for(WordItem item:list){
			ConstantValue.loadingProcess.put(filePath, count++);
			if(!wordDBHelper.hasWord(item))
				wordDBHelper.insert(item);
			else
				wordDBHelper.update(item);
		}
		for(Integer unitId:unitWordCount.keySet()){
			Unit unit = new Unit();
			unit.setUnitId(unitId);
			unit.setDictName(dictName);
			unit.setTotalWordCount(unitWordCount.get(unitId));
			unit.setDelegatedWord(unitDBHelper.getDelegateWord(unit));
			unit.setFinished(0);
			
			if(!unitDBHelper.hasUnit(unit))
				unitDBHelper.insert(unit);
			else
				unitDBHelper.update(unit);
		}
		ConstantValue.loadingProcess.put(filePath, -1);
		wordDBHelper.close();
		unitDBHelper.close();
		Date e=new Date();
		Log.d("DictLoader", "loadDictFromFile COST="+TimeHelper.getDiffMilliSec(e, s));
		return ret;
	}

	public boolean isDictLoaded(String dict){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.contains(dict);
	}
	public Set<String> getAllLoadedFiles(){
		Set<String> set = SharePreferenceHelper.getPreferences(context);
		Set<String>  ret = new HashSet<String>();
		for(String s:set){
			if(s.startsWith("DictToDB_"))
				ret.add(s.substring(9));
		}
		return ret;
	}
	public void addLoadedFile(String filename,String value){
		SharePreferenceHelper.savePreferences("DictToDB_"+filename, value, context);
	}
}
