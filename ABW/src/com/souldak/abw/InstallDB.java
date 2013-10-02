package com.souldak.abw;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.souldak.config.Configure;
import com.souldak.util.SharePreferenceHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InstallDB  {
	private Context context;
	public InstallDB(Context context){
		this.context = context;
	}
	public boolean isInstalled(){
		Object installed =  SharePreferenceHelper.getPreferences("IS_INSTALLED", context);
		return installed!=null &&installed.equals("1");
	}
	public void install() {
		// 接收安装广播
		if (isInstalled()==false) {
			SharePreferenceHelper.savePreferences("IS_INSTALLED", "1", context);
			Log.i("InstallDB","安装数据库中");
			try {
				OutputStream out = new FileOutputStream(new File(Configure.DATABASE_ROOT_PATH+"dictstorage.zip"));
				InputStream in = context.getAssets().open("db/dictstorage.zip.1");
				copyStream(in, out);
				in.close();
				in = context.getAssets().open("db/dictstorage.zip.2");
				copyStream(in, out);
				in.close();
				out.close();
				unzip(Configure.DATABASE_ROOT_PATH+"dictstorage.zip",Configure.DATABASE_ROOT_PATH);
			} catch (FileNotFoundException e) {
				Log.e("InstallDB","FileNotFoundException:"+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e("InstallDB","IOException:"+e.getMessage());
				e.printStackTrace();
			}

		} 
	}

	public  int copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[2048];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	public  void unzip(String zipFile, String location) throws IOException {
	    try {
	        File f = new File(location);
	        if(!f.isDirectory()) {
	            f.mkdirs();
	        }
	        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
	        try {
	            ZipEntry ze = null;
	            while ((ze = zin.getNextEntry()) != null) {
	                String path = location + ze.getName();

	                if (ze.isDirectory()) {
	                    File unzipFile = new File(path);
	                    if(!unzipFile.isDirectory()) {
	                        unzipFile.mkdirs();
	                    }
	                }
	                else {
	                    FileOutputStream fout = new FileOutputStream(path, false);
	                    copyStream(new BufferedInputStream(zin),fout);
	                    fout.close();
	                }
	            }
	        }
	        finally {
	            zin.close();
	        }
	    }
	    catch (Exception e) {
	        Log.e("Unzip", "Unzip exception:", e);
	    }
	}

}
