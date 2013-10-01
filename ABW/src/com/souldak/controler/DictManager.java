package com.souldak.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.souldak.db.DBFactory;
import com.souldak.db.DictDBHelper;
import com.souldak.model.DictModel;
import com.souldak.util.SharePreferenceHelper;


public class DictManager {
	//dictname--unitcount pair
	public static HashMap<String,DictModel> dictIndexes;
	private Context context;
	private  static final String DICT_INDEX_MAP = "dict_index_map";
	private Gson gson=new Gson();
	private DictDBHelper dictDBHelper;
	public DictManager(Context context){
		this.context = context;
		dictDBHelper = new DictDBHelper();
		dictIndexes = dictDBHelper.getAll();
		if(dictIndexes==null)
			dictIndexes = new HashMap<String, DictModel>();
	}
	public void addDict(String dictName,int unitCount,int wordCount){
		DictModel dict = new DictModel(dictName,unitCount,wordCount);
		if(!hasDict(dictName)){
			dictIndexes.put(dictName, dict);
			dictDBHelper.insert(dict);
		}
	}
	public void deleteDict(String dictName){
		dictIndexes.remove(dictName);
		dictDBHelper.deleteDict(dictName);
	}
	 
	public void saveDictIndexes(){
		
		String value = gson.toJson(dictIndexes);
		SharePreferenceHelper.savePreferences(DICT_INDEX_MAP, value, context);
	}
	public  List<String> getDictList(){
		List<String> list = new ArrayList<String>();
		if(dictIndexes!=null){
			list.addAll(dictIndexes.keySet());
		}
		Collections.sort(list);
		return list;
	}
 
	public boolean hasDict(String dictName){
		if(dictIndexes==null)
			return false;
		if(dictIndexes.containsKey(dictName))
			return true;
		else
			return false;
		
	}
	
}
