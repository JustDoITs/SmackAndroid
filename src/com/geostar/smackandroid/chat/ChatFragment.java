package com.geostar.smackandroid.chat;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.R;

public class ChatFragment extends BaseFragment implements OnRefreshListener {

	public ChatFragment(MainActivity activity) {
		super(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.frag_messages, null);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		super.onServiceDisconnected(name);
		
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
	

}
