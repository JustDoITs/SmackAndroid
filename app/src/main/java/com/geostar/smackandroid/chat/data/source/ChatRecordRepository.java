package com.geostar.smackandroid.chat.data.source;

import java.util.List;

import com.geostar.smackandroid.chat.data.dao.ChatRecord;

public class ChatRecordRepository implements ChatRecordDataSource{

	private static ChatRecordRepository INSTANCE = null;
	
	/**
	 * 本地数据源
	 */
	private ChatRecordDataSource mLocalDataSource;
	
	
	public ChatRecordRepository(ChatRecordDataSource localDataSource) {
		mLocalDataSource = localDataSource;
	}


	public static ChatRecordRepository getInstance(ChatRecordDataSource localDataSource) {
		if (INSTANCE == null) {
			INSTANCE = new ChatRecordRepository(localDataSource);
		}
		return INSTANCE;
	}
	
	
    public static void destroyInstance() {
        INSTANCE = null;
    }


	@Override
	public void close() {
		mLocalDataSource.close();
	}


	@Override
	public List<ChatRecord> getChatList() {
		return mLocalDataSource.getChatList();
	}


	@Override
	public long addChatRecord(ChatRecord record) {
		return mLocalDataSource.addChatRecord(record);
	}


	@Override
	public void removeChatRecord(ChatRecord record) {
		mLocalDataSource.removeChatRecord(record);
	}


	@Override
	public void updateChatRecord(ChatRecord upRecord) {
		mLocalDataSource.updateChatRecord(upRecord);
	}


	@Override
	public ChatRecord queryChatRecordByIdentify(String identify) {
		// TODO Auto-generated method stub
		return mLocalDataSource.queryChatRecordByIdentify(identify);
	}


	@Override
	public ChatRecord queryChatRecordByChatTo(String who) {
		// TODO Auto-generated method stub
		return mLocalDataSource.queryChatRecordByChatTo(who);
	}
}
