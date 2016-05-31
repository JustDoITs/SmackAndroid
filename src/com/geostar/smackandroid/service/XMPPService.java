package com.geostar.smackandroid.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.geostar.smackandroid.R;

public class XMPPService extends Service {

	private static final String TAG = "XMPPService";
	
	ExecutorService mExecService = Executors.newFixedThreadPool(2);
	
    private AbstractXMPPConnection mXmppConnection;
    private String mUsername,mPassword;

    private final IBinder mBinder = new XMPPBinder();
    private Chat mCurChat;
    private List<String> mChatThreads = new ArrayList<String>();
    
    private List<Chat> mAllChats = new ArrayList<Chat>();

    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	Log.d(TAG,"usrName:" + mUsername);
    	super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	Log.d(TAG,"usrName:" + mUsername);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG,"Onbind");
        return mBinder;
    }

    public class XMPPBinder extends Binder {
        public XMPPService getService(){
            return XMPPService.this;
        }
    }


    private void connect(String usrName,String password) throws IOException, InterruptedException, XMPPException, SmackException {
    	SmackConfiguration.DEBUG = true;
    	
        String hostIp = getResources().getString(R.string.server_ip_address);
        String serviceName = getResources().getString(R.string.server_name);
//        Jid mJid = null;
//        mJid = JidCreate.from(usrName,hostIp,getResources().);
//        mXmppConnection = new XMPPTCPConnection(usrName, password,hostIp);
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(usrName, password)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(hostIp)
                .setServiceName(serviceName)
                .setResource(getString(R.string.defalut_resource_name))
                .setPort(getResources().getInteger(R.integer.server_port))
                .build();

        mXmppConnection = new XMPPTCPConnection(config);
        mXmppConnection.connect();

        // ������������Ϣ����
        registerPacketListener();
    }

    private void registerPacketListener() {
    	// StanzaListener Ϊ�첽�ģ�����PacketCollector Ϊͬ���� 
//        StanzaListener presenceTypelistener = new StanzaListener() {
//            @Override
//            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//                sendMsgNotification();
//                Log.d(TAG,"Recv a message PresenceTypeFilter.AVAILABLE - getStanzaId : " +  packet.getStanzaId());
//            }
//        };
//        StanzaListener Messagelistener = new StanzaListener() {
//            @Override
//            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
//                sendMsgNotification();
//                if(packet instanceof org.jivesoftware.smack.packet.Message){
//                    org.jivesoftware.smack.packet.Message msg = ((org.jivesoftware.smack.packet.Message)packet);
//                    Log.d(TAG,"Recv a message - MessageWithBodiesFilter : " +  msg.getBody());
//                }
//            }
//        };
////        mXmppConnection.addAsyncStanzaListener(listener,ForEveryMessage.INSTANCE);
//       //  MessageWithBodiesFilter.INSTANCE��ForEveryMessage.INSTANCE Ϊ��ͬ����Ϣ������
//        mXmppConnection.addAsyncStanzaListener(Messagelistener, MessageWithBodiesFilter.INSTANCE);
//        mXmppConnection.addAsyncStanzaListener(presenceTypelistener, PresenceTypeFilter.AVAILABLE);
        
    }

    private void sendMsgNotification() {

    }

    public AbstractXMPPConnection getXMPPConnection() {
        return mXmppConnection;
    }

    public void connect() {
        if(!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mPassword)){
            login(mUsername,mPassword,null);
        }
    }

    public void reconnect(){
    	mExecService.execute(new Runnable() {
			@Override
			public void run() {
				try {
		            mXmppConnection.connect();
		            mXmppConnection.login(mUsername,mPassword);
		        } catch (XMPPException e) {
		            e.printStackTrace();
		        } catch (SmackException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
		});
    }


    /**
     * Login
     * @param usrName
     * @param pwd
     * @param callback 不可执行UI操作
     */
    public void login(final String usrName,final String pwd,final XMPPLoginCallback callback) {
        Runnable work = new Runnable() {
            @Override
            public void run() {
				try {
					connect(usrName, pwd);
					login(usrName, pwd);
				} catch (IOException | InterruptedException | XMPPException
						| SmackException e) {
					e.printStackTrace();
					// TODO: 登录失败处理
					if (callback != null) {
						callback.onLoginFailed(e);
					}
					return;
				}
                if(mXmppConnection.isConnected() && callback!=null) {
                    callback.onLoginSuccess();
                }
            }
        };
        mExecService.execute(work);
    }


    private void login(String usrName,String password) throws XMPPException, SmackException, IOException  {
        mUsername = usrName;
        this.mPassword = password;
		mXmppConnection.login(usrName, password);
        Presence presence = new Presence(Presence.Type.available,
                getResources().getString(R.string.default_login_state), 1, Presence.Mode.available);
        mXmppConnection.sendStanza(presence);
    }




    /**
     * 发送消息
     * @param toUser
     * @param msg
     */
    public void sendMessage(final String toUser, final String msg,final ChatMessageListener msgListener){
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                ChatManager chatmanager = ChatManager.getInstanceFor(mXmppConnection);
//                EntityJid chatOjb = null;
//                try {
//                    chatOjb = JidCreate.entityFullFrom(toUser,mXmppConnection.getXMPPServiceDomain().toString(),null).asEntityJidIfPossible();
//                } catch (XmppStringprepException e) {
//                    e.printStackTrace();
//                }
                String userJid = toUser + "@" + mXmppConnection.getServiceName();
                if(msgListener != null){
                    mCurChat = chatmanager.createChat(toUser,msgListener);
                }else{
                    mCurChat = chatmanager.createChat(toUser,mDefaultMsgListener);
                }

                if(!mChatThreads.contains(mCurChat.getThreadID())){
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

    private ChatMessageListener mDefaultMsgListener = new ChatMessageListener() {

        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {

            try {
                mCurChat.sendMessage(message.getBody());
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    };
    
    public void addChatThread(Chat chat){
    	if(!mChatThreads.contains(chat.getThreadID())){
    		mAllChats.add(chat);
    		mChatThreads.add(chat.getThreadID());
    		
    	}
    }
    
    
    public List<Chat> getAllChatThread(){
    	return mAllChats;
    }
    

}

