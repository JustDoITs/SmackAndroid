package com.geostar.smackandroid.chat;

import org.jivesoftware.smack.AbstractXMPPConnection;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.R;

public class ChatFragment extends BaseFragment implements OnRefreshListener {




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.frag_messages, null);
	}


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}
	

}
