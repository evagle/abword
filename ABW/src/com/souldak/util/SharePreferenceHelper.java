package com.souldak.util;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SharePreferenceHelper {
	public static void  removePreferences(String key,Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.remove(key);
		editor.commit();
	}
	public static void  removeAll(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
	}
	public static void savePreferences(String key, String value,Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	public static Set<String> getPreferences(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getAll().keySet();
	}
	public static Object getPreferences(String key,Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.contains(key))
			return prefs.getAll().get(key);
		else
			return null;
	}
}
