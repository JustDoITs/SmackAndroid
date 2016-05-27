package com.geostar.smackandroid.manager.login;

import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import com.geostar.smackandroid.manager.IManager;


public class LoginManagerImpl extends IManager implements ILoginManager {
	
	@Override
	public void login(final String username,final String password,final OnLoginCallback callback) {
		if(getXMPPConnection() != null && getXMPPConnection().isConnected()){
			Runnable work = new Runnable() {
	            @Override
	            public void run() {
	                try {
//	                    connect(username,password);
	                	getXMPPConnection().login(password,password);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                } catch (XMPPException e) {
	                    e.printStackTrace();
	                } catch (SmackException e) {
	                    e.printStackTrace();
	                }
	                if(getXMPPConnection().isAuthenticated()) {
	                    callback.onSuccess();
	                }else{
	                    callback.onFailed();
	                }
	            }
	        };
	        executeBackgroudWork(work);
		}else{
			System.err.println("XMPP Service Connection is not Ready!!!");
		}
		
	}

	@Override
	public void register(String username, String password,
			OnLoginCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addOnLoginStateChangedListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onXMPPConnect(boolean isAvalable,AbstractXMPPConnection conn) {
		super.onXMPPConnect(isAvalable,conn);
	}

}
