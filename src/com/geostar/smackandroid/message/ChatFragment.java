package com.geostar.smackandroid.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.message.ChatContract.Presenter;
import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.utils.Utils;

public class ChatFragment extends BaseFragment implements OnRefreshListener,ChatContract.View {
	
	private ChatAdapter mAdapter;
	
	private List<ChatMessage> mMsgLists = new ArrayList<ChatMessage>();
	
	private EditText mMsgInput;
	private Button mMsgSendBtn;
	private ImageButton mFileChooseBtn;
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private ChatContract.Presenter mPresenter;
	
	private static final int FILE_SELECT_CODE = 0x009;
	
	/**聊天对象 */
	private String mChatOjb;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_chat_room, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mMsgSendBtn = (Button) v.findViewById(R.id.btn_send_msg);
		mMsgInput = (EditText) v.findViewById(R.id.et_input_msg);
		mMsgSendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
		
		mFileChooseBtn = (ImageButton) v.findViewById(R.id.ib_chat_send_more);
		mFileChooseBtn.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View view) {
				Utils.showFileChooser(getActivity(), FILE_SELECT_CODE);
			}
		});
		return v;
	}
	
	

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		getActionBar().setTitle(mChatOjb.split("@")[0]);
		
		initListView();
		
		
	}

	/** 刷新List */
	protected void initListView() {
		mAdapter = new ChatAdapter(getActivity(), mMsgLists);
//		if(firstInMsg != null){
//			for(String msgContent : firstInMsg){
//				mMsgLists.add(new Message("me", msgContent));
//			}
//		}
		getListView().setAdapter(mAdapter);
	}
	
//	private void registerMessageListener() {
//		// TODO Auto-generated method stub
//		StanzaListener Messagelistener = new StanzaListener() {
//            
//        };
//        mXmppService.getXMPPConnection().addAsyncStanzaListener(Messagelistener, MessageWithBodiesFilter.INSTANCE);
//	}
	
	/** 
	 * 发送消息
	 */
	protected void sendMessage() {
		
		
		// TODO Auto-generated method stub
//		if(getXmppService() != null && getXmppService().getXMPPConnection() != null ){
//		}else{
//			Toast.makeText(this, "您已离线，请重新登录后发送", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		String msg = mMsgInput.getText().toString();
//		if(TextUtils.isEmpty(msg)){
//			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
//			return;
//		}

//		Message sendMsg = new Message();
//		sendMsg.setBody(msg);
//		mMsgLists.add(sendMsg);
//		mAdapter.notifyDataSetChanged();
//		getListView().setSelection(mMsgLists.size()-1);
//		
//		// 发送消息
//		ChatManager chatManager = ChatManager.getInstanceFor(getXmppService().getXMPPConnection());
//	
//		Chat newChat  =	chatManager.createChat(mChatOjb);
////		getXmppService().addChatThread(newChat);// nouse
//		try {
//			newChat.sendMessage(sendMsg);
//		} catch (NotConnectedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mMsgInput.setText("");
		
	}
	
	private void receiverANewMessage(ChatMessage msg) {
		if(getListView().getAdapter() == null){
			initListView();
		}
		mMsgLists.add(msg);
		mAdapter.notifyDataSetChanged();
		getListView().setSelection(mMsgLists.size()-1);
	}
	
	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setRefreshing(false);
	}

	


	@Override
	public String chooseFile() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setPresenter(Presenter presenter) {
		mPresenter = presenter;
	}

	
	/**
	 * 聊天消息列表Adapter
	 * 直接使用Smack 的 Message 对象为其数据item
	 * @author jianghanghang
	 *
	 */
	class ChatAdapter extends BaseAdapter{
		
		private static final int MAX_MSG_SIZE = 100;
		
		private List<ChatMessage> mMsgDatas;
		
		private LayoutInflater mInflater;
		
		public ChatAdapter(Context context,List<ChatMessage> mMsgDatas) {
			super();
			this.mMsgDatas = mMsgDatas;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mMsgDatas.size();
		}

		@Override
		public ChatMessage getItem(int position) {
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
			ChatMessage msg = getItem(position);
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
	public void updateChatList(List<ChatMessage> msgs) {
		// TODO Auto-generated method stub
		
	}

}
