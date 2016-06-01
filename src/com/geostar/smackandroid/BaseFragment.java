package com.geostar.smackandroid;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ListFragment;

import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public abstract class BaseFragment extends ListFragment implements ServiceConnection {

	private MainActivity mActivity;
	private XMPPService mService;
	
	
	public BaseFragment(MainActivity activity) {
		mActivity = activity;
	}


	/**
	 * 默认获取并设置 XMPPService 
	 * @see android.content.ServiceConnection#onServiceConnected(android.content.ComponentName, android.os.IBinder)
	 */
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		mService = ((XMPPBinder)service).getService();
	}


	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}
	
	public XMPPService getXMPPService(){
		return mService;
	}
	
	
	public boolean checkConnection(){
		if(mService != null && mService.getXMPPConnection() != null
//				&& mService.getXMPPConnection().isConnected()
				){
			return true;
		}
		return false;
	}
	
}
