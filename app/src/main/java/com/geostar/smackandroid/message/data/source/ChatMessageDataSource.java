package com.geostar.smackandroid.message.data.source;

import java.util.List;

import com.geostar.smackandroid.base.BaseDataSource;
import com.geostar.smackandroid.message.data.dao.ChatMessage;


/**
 * 聊天消息数据源接口定义
 * @author jianghanghang
 *
 */
public interface ChatMessageDataSource extends BaseDataSource{

	List<ChatMessage> getAllMessages();
	
	long saveChatMessage(ChatMessage msg);
	
	boolean deleteMessage(ChatMessage msg);
	
	ChatMessage getChatMessage(int id);
	
}
