package com.geostar.smackandroid.message;

import java.util.List;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.base.BaseView;
import com.geostar.smackandroid.message.data.dao.ChatMessage;

public interface ChatContract {

	interface View extends BaseView<Presenter> {

		String chooseFile();
//		void updataContactPresenceState(String who,Presence presence);

		void updateChatList(List<ChatMessage> msgs);
		
	}

	interface Presenter extends BasePresenter {
		
		void sendFile(String filePath);
		
		void sendMessage(String msg);
		
		void destory();
		
	}
	
}
