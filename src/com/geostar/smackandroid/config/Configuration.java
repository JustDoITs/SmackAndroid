package com.geostar.smackandroid.config;

import java.io.File;

import com.geostar.smackandroid.R;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuration {
	
	/** 保存密码 */
	private static final String PREF_SAVE_PWD = "boolean_save_passwd";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";

	
	private static Configuration mInstance;
	
	private Context mContext;
	
	private Configuration(Context context){
		mContext = context;
	}
	
	public static synchronized Configuration getInstance(Context context){
		if(mInstance == null){
			mInstance = new Configuration(context);
		}
		return mInstance;
	}
	
	
	public String getSavedUserName(){
		SharedPreferences prefs = mContext.getSharedPreferences("user_pwd", Context.MODE_PRIVATE);
		return prefs.getString(PREF_USERNAME, "");
	}
	
	public String getSavedPassword(){
		SharedPreferences prefs = mContext.getSharedPreferences("user_pwd", Context.MODE_PRIVATE);
		return prefs.getString(PREF_PASSWORD, "");
	}
	
	public void setSavedUserName(String username){
		SharedPreferences prefs = mContext.getSharedPreferences("user_pwd", Context.MODE_PRIVATE);
		prefs.edit().putString(PREF_USERNAME, username).commit();
	}
	
	public void setSavedPassword(String pwd){
		SharedPreferences prefs = mContext.getSharedPreferences("user_pwd", Context.MODE_PRIVATE);
		prefs.edit().putString(PREF_PASSWORD, pwd).commit();
	}
	
	/** 获取记住密码设置 */
	public boolean isRememberPwdEnable(){
		SharedPreferences prefs = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
		return prefs.getBoolean(PREF_SAVE_PWD, false);
	}
	
	/** 开启记住密码 */
	public void setPasswordRememberEnable(boolean enable){
		SharedPreferences prefs = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
		prefs.edit().putBoolean(PREF_SAVE_PWD, enable).commit();
	}
	
	
	/**
	 * 用户数据保存目录
	 * @param context
	 * @param userName
	 * @return
	 */
	public static String getUserDataHome(Context context, String userName){
		File path = context.getDir("data",Context.MODE_PRIVATE);
    	File msgDir = new File(path,userName);
    	if(!msgDir.isDirectory()){
    		msgDir.mkdirs();
    	}
		return msgDir.getAbsolutePath();
	}
	
	/**
	 * 用户聊天记录保存目录
	 * @param context
	 * @param userName
	 * @return
	 */
	public static String getUserChatMsgDir(Context context, String userName){
		String filePath = getUserDataHome(context,userName) + File.separator + "msg_record";
		File f = new File(filePath);
		if(!f.isDirectory()){
			f.mkdirs();
		}
		return f.getAbsolutePath();
	}
	
	/** 获取sd卡上的数据存储目录 -- 文件接受  */
	public static String getUserFileRecDir(Context context,String userName){
		String path = context.getResources().getString(R.string.app_data_path);
    	File msgDir = new File(path,userName);
    	if(!msgDir.isDirectory()){
    		msgDir.mkdirs();
    	}
		return msgDir.getAbsolutePath();
	}
	
}
