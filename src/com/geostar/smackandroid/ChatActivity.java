package com.geostar.smackandroid;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.MessageWithBodiesFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

import android.app.ListActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

/**
 * 聊天
 * @author jianghanghang
 *
 */
public class ChatActivity extends ListActivity implements OnRefreshListener {

	protected static final String TAG = "ChatActivity";
	private XMPPService mXmppService;
	/**聊天对象 */
	private String mChatOjb;
	private ChatAdapter mAdapter;
	
	private List<Message> mMsgLists = new ArrayList<Message>();
	
	private EditText mMsgInput;
	private Button mMsgSendBtn;
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	/** 进入时消息 */
	private String firstInMsg;
	
	private  XMPPService getXmppService(){
		return mXmppService;
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPBinder)service).getService();
			registerMessageListener();
		}
	};
	
	private void registerMessageListener() {
		// TODO Auto-generated method stub
		StanzaListener Messagelistener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                if(packet instanceof org.jivesoftware.smack.packet.Message){
                    final org.jivesoftware.smack.packet.Message msg = ((org.jivesoftware.smack.packet.Message)packet);
//                    Log.d(TAG,"Recv a message - MessageWithBodiesFilter : " +  msg.getBody());
                    getListView().post(new Runnable() {
						
						@Override
						public void run() {
							receiverANewMessage(msg);
						}
					});
                }
            }
        };
        mXmppService.getXMPPConnection().addAsyncStanzaListener(Messagelistener, MessageWithBodiesFilter.INSTANCE);
	}
	

	/** 刷新List */
	protected void initListView() {
		mAdapter = new ChatAdapter(this, mMsgLists);
		if(firstInMsg != null){
			mMsgLists.add(new Message("me", firstInMsg));
		}
		getListView().setAdapter(mAdapter);
	}
	
	private void receiverANewMessage(Message msg) {
		if(getListView().getAdapter() == null){
			initListView();
		}
		mMsgLists.add(msg);
		mAdapter.notifyDataSetChanged();
		getListView().setSelection(mMsgLists.size()-1);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String inMsg = null;
		if(mChatOjb == null){
			Intent args = getIntent();
			if(args == null){
				finish();
			}
			
			mChatOjb = args.getStringExtra("user");
			firstInMsg = args.getStringExtra("msg");
			
			if(TextUtils.isEmpty(mChatOjb)){
				finish();
			}
		}
		
		setContentView(R.layout.frag_chat_room);
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		
		bindService(new Intent(this,XMPPService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
	
		getActionBar().setTitle(mChatOjb.split("@")[0]);
		initListView();
		
		mMsgSendBtn = (Button) findViewById(R.id.btn_send_msg);
		mMsgInput = (EditText) findViewById(R.id.et_input_msg);
		mMsgSendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendMessage();
			}
		});
		
	}


	/** 
	 * 发送消息
	 */
	protected void sendMessage() {
		// TODO Auto-generated method stub
		if(getXmppService() != null && getXmppService().getXMPPConnection() != null ){
		}else{
			Toast.makeText(this, "您已离线，请重新登录后发送", Toast.LENGTH_SHORT).show();
			return;
		}
		String msg = mMsgInput.getText().toString();
		if(TextUtils.isEmpty(msg)){
			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}

		Message sendMsg = new Message();
		sendMsg.setBody(msg);
		mMsgLists.add(sendMsg);
		mAdapter.notifyDataSetChanged();
		getListView().setSelection(mMsgLists.size()-1);
		
		// 发送消息
		ChatManager chatManager = ChatManager.getInstanceFor(getXmppService().getXMPPConnection());
	
		Chat newChat  =	chatManager.createChat(mChatOjb);
//		getXmppService().addChatThread(newChat);// nouse
		try {
			newChat.sendMessage(sendMsg);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMsgInput.setText("");
		
	}

	/**
	 * 聊天消息列表Adapter
	 * 直接使用Smack 的 Message 对象为其数据item
	 * @author jianghanghang
	 *
	 */
	class ChatAdapter extends BaseAdapter{
		
		private static final int MAX_MSG_SIZE = 100;
		
		private List<Message> mMsgDatas;
		
		private LayoutInflater mInflater;
		
		public ChatAdapter(Context context,List<Message> mMsgDatas) {
			super();
			this.mMsgDatas = mMsgDatas;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mMsgDatas.size();
		}

		@Override
		public Message getItem(int position) {
			return mMsgDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.listitem_chat_msg_all_com, null);
				holder= new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Message msg = getItem(position);
			if(msg.getTo().contains(mChatOjb)){// 自己发出的消息
				convertView.findViewById(R.id.chat_msg_mine).setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.chat_msg_others).setVisibility(View.GONE);
				holder.msgMine.setText(msg.getBody());
			}else{
				convertView.findViewById(R.id.chat_msg_mine).setVisibility(View.GONE);
				convertView.findViewById(R.id.chat_msg_others).setVisibility(View.VISIBLE);
				holder.msgOther.setText(msg.getBody());
			}
			return convertView;
		}
		
		class ViewHolder {
			
			ImageView iconOther,iconMine;
			TextView msgOther,msgMine;
			
			public ViewHolder(View convertView) {
				iconMine =(ImageView) convertView.findViewById(R.id.chat_msg_mine).findViewById(R.id.iv_icon);
				msgMine = (TextView) convertView.findViewById(R.id.chat_msg_mine).findViewById(R.id.tv_msg_content);
				
				iconOther =(ImageView) convertView.findViewById(R.id.chat_msg_others).findViewById(R.id.iv_icon);
				msgOther = (TextView) convertView.findViewById(R.id.chat_msg_others).findViewById(R.id.tv_msg_content);
			}
		}
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(getXmppService() != null && getXmppService() !=null && !getXmppService().getXMPPConnection().isConnected()){
			getXmppService().reconnect();
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mXmppService != null && mServiceConnection != null){
			unbindService(mServiceConnection);
		}
		super.onDestroy();
	}




	@Override
	public void onRefresh() {
		// TODO 刷新动作
		mSwipeRefreshLayout.setRefreshing(false);
	}
}
