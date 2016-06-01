package com.geostar.smackandroid;

import org.jivesoftware.smack.AbstractXMPPConnection;

import android.support.v4.app.ListFragment;

public abstract class BaseFragment extends ListFragment implements OnConnectionMake {

	private AbstractXMPPConnection mXmppConnection;
	
	
	@Override
	public void onServiceConnected(AbstractXMPPConnection conn) {
		mXmppConnection = conn;
	}


	public BaseFragment(AbstractXMPPConnection conn ) {
		mXmppConnection = conn;
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
