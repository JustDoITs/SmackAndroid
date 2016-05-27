package com.geostar.smackandroid.manager.im;

import org.jivesoftware.smack.packet.Message;

import com.geostar.smackandroid.manager.IManager.ConnectException;


public interface IIMManager {
	
	public interface OnMessageArriveListener{
		
		void onMessageArriver(Message msg);
		
	}
	
	public void sendMessage(String user,Message msg) throws ConnectException;
	
	public void sendMessage(String user,String msg) throws ConnectException;
	
	public void addOnMessageArriveListener(OnMessageArriveListener listener);
	
	public void removeMessageArriveListener(OnMessageArriveListener listener);

}
