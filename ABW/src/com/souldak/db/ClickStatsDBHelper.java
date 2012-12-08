package com.souldak.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.souldak.model.ClickStatsItem;
import com.souldak.util.TimeHelper;

public class ClickStatsDBHelper {
	public static String tableName = "click_stats";
	private BaseDBHelper baseDBHelper;
	private static final String DATE = "date_id";
	private static final String TOTAL = "total";
	private static final String GOOD = "good";
	private static final String PASS = "pass";
	private static final String BAD = "bad";
	private static String createSqlStr;
	public static String SumRecordDate = "sum_id";

	public ClickStatsDBHelper() {
		createSqlStr = "CREATE TABLE " + tableName + "(" + DATE
				+ " text not null," + TOTAL + " integer not null," + GOOD
				+ " integer not null," + PASS + " integer not null," + BAD
				+ " integer not null);";

		baseDBHelper = new BaseDBHelper(tableName, createSqlStr);
	}

	public void close() {
		baseDBHelper.close();
	}

	private ContentValues toContentValues(ClickStatsItem item) {
		ContentValues values = new ContentValues();
		values.put(DATE, item.getDate());
		values.put(TOTAL, item.getTotal());
		values.put(GOOD, item.getGood());
		values.put(PASS, item.getPass());
		values.put(BAD, item.getBad());
		return values;
	}

	public boolean insert(ClickStatsItem item) {
		Date s = new Date();
		if (item == null) {
			Log.e("ClickStatsDBHelper",
					"insert canceled. ClickStatsItem is null");
			return false;
		}
		ContentValues values = toContentValues(item);
		if (baseDBHelper.insert(values)) {
			Date e = new Date();
			Log.d("ClickStatsDBHelper",
					"insert success COST= " + TimeHelper.getDiffMilliSec(e, s));
			return true;
		} else {
			return false;
		}
	}

	public boolean update(ClickStatsItem item) {
		Date s = new Date();
		if (item == null) {
			Log.e("ClickStatsDBHelper", "update click stats failed. stats=NULL");
			return false;
		}

		ContentValues values = toContentValues(item);
		boolean result = baseDBHelper.update(values, DATE + " = "
				+ " '" + item.getDate() + "'", null);
		Date e = new Date();
		if (!result) {
			Log.e("ClickStatsDBHelper", "update ClickStats failed COST= "
					+ TimeHelper.getDiffMilliSec(e, s));
			return false;
		} else {
			Log.d("ClickStatsDBHelper", "update ClickStats success COST= "
					+ TimeHelper.getDiffMilliSec(e, s));
			return true;
		}
	}

	// SELECT * FROM table WHERE
	// strftime('%s', date) BETWEEN strftime('%s', start_date) AND strftime('%s,
	// end_date)
	// strftime('%s','2004-01-01 02:34:56')
	private ClickStatsItem getOne(Cursor cursor) {
		if (cursor == null)
			return null;
		ClickStatsItem item = null;
		try {
			item = new ClickStatsItem();
			int dateIndex = cursor.getColumnIndex(DATE);
			int totalIndex = cursor.getColumnIndex(TOTAL);
			int goodIndex = cursor.getColumnIndex(GOOD);
			int passIndex = cursor.getColumnIndex(PASS);
			int badIndex = cursor.getColumnIndex(BAD);

			item.setDate(cursor.getString(dateIndex));
			item.setTotal(cursor.getInt(totalIndex));
			item.setGood(cursor.getInt(goodIndex));
			item.setPass(cursor.getInt(passIndex));
			item.setBad(cursor.getInt(badIndex));

		} catch (Exception ex) {
			Log.e("ClickStatsDBHelper",
					"getOne from cursor failed!" + ex.getMessage());
		}
		return item;
	}
	public ClickStatsItem queryOne(Date d) {
		return queryOne(TimeHelper.dateToString(d));
	}
	public ClickStatsItem queryOne(String date) {
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, DATE + " = "
				+ "'"+date+"'", null, null, null, null);
		if (cursor.getCount() == 0) {
			Log.w("ClickStatsDBHelper", "get 0 click stats.");
			return null;
		}
		cursor.moveToFirst();
		return getOne(cursor);
	}

	public ClickStatsItem getSumRecord() {
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, DATE + " = "
				+ SumRecordDate, null, null, null, null);
		if (cursor.getCount() == 0) {
			Log.w("ClickStatsDBHelper", "get 0 click stats.");
			return null;
		}
		cursor.moveToFirst();
		return getOne(cursor);
	}

	public List<ClickStatsItem> queryRange(Date startDate, Date endDate) {
		String startStr = TimeHelper.dateToString(startDate);
		String endStr = TimeHelper.dateToString(endDate);

		String sql = " date(`" + DATE + "`) BETWEEN date('" + startStr
				+ "') AND date('" + endStr + "');";
		Cursor cursor = baseDBHelper.query(new String[] { "*" }, sql, null,
				null, null, null);
		if (cursor.getCount() == 0) {
			Log.w("ClickStatsDBHelper", "get 0 click stats.");
			return null;
		}
		List<ClickStatsItem> list = new ArrayList<ClickStatsItem>();
		while (cursor.moveToNext()) {
			list.add(getOne(cursor));
		}
		cursor.close();
		Log.d("ClickStatsDBHelper", "get " + list.size() + " units.");
		return list;
	}
}
