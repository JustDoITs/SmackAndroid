package com.geostar.smackandroid.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;

import com.geostar.smackandroid.service.IXMPPConnectionStateChanged;

public abstract class IManager implements IXMPPConnectionStateChanged {
	
	private static ExecutorService mThreadPool =Executors.newFixedThreadPool(2);
	
	private AbstractXMPPConnection mXmppConnection;
	
	public AbstractXMPPConnection getXMPPConnection(){
		if(mXmppConnection == null){
//			throw new ConnectException("Xmpp connection not ready!!!");
		}
		return mXmppConnection;
	}
	
	public void setXMPPConnection(AbstractXMPPConnection conn){
		mXmppConnection = conn;
	}
	
	public boolean isConnectAvailable(){
		return mXmppConnection!=null && mXmppConnection.isConnected();
	}
	
	
	public class ConnectException extends Exception{
		public ConnectException(String detailMessage) {
			super(detailMessage);
		}
	}
	
	public static void executeBackgroudWork(Runnable work){
		mThreadPool.execute(work);
	}

	@Override
	public void onXMPPConnect(boolean isAvalable,AbstractXMPPConnection connt) {
		if(isAvalable){
			setXMPPConnection(connt);
		}
	};
	
	

}
