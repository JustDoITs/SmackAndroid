package com.geostar.smackandroid.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.chat.ChatActivity;
import com.geostar.smackandroid.contacts.RosterContract.Presenter;
import com.geostar.smackandroid.service.IChatMsgObserver;
import com.geostar.smackandroid.service.IChatMsgSubject;

public class RosterFragment extends BaseFragment implements RosterContract.View,OnRefreshListener,IChatMsgObserver{
	
	private static final String TAG = "RosterFragment";

	private RosterContract.Presenter mPresenter;
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private List<RosterEntry> mRostersData = new ArrayList<RosterEntry>();
	private ContactAdapter mAdapter;
	
	private Map<String,List<Message>> mUnReadMessages = new ArrayMap<String, List<Message>>();
	
	private IChatMsgSubject mChatMsgSubject;
	
	
	@Override
	public void setChatMsgSubject(IChatMsgSubject chatMsgSubject) {
		this.mChatMsgSubject = chatMsgSubject;
		mChatMsgSubject.registerChatMessageObserver(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_contacts, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return v;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		getListView().setOnItemClickListener(mOnContactClick);
		super.onViewCreated(view, savedInstanceState);
	}


	
	@Override
	public void onResume() {
		if(mPresenter != null){
			mPresenter.start();
//			updateContactList(mPresenter.getAllRosterEntrys());
		}
		if(mChatMsgSubject != null){
			mChatMsgSubject.registerChatMessageObserver(this);
		}
		super.onResume();
	}
	
	@Override
	public void onStop() {
		if(mChatMsgSubject != null){
			mChatMsgSubject.unregisterChatMessageObserver(this);
		}
		super.onStop();
	}
	
	class ContactAdapter extends BaseAdapter{

		private List<RosterEntry> mData;
		
		public ContactAdapter(List<RosterEntry> data) {
			super();
			this.mData = data;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public RosterEntry getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(parent.getContext(), R.layout.listitem_contact, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			RosterEntry entry = getItem(position);
			String userName = entry.getUser().split("@")[0];
			holder.user.setText(userName);
			
			Presence pre = mPresenter.getRoster().getPresence(entry.getUser());
			String nickname = TextUtils.isEmpty(entry.getName())?"":("("+ entry.getName()+")" );
			holder.state.setText(nickname + (pre.getStatus()==null?"":pre.getStatus()));
			
			if(mUnReadMessages.containsKey(userName)){
				holder.redDot.setText(mUnReadMessages.get(userName).size() + "");
				holder.redDot.setVisibility(View.VISIBLE);
			}else{
				holder.redDot.setVisibility(View.GONE);
			}
			return convertView;
		}
		
		class ViewHolder {
			TextView user;
			TextView state;
			Button redDot;
			
			ViewHolder(View convertView){
				user = (TextView) convertView.findViewById(R.id.tv_username);
				state = (TextView) convertView.findViewById(R.id.tv_presence);
				redDot = (Button) convertView.findViewById(R.id.btn_redot);
			}
		}
		
	}

	@Override
	public void onRefresh() {
		if(mPresenter != null){
			updateContactList(mPresenter.getAllRosterEntrys());
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	private void goToChatActivity(RosterEntry touser,List<Message> msgs) {
		ArrayList<String> strMessages = null;
		if(msgs != null){
			strMessages = new ArrayList<String>();
			for(Message msg: msgs){
				strMessages.add(msg.getBody());
			}
		}
		Intent intent = new Intent(getActivity(),ChatActivity.class);
		intent.putExtra(ChatActivity.KEY_USER,touser.getUser() );
		intent.putStringArrayListExtra(ChatActivity.KEY_MSG, strMessages);
		startActivity(intent);
	}

	private OnItemClickListener mOnContactClick = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			RosterEntry touser = mRostersData.get(position);
			if(touser == null){
				return ;
			}
			
			String userKey = touser.getUser().split("@")[0];
			goToChatActivity(touser,mUnReadMessages.get(userKey));
			if(mUnReadMessages.containsKey(userKey)){
				mUnReadMessages.remove(userKey);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void setPresenter(Presenter presenter) {
		mPresenter = presenter;
	}


	@Override
	public void showContactList(@NonNull List<RosterEntry> contacts) {
		
		mRostersData = contacts;
		mAdapter = new ContactAdapter(mRostersData);
		if( getView() != null){
			getListView().setAdapter(mAdapter);
		}
	}


	@Override
	public void updateContactList(@NonNull List<RosterEntry> entry) {
		if(mRostersData != null && mAdapter != null){
//			mRostersData.clear();
//			mRostersData.addAll(entry);
//			mAdapter.notifyDataSetChanged();
//			getListView().invalidate();
//		}else{
			showContactList(entry);
		}
	}

	/**
	 * 供后台线程调用
	 */
	@Override
	public void updateContactListFromBackground(final List<RosterEntry> entry) {
		if(getView() == null){
			Log.e(TAG,"View 还没有初始化");
			return;
		}
		getView().post(new Runnable() {
			
			@Override
			public void run() {
				updateContactList(entry);
			}
		});
	}

	/**
	 * 接收到新的消息
	 */
	@Override
	public void update(List<Message> msgs) {
		for(Message msg: msgs){
			String username = msg.getFrom().split("@")[0];
			if(mUnReadMessages.containsKey(username) ){
				mUnReadMessages.get(username).add(msg);
			}else{
				List<Message> msgList = new ArrayList<Message>();
				msgList.add(msg);
				mUnReadMessages.put(username, msgList);
			}
		}
		if(getView()!= null){
			getView().post(new Runnable() {
				
				@Override
				public void run() {
					mAdapter.notifyDataSetChanged();
				}
			});
		}
	}

}
