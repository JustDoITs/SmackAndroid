package com.geostar.smackandroid.utils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.sasl.SASLErrorException;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.geostar.smackandroid.BuildConfig;

public class Utils {

	public static final String LOG_TAG = "Log.debug";
	
	/**
	 * 输出日志
	 * <br/>
	 * 需要 BuildConfig.DEBUG = true
	 * @param tag
	 * @param msg
	 */
	public static final void logDebug(String tag,String msg){
		if(BuildConfig.DEBUG){
			Log.d(tag,msg);
		}
	}

	/**
	 * 使用默认tag（ {@link #LOG_TAG "Log.debug"}） 输出debug 级别log
	 * <br/>
	 * 需要 BuildConfig.DEBUG = true
	 * @param msg
	 */
	public static void logDebug(String msg) {
		if(BuildConfig.DEBUG){
			Log.d(LOG_TAG,msg);
		}
	}
	
	
	/** 选取文件 
	 * @param context Activity obj
	 * @param FILE_SELECT_CODE 请求码
	 */
	public static void showFileChooser(Activity context,int FILE_SELECT_CODE) {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	 
	    try {
	    	context.startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        Toast.makeText(context, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
	    }
	}
}
