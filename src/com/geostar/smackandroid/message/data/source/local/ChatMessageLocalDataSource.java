package com.geostar.smackandroid.message.data.source.local;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.message.data.dao.ChatMessageDao;
import com.geostar.smackandroid.message.data.dao.DaoMaster;
import com.geostar.smackandroid.message.data.dao.ChatMessageDao.Properties;
import com.geostar.smackandroid.message.data.source.ChatMessageDataSource;

public class ChatMessageLocalDataSource implements ChatMessageDataSource {

	/** 对应的聊天记录db文件 */
	private String mDataSourcePath;
	
	private ChatMessageDao mChatDao;
	
	
	public ChatMessageLocalDataSource(Context context, String dataSourcePath) {
		super();
		this.mDataSourcePath = dataSourcePath;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dataSourcePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		DaoMaster.createAllTables(db, false);
		mChatDao = new DaoMaster(db).newSession().getChatMessageDao();
	}

	@Override
	public List<ChatMessage> getAllMessages() {
		// TODO: 分页
		return mChatDao.queryBuilder().list();
	}


	@Override
	public long saveChatMessage(ChatMessage msg) {
		return mChatDao.insert(msg);
	}
	
	@Override
	public void close() {
		if(mChatDao != null){
			mChatDao.getDatabase().close();
		}
	}

	@Override
	public boolean deleteMessage(ChatMessage msg) {
		mChatDao.delete(msg);
		return true;
	}

	/**
	 * 如果找不到，则返回null
	 */
	@Override
	public ChatMessage getChatMessage(int id) {
		List<ChatMessage> result = mChatDao.queryBuilder().where(Properties.Id.eq(id)).list();
		if(result.size() > 0){
			return result.get(0);
		}else{
			return null;
		}
	}

}
