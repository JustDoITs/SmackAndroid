package com.geostar.smackandroid.message.data.source;

import java.util.List;

import com.geostar.smackandroid.message.data.dao.ChatMessage;

public class ChatMessageRepository implements ChatMessageDataSource {

	private static ChatMessageRepository INSTANCE = null;
	
	
	/**
	 * 本地数据源
	 */
	private ChatMessageDataSource mLocalDataSource;
	
	
	public static ChatMessageRepository getInstance(ChatMessageDataSource localDataSource) {
		if (INSTANCE == null) {
			INSTANCE = new ChatMessageRepository(localDataSource);
		}
		return INSTANCE;
	}
	
	
    public static void destroyInstance() {
        INSTANCE = null;
    }

	
	public ChatMessageRepository(ChatMessageDataSource localDataSource) {
		super();
		this.mLocalDataSource = localDataSource;
	}

	@Override
	public List<ChatMessage> getAllMessages() {
		return mLocalDataSource.getAllMessages();
	}



	@Override
	public void close() {
		mLocalDataSource.close();
	}


	@Override
	public long saveChatMessage(ChatMessage msg) {
		return mLocalDataSource.saveChatMessage(msg);
	}


	@Override
	public boolean deleteMessage(ChatMessage msg) {
		return mLocalDataSource.deleteMessage(msg);
	}


	@Override
	public ChatMessage getChatMessage(int id) {
		return mLocalDataSource.getChatMessage(id);
	}

}
