package com.geostar.smackandroid.chat;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.base.BaseView;

public interface ChatContract {

	interface View extends BaseView<Presenter> {

		String chooseFile();
//		void updataContactPresenceState(String who,Presence presence);

	}

	interface Presenter extends BasePresenter {
		
		void sendFile(String filePath);
	}
	
}
