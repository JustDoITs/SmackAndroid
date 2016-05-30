package com.geostar.smackandroid.frag;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.ListFragment;

import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public abstract class BaseFragment extends ListFragment implements ServiceConnection {

	private MainActivity mActivity;
	private XMPPService mService;
	
	
	public BaseFragment(MainActivity activity) {
		mActivity = activity;
	}


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
