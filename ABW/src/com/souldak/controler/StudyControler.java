package com.souldak.controler;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.content.Context;

import com.souldak.config.Configure;
import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.db.ClickStatsDBHelper;
import com.souldak.db.UnitDBHelper;
import com.souldak.db.WordDBHelper;
import com.souldak.model.ClickStatsItem;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.ABFileHelper;
import com.souldak.util.TimeHelper;

public class StudyControler {
	private Unit unit;
	private int showedPosition;
	private WordItem current;
	@SuppressWarnings("unused")
	private Context context;
	private static String CURRENT_UNIT_WORDS = "current_unit_words_";
	private String dictName;
	// private int unitId;
	private WordDBHelper wordDBHelper;
	private ClickStatsDBHelper clickStatsDBHelper;
	private boolean CLOSED = false;
	private HashMap<String, Integer> effectMap = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put("GOOD", 5);
			put("PASS", 3);
			put("BAD", 0);
		}
	};

	public StudyControler(Context context, String dictName, int unitId) {
		this.context = context;
		this.unit = new Unit();
		UnitDBHelper unitDBHelper = new UnitDBHelper(dictName);
		unit = unitDBHelper.getUnit(unitId, dictName);
		unitDBHelper.close();
		showedPosition = 0;
		this.dictName = dictName;
		// this.unitId = unitId;
		wordDBHelper = new WordDBHelper(dictName);
		clickStatsDBHelper = new ClickStatsDBHelper();
	}

	public void close() {
		wordDBHelper.close();
		clickStatsDBHelper.close();
		CLOSED = false;
	}

	public void loadCurrentUnit(boolean loadfromfile) {
		List<String> list = null;
		if (loadfromfile) {
			list = ABFileHelper.readLines(Configure.APP_DATA_PATH
					+ CURRENT_UNIT_WORDS + dictName + "_" + unit.getUnitId());
		}
		unit.init();
		if (list == null || !unit.parseFromString(list))
			unit.initWordsList();
	}

	public void updateIntervals() {
		if (new Random().nextInt(100) < 20) {
			new Thread(new Runnable() {
				public void run() {
					WordDBHelper helper = new WordDBHelper(unit.getDictName());
					for (WordItem word : unit.getWords()) {
						if (!CLOSED) {
							WordItem w = helper.getWord(word.getWord());
							if(w.getNextMemoDate()==null)
								continue;
							double interval = TimeHelper.getDiffDay(
									w.getNextMemoDate(), new Date());
							if (interval < 1)
								w.setInterval(1d);
							else
								w.setInterval(interval);
							word.setInterval(w.getInterval());
							helper.update(w);
						}else{
							break;
						}
					}
					helper.close();
				}
			}).start();
		}
	}

	public void saveCurrentUnitToFile() {
		ABFileHelper.rewriteFile(Configure.APP_DATA_PATH + CURRENT_UNIT_WORDS
				+ dictName + "_" + unit.getUnitId(), unit.wordListToString());
	}

	public int getProgress(STUDY_TYPE type) {
		if (type == STUDY_TYPE.REVIEW
				|| unit.getMemoedCount() + unit.getIgnoreCount() >= unit
						.getTotalWordCount()) {
			return unit.getShowedWords().size()
					* 100
					/ (unit.getShowedWords().size() + unit.getMemodWords()
							.size());
		} else {
			return (unit.getMemoedCount() + unit.getIgnoreCount()) * 100
					/ unit.getTotalWordCount();
		}

	}

	public boolean hasNext(STUDY_TYPE studyType) {
		if (studyType.equals(STUDY_TYPE.LEARN_NEW)) {
			if (unit.getNonMemodWords().size() > 0)
				return true;
			else
				return false;
		} else if (studyType.equals(STUDY_TYPE.REVIEW)) {
			if (showedPosition >= unit.getShowedWords().size())
				return false;
			else
				return true;
		} else
			return false;
	}

	public WordItem next(STUDY_TYPE studyType) {
		if (showedPosition + 1 < unit.getShowedWords().size()) {
			current = unit.getShowedWords().get(showedPosition++);
			return current;
		} else if (studyType.equals(STUDY_TYPE.LEARN_NEW)) {
			if (unit.getNonMemodWords().size() > 0) {
				WordItem current = unit.getNonMemodWords().get(0);
				current = wordDBHelper.getWord(current.getWord());
				unit.getNonMemodWords().remove(0);
				unit.getShowedWords().add(current);
				showedPosition++;
				return current;
			} else
				return null;
		} else if (studyType.equals(STUDY_TYPE.REVIEW)) {
			if (unit.getMemodWords().size() > 0) {
				WordItem current = unit.getMemodWords().get(0);
				current = wordDBHelper.getWord(current.getWord());
				unit.getMemodWords().remove(0);
				unit.getShowedWords().add(current);
				showedPosition++;
				return current;
			} else
				return null;
		}
		return null;
	}

	public void resetMemodList() {
		unit.getMemodWords().addAll(unit.getShowedWords());
		Collections.sort(unit.getMemodWords());
		unit.getShowedWords().clear();
	}

	public boolean currentUnitFinished() {
		boolean isFinished = unit.allFinished();
		UnitDBHelper unitDBHelper = new UnitDBHelper(unit.getDictName());
		unitDBHelper.update(unit);
		unitDBHelper.close();
		return isFinished;
	}

	public WordItem previous(STUDY_TYPE studyType) {
		if (showedPosition > 0) {
			current = unit.getShowedWords().get(--showedPosition);
			return current;
		} else {
			return null;
		}
	}

	public void finishMemoWord(WordItem w, Date startTime, double timeDelta,
			String gradeStr) {
		int grade = 0;
		if (gradeStr != null && effectMap.containsKey(gradeStr)) {
			grade = effectMap.get(gradeStr);
		}

		UnitDBHelper unitDBHelper = new UnitDBHelper(unit.getDictName());
		WordDBHelper wordDBHelper = new WordDBHelper(unit.getDictName());
		if (w.getIngnore() == 1) {
			unit.setIgnoreCount(unit.getIgnoreCount() + 1);
			unit.removeIgnoredWord(w);
		} else if (w.getMemoList().size() == 0) {
			unit.addMemodCount();
		}
		w.addMemoRecord(startTime, timeDelta, grade);
		wordDBHelper.update(w);
		unitDBHelper.update(unit);
		unitDBHelper.close();
		wordDBHelper.close();
		if (w.getIngnore() != 1) {
			updateClickStats(TimeHelper.dateToString(new Date()),
					effectMap.get(gradeStr));
			updateClickStats(ClickStatsDBHelper.SumRecordDate,
					effectMap.get(gradeStr));
		}
	}

	private void updateClickStats(String date, int grade) {
		ClickStatsItem item = clickStatsDBHelper.queryOne(date);
		boolean isnew = false;
		if (item == null) {
			isnew = true;
			item = new ClickStatsItem(date);
		}
		item.setTotal(item.getTotal() + 1);
		if (grade == 5)
			item.setGood(item.getGood() + 1);
		else if (grade == 3)
			item.setPass(item.getPass() + 1);
		else
			item.setBad(item.getBad() + 1);
		if (isnew)
			clickStatsDBHelper.insert(item);
		else
			clickStatsDBHelper.update(item);
	}

	public WordItem getCurrentWord() {
		return current;
	}

	public void setCurrentWord(WordItem current) {
		this.current = current;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}
