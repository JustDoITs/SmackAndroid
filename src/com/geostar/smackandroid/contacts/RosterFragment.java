package com.geostar.smackandroid.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

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
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.geostar.smackandroid.R;
import com.geostar.smackandroid.base.BaseFragment;
import com.geostar.smackandroid.contacts.RosterContract.Presenter;
import com.geostar.smackandroid.message.ChatActivity;
import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.service.IChatMsgObserver;
import com.geostar.smackandroid.service.IChatMsgSubject;

public class RosterFragment extends BaseFragment implements RosterContract.View,OnRefreshListener,IChatMsgObserver{
	
	private static final String TAG = "RosterFragment";

	private RosterContract.Presenter mPresenter;
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private List<RosterGroup> mRostersData = new ArrayList<RosterGroup>();
	private ContactAdapter mAdapter;
	
	private Map<String,List<ChatMessage>> mUnReadMessages = new ArrayMap<String, List<ChatMessage>>();
	
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
//		getListView().setOnItemClickListener(mOnContactClick);
		getListView().setOnChildClickListener(onChildClickListener);
		// 左边的箭头
//		getListView().setGroupIndicator(null);
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
	
	class ContactAdapter extends BaseExpandableListAdapter{

		private List<RosterGroup> mData;
		
		public ContactAdapter(List<RosterGroup> mRostersData) {
			super();
			this.mData = mRostersData;
		}

		@Override
		public RosterEntry getChild(int i, int j) {
			return getGroup(i).getEntries().get(j);
		}

		@Override
		public long getChildId(int i, int j) {
			return j;
		}

		@Override
		public View getChildView(int i, int j, boolean flag, View convertView,
				ViewGroup viewgroup) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getActivity(),
						R.layout.listitem_contact, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			RosterEntry entry = getChild(i, j);
			String userName = entry.getUser().split("@")[0];
			holder.user.setText(userName);

			Presence pre = mPresenter.getRoster().getPresence(entry.getUser());
			String nickname = TextUtils.isEmpty(entry.getName()) ? "" : ("("
					+ entry.getName() + ")");
			holder.state.setText(nickname
					+ (pre.getStatus() == null ? "" : pre.getStatus()));

			if (mUnReadMessages.containsKey(userName)) {
				holder.redDot
						.setText(mUnReadMessages.get(userName).size() + "");
				holder.redDot.setVisibility(View.VISIBLE);
			} else {
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
		
		@Override
		public int getChildrenCount(int i) {
			return getGroup(i).getEntryCount();
		}

		@Override
		public RosterGroup getGroup(int i) {
			return mData.get(i);
		}

		@Override
		public int getGroupCount() {
			return mData.size();
		}

		@Override
		public long getGroupId(int i) {
			return i;
		}

		@Override
		public View getGroupView(int i, boolean flag, View view,
				ViewGroup viewgroup) {
			if(view == null){
				view = View.inflate(getContext(), R.layout.group_item, null);
			}
			TextView title = (TextView) view.findViewById(R.id.text);
			title.setText(getGroup(i).getName());
			return view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		/**
		 * 
		 */
		@Override
		public boolean isChildSelectable(int i, int j) {
			return true;
		}
		
	}

	@Override
	public void onRefresh() {
		if(mPresenter != null){
			updateContactList(mPresenter.getRosterGroups());
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	private void goToChatActivity(RosterEntry touser,List<ChatMessage> msgs) {
		ArrayList<String> strMessages = null;
		if(msgs != null){
			strMessages = new ArrayList<String>();
			for(ChatMessage msg: msgs){
				strMessages.add(msg.getBody());
			}
		}
		Intent intent = new Intent(getActivity(),ChatActivity.class);
		intent.putExtra(ChatActivity.KEY_USER,touser.getUser() );
		intent.putStringArrayListExtra(ChatActivity.KEY_MSG, strMessages);
		startActivity(intent);
	}

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {
		
		@Override
		public boolean onChildClick(ExpandableListView expandablelistview,
				View view, int i, int j, long l) {
			RosterEntry touser = mAdapter.getChild(i, j);
			if(touser == null){
				return false;
			}
			
			String userKey = touser.getUser().split("@")[0];
			goToChatActivity(touser,mUnReadMessages.get(userKey));
			if(mUnReadMessages.containsKey(userKey)){
				mUnReadMessages.remove(userKey);
				mAdapter.notifyDataSetChanged();
			}
			return true;
		}
	};
	

	@Override
	public void setPresenter(Presenter presenter) {
		mPresenter = presenter;
	}



	@Override
	public void updateContactList(@NonNull List<RosterGroup> entry) {
		if(mRostersData != null && mAdapter != null){
			showContactGrpList(entry);
		}
	}

	/**
	 * 供后台线程调用
	 */
	@Override
	public void updateContactListFromBackground(final List<RosterGroup> entry) {
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
	public void update(List<ChatMessage> msgs) {
		for(ChatMessage msg: msgs){
			String username = msg.getFrom().split("@")[0];
			if(mUnReadMessages.containsKey(username) ){
				mUnReadMessages.get(username).add(msg);
			}else{
				List<ChatMessage> msgList = new ArrayList<ChatMessage>();
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


	@Override
	public void showContactGrpList(List<RosterGroup> grps) {
		mRostersData = grps;
		mAdapter = new ContactAdapter(mRostersData);
		if( getView() != null){
			getListView().setAdapter(mAdapter);
		}
	}
	
	@Override
	public ExpandableListView getListView() {
		return (ExpandableListView)super.getListView();
	}

}
