package com.geostar.smackandroid.utils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.sasl.SASLErrorException;

import com.geostar.smackandroid.message.data.dao.ChatMessage;

import android.support.annotation.NonNull;
import android.util.Log;

public class XMPPUtils {

	/**
	 * 简单处理下XMPP的登录异常信息
	 * @param e
	 * @return 异常信息
	 */
	public static String resloveLoginException(Exception e){
		if(e instanceof XMPPException){
			if(e instanceof XMPPErrorException){
				XMPPErrorException erp = (XMPPErrorException) e;
				XMPPError.Type type = erp.getXMPPError().getType();
				Log.e("XMPPException","XMPPError type:" + type);
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
	
	
	/** 获取用户名 
	 *  注：jid 用户名不能有@ */
	private static final String getBareJid(@NonNull String jid){
		return jid.split("@")[0];
	}
	
	/** 获取截去资源部分的用户名 如  admin@192.168.0.1
	 *  注：jid 用户名不能有@ */
	public static final String getJidWithoutRes(@NonNull String jid){
		return jid.split("/")[0];
	}

	// TODO: 消息类型处理 
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public static ChatMessage toChatMessage(@NonNull Message msg) {
		ChatMessage local = new ChatMessage();
		local.setBody(msg.getBody());
		local.setFrom(msg.getFrom());
		local.setIsRead(false);
		local.setSubject(msg.getSubject());
		local.setThread(msg.getThread());
		local.setTime(System.currentTimeMillis());
		local.setTo(msg.getTo());
		local.setType(msg.getType().toString());
		return local;
	}
	
}
