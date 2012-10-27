package com.souldak.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.souldak.model.DictOld;
import com.souldak.util.SharePreferenceHelper;


public class DictManager {
	//dictname--unitcount pair
	public static HashMap<String,Integer> dictIndexes;
	private Context context;
	private  static final String DICT_INDEX_MAP = "dict_index_map";
	private Gson gson=new Gson();
	public DictManager(Context context){
		this.context = context;
		dictIndexes = gson.fromJson((String) SharePreferenceHelper.getPreferences(DICT_INDEX_MAP, context),HashMap.class);
		if(dictIndexes==null)
			dictIndexes = new HashMap<String, Integer>();
	}
	public void addDict(String dictName,int unitCount){
		dictIndexes.put(dictName, unitCount);
		saveDictIndexes();
	}
	public void deleteDict(String dictName){
		dictIndexes.remove(dictName);
		saveDictIndexes();
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
	/**
	 * @param dictName
	 * @return a dict if exists, null otherwise
	 */
//	public Dict getDict(String dictName){
//		if(dictIndexes==null)
//			return null;
//		String path = dictIndexes.get(dictName);
//		Dict dict = new Dict(context, dictName, path);
//		return dict;
//	}
	public boolean hasDict(String dictName){
		if(dictIndexes==null)
			return false;
		if(dictIndexes.containsKey(dictName))
			return true;
		else
			return false;
		
	}
	
}
