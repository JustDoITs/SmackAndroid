package com.geostar.smackandroid.service;

import java.util.List;

import org.jivesoftware.smack.packet.Message;

public interface IChatMsgObserver {
	
	void update(List<Message> msgs);
	
	/**
	 * 设置观察主题
	 * @param chatMsgSubject
	 */
	void setChatMsgSubject(IChatMsgSubject chatMsgSubject);
	
}

