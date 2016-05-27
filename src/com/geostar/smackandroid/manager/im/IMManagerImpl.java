package com.geostar.smackandroid.manager.im;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import android.text.TextUtils;

import com.geostar.smackandroid.manager.IManager;

public class IMManagerImpl extends IManager implements IIMManager {

	private Set<OnMessageArriveListener> mMessageListeners;
	
//	private List<Chat> mChats = new ArrayList<Chat>();
	
	private Chat mCurChat;
	
	private List<String> mChatThreads = new ArrayList<>();
	
	private StanzaFilter mDefaultMessageFilter = MessageWithBodiesFilter.INSTANCE;
	
	private StanzaListener mDefaultMessageListener = new StanzaListener() {
		
		@Override
		public void processPacket(Stanza packet) throws NotConnectedException {
			if(packet instanceof Message){
				for(OnMessageArriveListener listener : mMessageListeners){
					listener.onMessageArriver((Message)packet);
				}
			}
		}
	};
	
	
	@Override
	public void sendMessage(final String user,final Message msg) throws ConnectException {
		executeBackgroudWork(new Runnable() {
			
			@Override
			public void run() {
				ChatManager chatmanager = ChatManager
						.getInstanceFor(getXMPPConnection());
				if(TextUtils.isEmpty(user)){
					return;
				}
				String userJid = user;
				if(!user.contains("@")){
					userJid = user + "@" + getXMPPConnection().getServiceName();
				}
				mCurChat = chatmanager.createChat(userJid, null);

				if (!mChatThreads.contains(mCurChat.getThreadID())) {
					mChatThreads.add(mCurChat.getThreadID());
				}
				try {
					mCurChat.sendMessage(msg);
				} catch (SmackException.NotConnectedException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	@Override
	public void sendMessage(final String user,final String msg) throws ConnectException {
		
		executeBackgroudWork(new Runnable() {
			
			@Override
			public void run() {
				ChatManager chatmanager = ChatManager
						.getInstanceFor(getXMPPConnection());
				if(TextUtils.isEmpty(user)){
					return;
				}
				String userJid = user;
				if(!user.contains("@")){
					userJid = user + "@" + getXMPPConnection().getServiceName();
				}
				mCurChat = chatmanager.createChat(userJid, null);

				if (!mChatThreads.contains(mCurChat.getThreadID())) {
					mChatThreads.add(mCurChat.getThreadID());
				}
				try {
					mCurChat.sendMessage(msg);
				} catch (SmackException.NotConnectedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void addOnMessageArriveListener(OnMessageArriveListener listener) {
		if(mMessageListeners == null){
			mMessageListeners = new HashSet<OnMessageArriveListener>();
		}
		
		if(!mMessageListeners.contains(listener)){
			mMessageListeners.add(listener);
		}
		
	}

	@Override
	public void removeMessageArriveListener(OnMessageArriveListener listener) {
		if(mMessageListeners.contains(listener)){
			mMessageListeners.remove(listener);
		}
		
	}

	@Override
	public void onXMPPConnect(boolean isAvalable,AbstractXMPPConnection conn) {
		super.onXMPPConnect(isAvalable,conn);
		// TODO: 
		if(isAvalable){
			getXMPPConnection().addAsyncStanzaListener(mDefaultMessageListener, mDefaultMessageFilter);
		}else{
			getXMPPConnection().removeAsyncStanzaListener(mDefaultMessageListener);
		}
		
	}

}
