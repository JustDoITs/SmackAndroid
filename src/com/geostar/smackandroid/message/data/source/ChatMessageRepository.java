package com.geostar.smackandroid.message.data.source;

import java.util.List;
import java.util.Map;

import android.support.v4.util.ArrayMap;

import com.geostar.smackandroid.message.data.dao.ChatMessage;

/**
 * 包含和多个用户的聊天记录数据库，通过checkout 切换
 * @author jianghanghang
 *
 */
public class ChatMessageRepository implements ChatMessageDataSource {

	private static ChatMessageRepository INSTANCE = null;
	
	/**
	 * 本地数据源
	 */
	private ChatMessageDataSource mLocalDataSource;
	private String mCurrentKey;
	
	private Map<String,ChatMessageDataSource> mDataSources ;
	
	
	public static synchronized ChatMessageRepository getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ChatMessageRepository();
		}
		return INSTANCE;
	}
	
	
    public static void destroyInstance() {
    	if(INSTANCE != null){
    		INSTANCE.destory();
    	}
        INSTANCE = null;
    }

	
	private void destory() {
        for(ChatMessageDataSource ds: mDataSources.values()){
			ds.close();
		}
		mDataSources.clear();
	}


	public ChatMessageRepository() {
		super();
		mDataSources = new ArrayMap<String,ChatMessageDataSource>();
	}

	/**
	 * 添加数据源，并自动切换到此数据源
	 * @param key
	 * @param ds 
	 */
	public void addChatDataSource(String key, ChatMessageDataSource ds){
		if(!mDataSources.containsKey(key)){
			mDataSources.put(key, ds);
		}
		checkoutDS(key);
	}
	
	public void removeChatDataSource(String key, ChatMessageDataSource ds){
		if(mCurrentKey.equals(key)){
			mLocalDataSource.close();
			mLocalDataSource = null;
			mCurrentKey = null;
		}
		if(!mDataSources.containsKey(key)){
			mDataSources.remove(key);
		}
	}
	
	/**
	 * 切换到某一数据源
	 * @param key
	 * @return false - 不存在此数据源
	 */
	public synchronized boolean checkoutDS(String key){
		mLocalDataSource = mDataSources.get(key);
		if(mLocalDataSource == null){
			mCurrentKey = null;
			return false;
		}
		mCurrentKey = key;
		return true;
	}
	
	
	@Override
	public List<ChatMessage> getAllMessages() {
		checkDataSource();
		return mLocalDataSource.getAllMessages();
	}


	/**
	 * 关闭当前checkout出的聊天记录数据库
	 */
	@Override
	public void close() {
		if(mLocalDataSource != null){
			mLocalDataSource.close();
			mDataSources.remove(mCurrentKey);
			mCurrentKey = null;
		}
	}


	@Override
	public long saveChatMessage(ChatMessage msg) {
		checkDataSource();
		return mLocalDataSource.saveChatMessage(msg);
	}


	@Override
	public boolean deleteMessage(ChatMessage msg) {
		checkDataSource();
		return mLocalDataSource.deleteMessage(msg);
	}


	@Override
	public ChatMessage getChatMessage(int id) {
		checkDataSource();
		return mLocalDataSource.getChatMessage(id);
	}

	
	/**
	 * 检查当前是否选中数据源，如果没有，则抛出运行期异常
	 * @throws IllegalStateException
	 */
	private void checkDataSource() throws IllegalStateException{
		if(mLocalDataSource == null){
			throw new IllegalStateException("You must checkout a datasource first,before u can operate chat message!!!");
		}
	}
	
	
}
