package com.geostar.smackandroid.service;

public interface IChatMsgSubject {
	
	void registerChatMessageObserver(IChatMsgObserver obs);
	
	/** 注销一个消息观察者，如在界面不可见时  */
	void unregisterChatMessageObserver(IChatMsgObserver obs);
	
	void notifyNewChatMessage();
	
}
