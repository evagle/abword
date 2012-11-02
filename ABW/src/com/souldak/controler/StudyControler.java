package com.souldak.controler;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.souldak.config.Configure;
import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.db.UnitDBHelper;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;
import com.souldak.util.ABFileHelper;

public class StudyControler {
	private Unit unit;
	private int showedPosition;
	private WordItem current;
	private Context context;
	private static String CURRENT_UNIT_WORDS="current_unit_words_";
	private String dictName;
	private int unitId;
	private WordDBHelper wordDBHelper;
	public StudyControler(Context context ,String dictName,int unitId){
		this.unit  = new Unit();
		UnitDBHelper unitDBHelper = new UnitDBHelper(dictName);
		unit = unitDBHelper.getUnit(unitId, dictName);
		unitDBHelper.close();
		showedPosition = 0;
		this.dictName = dictName;
		this.unitId = unitId;
		wordDBHelper = new WordDBHelper(dictName);
		
	}
	public void close(){
		wordDBHelper.close();
	}
	public void loadCurrentUnit(){
		List<String> list = ABFileHelper.readLines(Configure.APP_DATA_PATH+CURRENT_UNIT_WORDS+dictName);
		if(list==null || !unit.parseFromString(list))
			unit.initWordsList();
	}
	public void saveCurrentUnitToFile(){
		ABFileHelper.rewriteFile(Configure.APP_DATA_PATH+CURRENT_UNIT_WORDS+dictName, unit.wordListToString());
	}
	public boolean hasNext(STUDY_TYPE studyType){
		if(studyType.equals(STUDY_TYPE.LEARN_NEW)){
			if(unit.getNonMemodWords().size()>0)
				return true;
			else
				return false;
		}else if(studyType.equals(STUDY_TYPE.REVIEW)){
			if(showedPosition >= unit.getShowedWords().size())
				return false;
			else
				return true;
		}else
			return false;
	}
	public WordItem next(STUDY_TYPE studyType){
		if(showedPosition+1<unit.getShowedWords().size()){
			current = unit.getShowedWords().get(showedPosition++);
			return current;
		}else if(studyType.equals(STUDY_TYPE.LEARN_NEW)){
			if(unit.getNonMemodWords().size()>0){
				WordItem current = unit.getNonMemodWords().get(0);
				current = wordDBHelper.getWord(current.getWord());
				unit.getNonMemodWords().remove(0);
				unit.getShowedWords().add(current);
				showedPosition++;
				return current;
			}else
				return null;
		}else if(studyType.equals(STUDY_TYPE.REVIEW)){
			if(unit.getMemodWords().size()>0){
				WordItem current = unit.getMemodWords().get(0);
				current = wordDBHelper.getWord(current.getWord());
				unit.getMemodWords().remove(0);
				unit.getShowedWords().add(current);
				showedPosition++;
				return current;
			}else
				return null;
		}
		return null;
	}
	public void resetMemodList(){
		unit.getMemodWords().addAll(unit.getShowedWords());
		Collections.sort(unit.getMemodWords());
		unit.getShowedWords().clear();
	}
	public boolean currentUnitFinished(){
		boolean isFinished =unit.allFinished();
		UnitDBHelper unitDBHelper = new UnitDBHelper(unit.getDictName());
		unitDBHelper.update(unit);
		unitDBHelper.close();
		return isFinished;
	}
	public WordItem previous(STUDY_TYPE studyType){
		if(showedPosition>0){
			current = unit.getShowedWords().get(--showedPosition);
			return current;
		}else {
			return null;
		}
	}
	public void finishMemoWord(WordItem w,Date startTime,double timeDelta,int grade){
		UnitDBHelper unitDBHelper = new UnitDBHelper(unit.getDictName());
		WordDBHelper wordDBHelper = new WordDBHelper(unit.getDictName());
		if(w.getIngnore()==1){
			unit.setIgnoreCount(unit.getIgnoreCount()+1);
		}else if(w.getMemoList().size()==1){
			unit.addMemodCount();
		}
		w.addMemoRecord(startTime, timeDelta, grade);
		wordDBHelper.update(w);
		unitDBHelper.update(unit);
		unitDBHelper.close();
		wordDBHelper.close();
//		words.put(w.getWord(), w);
//		oldWords.add(w);
//		
//		for(int i=memoedList.size()-1;i>=0;i--){
//			if(memoedList.get(i).getId()==w.getId()){
//				memoedList.set(i, w);//�����Ѽ����б���Ķ�Ӧ�ĵ��ʣ������;�˳�����һ������ô��û�и����ˣ�gradeĬ�Ͼ���0
//				break;
//			}
//		}
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
