package com.geostar.smackandroid;

import org.jivesoftware.smack.AbstractXMPPConnection;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ListFragment;

import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public abstract class BaseFragment extends ListFragment implements ServiceConnection {

	private AbstractXMPPConnection mXmppConnection;
	
	
	public BaseFragment(AbstractXMPPConnection conn ) {
		mXmppConnection = conn;
	}


	/**
	 * 默认获取并设置 XMPPService 
	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mXmppConnection = ((XMPPBinder)service).getService().getXMPPConnection();
	}


	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}
	
	public AbstractXMPPConnection getXMPPConnection(){
		return mXmppConnection;
	}
	
	public boolean checkConnection(){
		if(mXmppConnection != null && mXmppConnection.isConnected()
				){
			return true;
		}
		return false;
	}
	
}
