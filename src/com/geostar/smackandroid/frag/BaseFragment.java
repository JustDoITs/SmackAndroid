package com.geostar.smackandroid.frag;

import android.app.Fragment;
import android.content.ServiceConnection;

import com.geostar.smackandroid.AfterLoginActivity;
import com.geostar.smackandroid.service.XMPPService;

public abstract class BaseFragment extends Fragment implements ServiceConnection {

	private AfterLoginActivity mActivity;
	protected XMPPService mService;
	
	
	public BaseFragment(AfterLoginActivity activity) {
		mActivity = activity;
	}
	
	
}
