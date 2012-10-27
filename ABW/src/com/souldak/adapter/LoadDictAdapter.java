package com.souldak.adapter;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.souldak.abw.R;
import com.souldak.config.Configure;
import com.souldak.config.ConstantValue;
import com.souldak.controler.DictToDB;
import com.souldak.model.LoadDictAdapterModel;

public class LoadDictAdapter extends ArrayAdapter<LoadDictAdapterModel> {
	private Activity context;
	private List<LoadDictAdapterModel> list;
	private DictToDB dictToDB;
	private Thread checkThread;
	private static HashMap<String, Boolean> isDictLoadingMap;

	public LoadDictAdapter(Context context, int textViewResourceId,
			List<LoadDictAdapterModel> list) {
		super(context, textViewResourceId);
		this.context = (Activity) context;
		this.list = list;
		dictToDB = new DictToDB(context);
		isDictLoadingMap = new HashMap<String, Boolean>();
	}

	class ViewHolder {
		protected ImageView icon;
		protected TextView dictName;
		protected TextView wordNum;
		protected TextView process;
		protected ImageView load;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View rowView = convertView;
		Log.d("LoadDictAdapter", "in getView,position="+position);
		final String name= list.get(position).getDictPath().substring(list.get(position).getDictPath().lastIndexOf('/')+1);
		if (rowView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.adapter_load_dict_row, null, true);
			holder.icon =  (ImageView) rowView.findViewById(R.id.adapter_load_dict_row_image);
			holder.dictName = (TextView) rowView.findViewById(R.id.adapter_load_dict_row_name);
			holder.wordNum = (TextView) rowView.findViewById(R.id.adapter_load_dict_row_num);
			holder.process = (TextView) rowView.findViewById(R.id.adapter_load_dict_row_process);
			holder.load = (ImageView) rowView.findViewById(R.id.adapter_load_dict_row_load);
			final int pos=position;
			holder.load.setOnClickListener(new View.OnClickListener(){
				@SuppressLint("HandlerLeak")
				public void onClick(View arg0) {
					String title="是否载入词典"+name+"？";
					if(list.get(pos).isLoaded()){
						title="是否重新载入词典"+name+"？";
					}
							
					new android.app.AlertDialog.Builder(context)//Context
					.setTitle(title)
					.setIcon(android.R.drawable.ic_dialog_alert)//图标
					
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {//按钮2
						public void onClick(DialogInterface arg0, int arg1) {
							//在线程中载入词典
							final String path = Configure.APP_DICTS_PATH+holder.dictName.getText();
							if(!ConstantValue.loadingStats.containsKey(path)||ConstantValue.loadingStats.get(path)==ConstantValue.LOAD_STAT_WAITING){
								ConstantValue.loadingStats.put(path, ConstantValue.LOAD_STAT_LODING);
								new Thread(){  
									public void run(){  
										isDictLoadingMap.put(path, true);
										dictToDB.loadDictFromFile(path);
										isDictLoadingMap.put(path, false);
									}  
								}.start(); 
							}
							if(checkThread==null||checkThread.getState().equals(Thread.State.TERMINATED))
								checkLoadingProcess(path,holder.wordNum.getText().toString());
							dictToDB.addLoadedFile(Configure.APP_DICTS_PATH+holder.dictName.getText(),"1");
						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					})
					.show();//显示					
					
				}});
			
			rowView.setTag(holder);
			if(position==0&&(checkThread==null||checkThread.getState().equals(Thread.State.TERMINATED))){
				checkLoadingProcess(list.get(0).getDictPath(),list.get(0).getWordNum()+"");
			}
		} else {
			holder = (ViewHolder) rowView.getTag();
		}
		holder.dictName.setText(name);
		holder.wordNum.setText(list.get(position).getWordNum()+"");
		
		if(ConstantValue.loadingProcess.containsKey(list.get(position).getDictPath())&&
				ConstantValue.loadingProcess.get(list.get(position).getDictPath())>=0)
			holder.process.setText(ConstantValue.loadingProcess.get(list.get(position).getDictPath())+"/"+list.get(position).getWordNum()+"");
		else
			holder.process.setText("");
		if(list.get(position).isLoaded())
			holder.load.setImageResource(R.drawable.reload_wooden);
		else
			holder.load.setImageResource(R.drawable.load_wooden);
		
		return rowView;
	}
	public void checkLoadingProcess(final String path,final String totalNum){
		 final Handler handler = new Handler() {
             @Override
             public void handleMessage(Message message) {
             	LoadDictAdapter.this.notifyDataSetChanged();
             }
         };

         checkThread=new Thread(){  
             public void run(){ 
             	int t=2;
             	while(t-->0){
             		if(!ConstantValue.loadingProcess.containsKey(path)){
             			try {
             				Thread.sleep(1000);
             			} catch (InterruptedException e) {
             				e.printStackTrace();
             			}
             		}
             	}
             	while(ConstantValue.loadingProcess.containsKey(path)&&
             			ConstantValue.loadingProcess.get(path)>=0||
             			(isDictLoadingMap.containsKey(path)&&isDictLoadingMap.get(path)==true)){
             		Message message = handler.obtainMessage(1, this);
                    handler.sendMessage(message);
             		Log.d("LoadDictAdapter","Process:"+ConstantValue.loadingProcess.get(path)+"/"+totalNum);
             		try {
         				Thread.sleep(1000);
         			} catch (InterruptedException e) {
         				e.printStackTrace();
         			}
             	}
             	ConstantValue.loadingStats.put(path, ConstantValue.LOAD_STAT_WAITING);
             }  
         };
         checkThread.start();
     		
	}
}
