package com.geostar.smackandroid.config;

import java.io.File;

import android.content.Context;

public class Configuration {

	public static String getUserDataHome(Context context, String userName){
		File path = context.getDir("data",Context.MODE_PRIVATE);
    	File msgDir = new File(path,userName);
    	if(!msgDir.isDirectory()){
    		msgDir.mkdirs();
    	}
		return msgDir.getAbsolutePath();
	}
	
	
	public static String getUserChatMsgDir(Context context, String userName){
		String filePath = getUserDataHome(context,userName) + File.separator + "msg_record";
		File f = new File(filePath);
		if(!f.isDirectory()){
			f.mkdirs();
		}
		return f.getAbsolutePath();
	}
	
}
