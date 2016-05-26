package com.example.smackandroid.frag;

import android.app.Fragment;
import android.content.ServiceConnection;

import com.example.smackandroid.AfterLoginActivity;
import com.example.smackandroid.service.XMPPService;

public abstract class BaseFragment extends Fragment implements ServiceConnection {

	private AfterLoginActivity mActivity;
	protected XMPPService mService;
	
	
	public BaseFragment(AfterLoginActivity activity) {
		mActivity = activity;
	}
	
	
}
