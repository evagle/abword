package com.souldak.controler;

import java.util.Date;

import com.souldak.config.ConstantValue.STUDY_TYPE;
import com.souldak.db.UnitDBHelper;
import com.souldak.db.WordDBHelper;
import com.souldak.model.Unit;
import com.souldak.model.WordItem;

public class StudyControler {
	private Unit unit;
	private int showedPosition;
	private WordItem current;
	private WordDBHelper wordDBHelper;
	private UnitDBHelper unitDBHelper;
	public StudyControler(Unit unit,WordDBHelper wordDBHelper,UnitDBHelper unitDBHelper){
		this.unit = unit;
		this.wordDBHelper = wordDBHelper;
		this.unitDBHelper = unitDBHelper;
		showedPosition = 0;
		
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
				unit.getNonMemodWords().remove(0);
				unit.getShowedWords().add(current);
				showedPosition++;
				return current;
			}else
				return null;
		}else if(studyType.equals(STUDY_TYPE.REVIEW)){
			if(unit.getMemodWords().size()>0){
				WordItem current = unit.getMemodWords().get(0);
				unit.getMemodWords().remove(0);
				unit.getShowedWords().add(current);
				showedPosition++;
				return current;
			}else
				return null;
		}
		return null;
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
		w.addMemoRecord(startTime, timeDelta, grade);
		wordDBHelper.update(w);
		unit.addMemodCount();
		unitDBHelper.update(unit);
		
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
}
