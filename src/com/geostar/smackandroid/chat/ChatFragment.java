package com.geostar.smackandroid.chat;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.chat.ChatContract.Presenter;

public class ChatFragment extends BaseFragment implements OnRefreshListener,ChatContract.View {
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_messages, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return v;
	}


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		mSwipeRefreshLayout.setRefreshing(false);
	}


	@Override
	public void setPresenter(Presenter presenter) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String chooseFile() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
