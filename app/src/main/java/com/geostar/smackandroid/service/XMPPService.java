package com.geostar.smackandroid.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.geostar.smackandroid.R;
import com.geostar.smackandroid.config.Configuration;
import com.geostar.smackandroid.message.ChatActivity;
import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.message.data.source.ChatMessageDataSource;
import com.geostar.smackandroid.message.data.source.ChatMessageRepository;
import com.geostar.smackandroid.message.data.source.local.ChatMessageLocalDataSource;
import com.geostar.smackandroid.utils.Utils;
import com.geostar.smackandroid.utils.XMPPUtils;
/**
 * 默认的服务器设置（IP 端口 服务名）配置在 xml 中
 * @author jianghanghang
 *
 */
public class XMPPService extends Service implements IXMPPService,IChatMsgSubject,IChatMsgObserver, FileTransferListener{

	private static final String TAG = "XMPPService";
	
	public static ExecutorService mExecService = Executors.newFixedThreadPool(2);
	
    public static void doInBackground(Runnable work){
    	mExecService.execute(work);
    }
    
    public class XMPPBinder extends Binder {
        public XMPPService getService(){
            return XMPPService.this;
        }
    }
    
    private List<IChatMsgObserver> mChatMessageObses = new ArrayList<IChatMsgObserver>();
    
    private final IBinder mBinder = new XMPPBinder();
	
    private AbstractXMPPConnection mXmppConnection;
    private String mUsername,mPassword;
    private static final int DEFAULT_NOTI_ID = 0x90;
    
    private Chat mCurChat;
    
    /** 未通知的聊天消息 */
    private List<ChatMessage> mNewUnReadMessages = new ArrayList<ChatMessage>();

	private FileTransferManager mFileTransManager;
    
    
    @Override
    public void onCreate() {
    	Utils.logDebug(TAG,"usrName:" + mUsername);
    	super.onCreate();
    	// 为自己注册一个消息观察
    	registerChatMessageObserver(this);
//    	NetworkChangeReceiver recv = new NetworkChangeReceiver();
//    	recv.addNetworkChangeListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Utils.logDebug(TAG,"usrName:" + mUsername);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Utils.logDebug(TAG,"Onbind");
        return mBinder;
    }

    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	unregisterChatMessageObserver(this);
    }
    
    @Override
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

                try {
                    mCurChat.sendMessage(msg);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    
    @Override
    public void logout(){
    	if(mXmppConnection != null && mXmppConnection.isConnected()){
    		mXmppConnection.disconnect();
    	}
    }

    
    private void connect(String usrName,String password) throws IOException, InterruptedException, XMPPException, SmackException {
    	SmackConfiguration.DEBUG = true;
    	
        String hostIp = getResources().getString(R.string.server_ip_address);
        String serviceName = getResources().getString(R.string.server_name);
        // Smack 4.2 JID 获取方式
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
        registerPacketListener();
    }

    /** 注册消息监听  */
    private void registerPacketListener() {
        StanzaListener messagelistener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                if(packet instanceof org.jivesoftware.smack.packet.Message){
                    org.jivesoftware.smack.packet.Message msg = ((org.jivesoftware.smack.packet.Message)packet);
                    ChatMessage chatmsg = XMPPUtils.toChatMessage(msg);
                    Utils.logDebug(TAG,"接收到一条消息: " +  msg.getBody());
                    /* ----------	 当接收到带有body的 聊天消息时的处理逻辑 ；
                     * 			    	 此时可能应用不在前台  -------- */
                    mNewUnReadMessages.clear();
                    if(!mNewUnReadMessages.contains(chatmsg)){
                    	mNewUnReadMessages.add(chatmsg);
                    }
                    saveNewChatMessage(chatmsg);
                    notifyNewChatMessage();
                    /* -------------- -------------------- */
                }
            }
        };
//        mXmppConnection.addAsyncStanzaListener(listener,ForEveryMessage.INSTANCE);
        mXmppConnection.addAsyncStanzaListener(messagelistener, MessageWithBodiesFilter.INSTANCE);
		if(mFileTransManager == null){
			mFileTransManager = FileTransferManager.getInstanceFor(mXmppConnection);
			mFileTransManager.addFileTransferListener(this);
		}
    }

    protected void saveNewChatMessage(ChatMessage msg) {
    	// TODO: 
    	String chatDbDSKey = null;
    	if( msg.getType().equals(Type.chat.toString()) ){
    		chatDbDSKey = XMPPUtils.getJidWithoutRes(msg.getFrom());
    		Utils.logDebug("is a chat getThread msg.getFrom(): " + chatDbDSKey + "; thread: "+ msg.getThread());
    	}else if( msg.getType().equals(Type.groupchat.toString()) ){
    		chatDbDSKey = msg.getThread() ;
    		Utils.logDebug("is a multiChat getThread : " + msg.getThread());
    	}
    	
    	ChatMessageRepository repo = ChatMessageRepository.getInstance();
    	if( !repo.checkoutDS(chatDbDSKey) ){
    		File msgFile = new File(Configuration.getUserChatMsgDir(this, mUsername) + File.separator + chatDbDSKey + ".db3");
        	if(!msgFile.isFile()){
        		try {
        			msgFile.createNewFile();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
        	}
        	Utils.logDebug("msgFile:" + msgFile.getAbsolutePath());
    		ChatMessageDataSource chatDS = new ChatMessageLocalDataSource(this, msgFile.getAbsolutePath());
    		repo.addChatDataSource(chatDbDSKey, chatDS);
    	}
    	repo.saveChatMessage(msg);
    	//XMPPUtils.toChatMessage(
//    	ChatRecordDataSource mdataSource = new LocalChatRecordDS(this, recordFile.getAbsolutePath());
//    	Utils.logDebug("File path:" + recordFile.getAbsolutePath());
//    	ChatRecordRepository repo = ChatRecordRepository.getInstance(mdataSource);
//    	ChatRecord record = repo.queryChatRecordByChatTo(msg.getFrom());
//    	if(record == null){
//    		record = new ChatRecord();
//    		record.
//    	}
	}

	/**发送通知栏通知  */
    private void sendMsgNotification(List<ChatMessage> msges) {
    	ChatMessage msg = msges.get(0);
    	
    	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.ic_launcher)
    	        .setContentTitle(msg.getFrom().split("@")[0])// 设置标题为联系人
    	        .setContentText( msg.getBody())
    	        .setTicker(msg.getFrom().split("@")[0] + " : " + msg.getBody())// 状态栏滚动消息内容
    	        .setAutoCancel(true); // 设置通知内容为消息内容
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(this, ChatActivity.class);
    	// 仅放置最新一条消息
    	resultIntent.putExtra(ChatActivity.KEY_USER, msg.getFrom());
    	ArrayList<String> strMessages = new ArrayList<>();
    	for(ChatMessage m : msges){
    		strMessages.add(m.getBody());
    	}
    	resultIntent.putExtra(ChatActivity.KEY_MSG, strMessages);
    	
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    	// Adds the back stack for the Intent (but not the Intent itself)
    	stackBuilder.addParentStack(ChatActivity.class);
    	// Adds the Intent that starts the Activity to the top of the stack
    	stackBuilder.addNextIntent(resultIntent);
    	PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
    				0, PendingIntent.FLAG_UPDATE_CURRENT );
    	mBuilder.setContentIntent(resultPendingIntent);
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// 默认不发送多条通知，即使来自不同的联系人，后来消息会覆盖新来的消息
    	mNotificationManager.notify(DEFAULT_NOTI_ID, mBuilder.build());
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
    
    
    /* 聊天消息观察者相关  */
	@Override
	public void registerChatMessageObserver(IChatMsgObserver obs) {
		if(!mChatMessageObses.contains(obs)){
			mChatMessageObses.add(obs);
			// 如果有其他的消息观察者，则移除服务里的
			if(mChatMessageObses.size() > 2 && mChatMessageObses.contains(this)){
				mChatMessageObses.remove(this);
			}
		}
	}

	@Override
	public void unregisterChatMessageObserver(IChatMsgObserver obs) {
		if(mChatMessageObses.contains(obs)){
			mChatMessageObses.remove(obs);
			// 如果当前移除了所有观察者，则添加服务自身作为后台观察
			if(mChatMessageObses.size() == 0){
				mChatMessageObses.add(this);
			}
		}
	}

	@Override
	public void notifyNewChatMessage() {
		for(IChatMsgObserver obs : mChatMessageObses){
			obs.update(mNewUnReadMessages);
		}
	}

	@Override
	public void update(List<ChatMessage> msgs) {
		sendMsgNotification(msgs);
	}

	@Override
	public void setChatMsgSubject(IChatMsgSubject chatMsgSubject) {// 自己就是Subject，do nothing
	}
	
	
	/** 
	 * 监听到文件传输请求
	 * 直接接收，然后更新聊天记录
	 */
	@Override
	public void fileTransferRequest(FileTransferRequest request) {
		Utils.logDebug("Get A file Transfer Request,FileName:" + request.getFileName());
		// 接收保存文件
		IncomingFileTransfer inft = request.accept();
		String filePath = Configuration.getUserFileRecDir(this, mXmppConnection.getUser()) + File.separator + request.getFileName();
		try {
			inft.recieveFile( new File(filePath) );
		} catch (SmackException | IOException e) {
			// TODO 异常处理
			e.printStackTrace();
		}
		Utils.logDebug("文件接受完毕");
		
		// 更新数据库记录
		ChatMessageRepository.getInstance().checkoutDS(XMPPUtils.getJidWithoutRes(request.getRequestor()));
		ChatMessage msg = new ChatMessage();
		msg.setBody(filePath); // body 为文件路径
		msg.setFrom(request.getRequestor());// 文件传输者
		msg.setTime(System.currentTimeMillis());
		msg.setTo(mXmppConnection.getUser());
		ChatMessageRepository.getInstance().saveChatMessage(msg);
		
		// 通知UI 更新消息列表
		mNewUnReadMessages.clear();
		mNewUnReadMessages.add(msg);
		notifyNewChatMessage();
		
	}

}

