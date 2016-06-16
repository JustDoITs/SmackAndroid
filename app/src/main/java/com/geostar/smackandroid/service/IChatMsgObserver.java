package com.geostar.smackandroid.service;

import java.util.List;

import com.geostar.smackandroid.message.data.dao.ChatMessage;

public interface IChatMsgObserver {
	
	/**
	 * 当有新的消息来到时，会调用观察者的此方法
	 * <br/> <b> 注：此方法中不能有View 更新操作，需要post到主线程 </b>
	 * @param msgs
	 */
	void update(List<ChatMessage> msgs);
	
	/**
	 * 设置观察主题
	 * @param chatMsgSubject
	 */
	void setChatMsgSubject(IChatMsgSubject chatMsgSubject);
	
}

