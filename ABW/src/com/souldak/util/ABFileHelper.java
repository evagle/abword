package com.souldak.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.souldak.config.Configure;

import android.os.Environment;
import android.util.Log;

public class ABFileHelper {
	private String filePath;

	// private BufferedReader reader;
	public ABFileHelper(String filePath) {
		this.filePath = filePath;
		// open();
	}

	public static BufferedReader open(String filePath) {
		BufferedReader reader = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File file = new File(filePath);
			InputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e3) {
				Log.e("ABFileReader",
						"Load File Failed! FileNotFoundException filepath="
								+ filePath + " error message:"
								+ e3.getMessage());
				return null;
			}
			try {
				in.mark(4);
				byte[] first3bytes = new byte[3];
				in.read(first3bytes);
				in.reset();
				// �ҵ��ĵ���ǰ����ֽڲ��Զ��ж��ĵ����͡�
				if (first3bytes[0] == (byte) 97 && first3bytes[1] == (byte) 98
						&& first3bytes[2] == (byte) 97) {
					reader = new BufferedReader(new InputStreamReader(in));
					Log.d("ABFileReader", "File is encode with ASCII");
				} else if (first3bytes[0] == (byte) 0xEF
						&& first3bytes[1] == (byte) 0xBB
						&& first3bytes[2] == (byte) 0xBF) {// utf-8
					reader = new BufferedReader(new InputStreamReader(in,
							"utf-8"));
					Log.d("ABFileReader", "File is encode with utf-8");
				} else if (first3bytes[0] == (byte) 0xFF
						&& first3bytes[1] == (byte) 0xFE) {

					reader = new BufferedReader(new InputStreamReader(in,
							"unicode"));
					Log.d("ABFileReader", "File is encode with unicode");
				} else if (first3bytes[0] == (byte) 0xFE
						&& first3bytes[1] == (byte) 0xFF) {

					reader = new BufferedReader(new InputStreamReader(in,
							"utf-16be"));
					Log.d("ABFileReader", "File is encode with utf-16be");
				} else if (first3bytes[0] == (byte) 0xFF
						&& first3bytes[1] == (byte) 0xFF) {

					reader = new BufferedReader(new InputStreamReader(in,
							"utf-16le"));
					Log.d("ABFileReader", "File is encode with utf-16le");
				} else {

					reader = new BufferedReader(
							new InputStreamReader(in, "GBK"));
					Log.d("ABFileReader", "File is encode with GBK");
				}
			} catch (UnsupportedEncodingException e1) {
				Log.e("ABFileReader",
						"Load  File Failed! UnsupportedEncodingException error message:"
								+ e1.getMessage());
			} catch (IOException e) {
				Log.e("ABFileReader",
						"Load  File Failed! IOException error message:"
								+ e.getMessage());
			} catch (NullPointerException e) {
				Log.e("ABFileReader",
						"Load  File Failed! NullPointerException error message:"
								+ e.getMessage());
			}

		} else {
			Log.e("ABFileReader", "SDcard was'nt mounted.");
		}
		return reader;
	}

	public static List<String> list(String path) {
		return list(new File(path));
	}

	public static List<String> list(File f) {
		List<String> fileNames = new ArrayList<String>();
		if (f.isDirectory()) {
			// �����ļ������е����
			File[] files = f.listFiles();
			// ���ж�����û��Ȩ�ޣ����û��Ȩ�޵Ļ����Ͳ�ִ����
			if (null == files)
				return fileNames;

			for (int i = 0; i < files.length; i++) {
				fileNames.addAll(list(files[i]));
			}
		}
		// ������ļ��Ļ�ֱ�Ӽ���
		else {
			fileNames.add(f.getAbsolutePath());
		}
		Log.i("ABFileHelper", "Get " + fileNames.size() + " files.");
		return fileNames;
	}

	public static int getLineCount(String path) {
		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new FileReader(new File(path)));
			lnr.skip(Long.MAX_VALUE);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (lnr == null)
			return 0;
		return lnr.getLineNumber();
	}

	public static void rewriteFile(String fileName, List<String> content) {
		try {
			FileWriter writer = new FileWriter(fileName, false);
			for(String str:content){
				writer.write(str+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Log.e("ABFileHelper", "rewriteFile error: "+e.getMessage());
		}
	}
    public static List<String> readLines(String fileName){
    	 BufferedReader reader = ABFileHelper.open(fileName);
    	 if(reader == null){
    		 Log.e("ABFileHelper", "open file failed");
    		 return null;
    	 }
    	 List<String> lines =new ArrayList<String>();
    	 String line;
    	 try {
			while((line = reader.readLine())!=null){
				 lines.add(line);
			 }
			return lines;
		} catch (IOException e) {
			Log.e("ABFileHelper", "readLines error: "+e.getMessage());
			return null;
		}
    }
	public static void writeObjectToFile(String filePath, Object object) {
		try {
			FileOutputStream outStream = new FileOutputStream(filePath);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					outStream);
			objectOutputStream.writeObject(object);
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object getObjectFromFile(String filePath) {
		FileInputStream freader;
		Object ret = null;
		try {
			freader = new FileInputStream(filePath);
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			ret = objectInputStream.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
