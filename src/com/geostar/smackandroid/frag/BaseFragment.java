package com.geostar.smackandroid.frag;

import android.content.ServiceConnection;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.service.XMPPService;

public abstract class BaseFragment extends ListFragment implements ServiceConnection {

	private MainActivity mActivity;
	protected XMPPService mService;
	
	
	public BaseFragment(MainActivity activity) {
		mActivity = activity;
	}
	
	
}
