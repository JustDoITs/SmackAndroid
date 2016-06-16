package com.geostar.smackandroid.message;

import java.util.List;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.base.BaseView;
import com.geostar.smackandroid.message.data.dao.ChatMessage;

public interface ChatContract {

	interface View extends BaseView<Presenter> {

		String chooseFile();
//		void updataContactPresenceState(String who,Presence presence);
//		void updateChatList(List<ChatMessage> msgs);
		
		/** 有新消息来了  */
		void postNewMessageFromNoneUiThread(List<ChatMessage> newMsgs);
		
		void postNewMessage(List<ChatMessage> newMsgs);
		
		/** 刷新  */
		void refreshMessageList();
	}

	interface Presenter extends BasePresenter {
		
		/**
		 * 开启同某人的会话
		 * <br/> 设置聊天对象,设置聊天记录数据源
		 * @param chatObj
		 */
		void openChat(String chatObj);
		
		/**当前用户 jid */
		String getCurrentUser();
		
		/**
		 * 获取当前同当前聊天对象的所有的消息
		 * @return 
		 */
		List<ChatMessage> getAllMessages();
		
		
		void sendFile(String filePath);
		
		void sendMessage(String msg);
		
		
		/**
		 * 做一些销毁工作，如关闭数据源
		 */
		void destory();
		
	}
	
}
