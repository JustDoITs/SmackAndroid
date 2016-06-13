package com.geostar.smackandroid.chat.data.source;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.geostar.smackandroid.chat.data.dao.ChatRecord;
import com.geostar.smackandroid.chat.data.dao.ChatRecordDao;
import com.geostar.smackandroid.chat.data.dao.DaoMaster;

public class LocalChatRecordDS implements ChatRecordDataSource {

	private String mDataSourcePath;
	
	private ChatRecordDao mChatRecordDao;
	
	
	public LocalChatRecordDS(Context context, String dataSourcePath) {
		super();
		this.mDataSourcePath = dataSourcePath;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(dataSourcePath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		DaoMaster.createAllTables(db, false);
		mChatRecordDao = new DaoMaster(db).newSession().getChatRecordDao();
	}
	
	@Override
	public void close() {
		if(mChatRecordDao != null){
			mChatRecordDao.getDatabase().close();
		}
	}

	@Override
	public List<ChatRecord> getChatList() {
		return mChatRecordDao.queryBuilder().list();
	}

	@Override
	public long addChatRecord(ChatRecord record) {
		return mChatRecordDao.insert(record);
	}

	@Override
	public void removeChatRecord(ChatRecord record) {
		mChatRecordDao.delete(record);
	}

	@Override
	public void updateChatRecord(ChatRecord upRecord) {
		mChatRecordDao.update(upRecord);
	}

}
