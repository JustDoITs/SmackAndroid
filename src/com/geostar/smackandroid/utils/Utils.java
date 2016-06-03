package com.geostar.smackandroid.utils;

import com.geostar.smackandroid.BuildConfig;

import android.util.Log;

public class Utils {

	public static final String LOG_TAG = "Log.debug";
	/**
	 * 输出日志
	 * @param tag
	 * @param msg
	 */
	public static final void logDebug(String tag,String msg){
		if(BuildConfig.DEBUG){
			Log.d(tag,msg);
		}
	}

	public static void logDebug(String msg) {
		if(BuildConfig.DEBUG){
			Log.d(LOG_TAG,msg);
		}
	}
	
}
