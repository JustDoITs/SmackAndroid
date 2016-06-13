package com.geostar.smackandroid.chat.data.source;

import java.util.List;

import com.geostar.smackandroid.base.BaseDataSource;
import com.geostar.smackandroid.chat.data.dao.ChatRecord;

public interface ChatRecordDataSource extends BaseDataSource{

	/** 获取聊天记录列表  */
	List<ChatRecord> getChatList();
	
	long addChatRecord(ChatRecord record);
	
	void removeChatRecord(ChatRecord record);
	
	void updateChatRecord(ChatRecord upRecord);
	
	
}
