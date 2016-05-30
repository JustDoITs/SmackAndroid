package com.geostar.smackandroid.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.R;

public class AChatFrag extends BaseFragment {

	public AChatFrag(MainActivity activity) {
		super(activity);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.chat_room_layout, null);
		
		return v;
	}
	

}
