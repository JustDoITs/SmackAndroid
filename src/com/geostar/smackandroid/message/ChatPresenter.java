package com.geostar.smackandroid.message;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.content.Context;
import android.support.annotation.NonNull;

import com.geostar.smackandroid.config.Configuration;
import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.message.data.source.ChatMessageDataSource;
import com.geostar.smackandroid.message.data.source.ChatMessageRepository;
import com.geostar.smackandroid.message.data.source.local.ChatMessageLocalDataSource;
import com.geostar.smackandroid.service.IChatMsgObserver;
import com.geostar.smackandroid.service.IChatMsgSubject;
import com.geostar.smackandroid.utils.Utils;
import com.geostar.smackandroid.utils.XMPPUtils;


public class ChatPresenter implements ChatContract.Presenter, FileTransferListener,StanzaListener,IChatMsgObserver{

	
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
				mFileTransManager.addFileTransferListener(this);
			}
			mConnection.removeAsyncStanzaListener(this);
			mCurrentUser = XMPPUtils.getJidWithoutRes(mConnection.getUser());
		}else{
			System.out.println("------ ERROR: connnect not connected!!!");
		}
	}

	@Override
	public void start() {
		if(mFileTransManager == null){
			mFileTransManager = FileTransferManager.getInstanceFor(mConnection);
			mFileTransManager.addFileTransferListener(this);
		}
	}
	
	
	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		Utils.logDebug("Get A file Transfer Request,FileName:" + request.getFileName());
		IncomingFileTransfer inft = request.accept();
		try {
			inft.recieveFile(new File("/sdcard/" +request.getFileName()));
		} catch (SmackException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void sendFile(String filePath) {
		// TODO Auto-generated method stub
		
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
		
		openChat(mTo);
		// 存储消息
		ChatMessage chatmsg = new ChatMessage();
		chatmsg.setFrom(mCurrentUser);
		chatmsg.setTo(mTo);
		chatmsg.setBody(msg);
		chatmsg.setTime(System.currentTimeMillis());
		chatmsg.setType(Type.chat.toString());
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
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if(packet instanceof org.jivesoftware.smack.packet.Message){
            final org.jivesoftware.smack.packet.Message msg = ((org.jivesoftware.smack.packet.Message)packet);
            
//            Log.d(TAG,"Recv a message - MessageWithBodiesFilter : " +  msg.getBody());
//            getListView().post(new Runnable() {
//				
//				@Override
//				public void run() {
//					receiverANewMessage(msg);
//				}
//			});
        }
    }

	@Override
	public void destory() {
		if(mConnection != null){
			mConnection.removeAsyncStanzaListener(this);
		}
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
