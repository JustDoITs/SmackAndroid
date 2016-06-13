package com.geostar.smackandroid.utils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.sasl.SASLErrorException;

import android.util.Log;

import com.geostar.smackandroid.BuildConfig;

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
	
	
	/**
	 * 简单处理下XMPP的异常信息
	 * @param e
	 * @return
	 */
	public static String resloveLoginException(Exception e){
		if(e instanceof XMPPException){
			if(e instanceof XMPPErrorException){
				XMPPErrorException erp = (XMPPErrorException) e;
				XMPPError.Type type = erp.getXMPPError().getType();
			}
			if(e instanceof SASLErrorException){
				return "登录失败：用户名或密码错误";
			}
		}else if(e instanceof SmackException){
			if(e instanceof ConnectionException){
				return "登录失败：无法连接到服务器";
			}
		}
		return "未知错误";
	}
}
