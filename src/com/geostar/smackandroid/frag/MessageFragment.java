package com.geostar.smackandroid.frag;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geostar.smackandroid.AfterLoginActivity;
import com.geostar.smackandroid.R;

public class MessageFragment extends BaseFragment {

	public MessageFragment(AfterLoginActivity activity) {
		super(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.messages, null);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}
	

}
