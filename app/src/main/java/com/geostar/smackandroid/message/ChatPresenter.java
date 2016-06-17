package com.geostar.smackandroid.message;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.geostar.smackandroid.config.Configuration;
import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.message.data.source.ChatMessageDataSource;
import com.geostar.smackandroid.message.data.source.ChatMessageRepository;
import com.geostar.smackandroid.message.data.source.local.ChatMessageLocalDataSource;
import com.geostar.smackandroid.service.IChatMsgObserver;
import com.geostar.smackandroid.service.IChatMsgSubject;
import com.geostar.smackandroid.utils.Utils;
import com.geostar.smackandroid.utils.XMPPUtils;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChatPresenter implements ChatContract.Presenter,IChatMsgObserver{

	
	private AbstractXMPPConnection mConnection;
	
	private ChatContract.View mView;
	
	private FileTransferManager mFileTransManager;
	
	/** 不带resource  */
	private String mCurrentUser,mTo;
	
	private Context mContext;

	
	public ChatPresenter(@NonNull Context context,@NonNull AbstractXMPPConnection conn,@NonNull ChatContract.View view) {
		super();
		this.mContext = context;
		this.mConnection = conn;
		this.mView = view;
		view.setPresenter(this);
		
		if(mConnection != null && mConnection.isConnected()){
			if(mFileTransManager == null){
				mFileTransManager = FileTransferManager.getInstanceFor(mConnection);
//				mFileTransManager.addFileTransferListener(this);
			}
//			mConnection.removeAsyncStanzaListener(this);
			mCurrentUser = XMPPUtils.getJidWithoutRes(mConnection.getUser());
		}else{
			System.out.println("------ ERROR: connnect not connected!!!");
		}
	}

	@Override
	public void start() {
		if(mFileTransManager == null){
			mFileTransManager = FileTransferManager.getInstanceFor(mConnection);
//			mFileTransManager.addFileTransferListener(this);
		}
	}
	

	// TODO : 更新文件传输进度 + 放置到服务中去发送文件

	@Override
	public void sendFile(String filePath) {
		// Create the file transfer manager
		FileTransferManager manager = FileTransferManager.getInstanceFor(mConnection);

		// 因为发送文件必须要知道对方的fullJId，也就是包含资源部分的jid，所以先获取当前用户所有可用的fulljid
		Roster roster= Roster.getInstanceFor(mConnection);
		List<Presence> preses = roster.getPresences(mTo);
		List<String> sendTos = new ArrayList<>();
		for( Presence p : preses ){
			Utils.logDebug("To jid :" + mTo + "/" + p.getFrom() + ";status :" + p.getStatus());
			if(p.getStatus() != null){ // 如果Presence statu 为null，则表示此用户没有登录，也就没有资源ID
				sendTos.add(p.getFrom());
			}
		}
		if(sendTos.size() == 0){
			Toast.makeText(mContext, "对方不在线，无法发送文件！",Toast.LENGTH_LONG).show();
			return;
		}
		for(String sentTo : sendTos){
			// Create the outgoing file transfer
			OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(sentTo);
			// Send the file
			try {
				File file2Sent = new File(filePath);
				transfer.sendFile(file2Sent, "请接受");
				
				ChatMessage chatmsg = new ChatMessage();
				chatmsg.setFrom(mCurrentUser);
				chatmsg.setTo(sentTo);
				chatmsg.setBody(filePath);
				chatmsg.setTime(System.currentTimeMillis());
				// TODO: 
				chatmsg.setType(XMPPUtils.getFileMsgType(file2Sent).toString());
				
				boolean isDataSourceAvailable = ChatMessageRepository.getInstance().checkoutDS(XMPPUtils.getJidWithoutRes(mTo) );
				if(isDataSourceAvailable ){
					// 保存消息
					ChatMessageRepository.getInstance().saveChatMessage(chatmsg);
					// 更新UI
					List<ChatMessage> msgs = new ArrayList<ChatMessage>();
					msgs.add(chatmsg);
					mView.postNewMessageFromNoneUiThread(msgs);
				}else{
					Utils.logDebug("====== ERROR: datasource is not available");
				}
				
			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO : 可以通过transfer 去获取传输状态
//			transfer.getStatus();
		}
	}
	
	@Override
	public void sendMessage(String msg) {
		// 发送消息
		ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
		Chat newChat  =	chatManager.createChat(mTo);
		try {
			newChat.sendMessage(msg);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mTo = newChat.getParticipant();
		Utils.logDebug("Set to:" + mTo);
		
		openChat(mTo);
		// 存储消息
		ChatMessage chatmsg = new ChatMessage();
		chatmsg.setFrom(mCurrentUser);
		chatmsg.setTo(mTo);
		chatmsg.setBody(msg);
		chatmsg.setTime(System.currentTimeMillis());
		chatmsg.setType(ChatMessage.Type.text.toString());
		chatmsg.setThread(newChat.getThreadID());
		
		boolean isDataSourceAvailable = ChatMessageRepository.getInstance().checkoutDS(XMPPUtils.getJidWithoutRes(mTo) );
		if(isDataSourceAvailable ){
			// 保存消息
			ChatMessageRepository.getInstance().saveChatMessage(chatmsg);
			// 更新UI
			List<ChatMessage> msgs = new ArrayList<ChatMessage>();
			msgs.add(chatmsg);
			mView.postNewMessage(msgs);
		}else{
			Utils.logDebug("====== ERROR: datasource is not available");
		}

	}
	
	@Override
	public void destory() {
//		if(mConnection != null){
//			mConnection.removeAsyncStanzaListener(this);
//		}
	}

	
	@Override
	public void openChat(String chatObj) {
		mTo = chatObj;
    	String chatDbDSKey = XMPPUtils.getJidWithoutRes(chatObj);
    	ChatMessageRepository repo = ChatMessageRepository.getInstance();
    	if( !repo.checkoutDS(chatDbDSKey) ){
    		File msgFile = new File(Configuration.getUserChatMsgDir(mContext, mCurrentUser) + File.separator + chatDbDSKey + ".db3");
        	if(!msgFile.isFile()){
        		try {
        			msgFile.createNewFile();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
        	}
        	Utils.logDebug("openChat - msgFile:" + msgFile.getAbsolutePath());
    		ChatMessageDataSource chatDS = new ChatMessageLocalDataSource(mContext, msgFile.getAbsolutePath());
    		repo.addChatDataSource(chatDbDSKey, chatDS);
    	}
    	mView.refreshMessageList();
	}
	
	/**
	 * TODO:  分页显示
	 * */
	@Override
	public List<ChatMessage> getAllMessages() {
		return ChatMessageRepository.getInstance().getAllMessages();
	}

	@Override
	public String getCurrentUser() {
		return mCurrentUser;
	}
	

	@Override
	public void update(List<ChatMessage> msgs) {
		// TODO Auto-generated method stub
		Utils.logDebug("聊天Activity 收到新消息：" + msgs.size() );
		mView.postNewMessageFromNoneUiThread(msgs);
	}

	
	@Override
	public void setChatMsgSubject(IChatMsgSubject chatMsgSubject) {
		// TODO Auto-generated method stub
		
	}


}
