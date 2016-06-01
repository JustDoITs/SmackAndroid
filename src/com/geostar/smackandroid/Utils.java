package com.geostar.smackandroid;

import android.util.Log;

public class Utils {

	
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
	
}
