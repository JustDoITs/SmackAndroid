package com.geostar.smackandroid.message;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.geostar.smackandroid.R;
import com.geostar.smackandroid.base.BaseFragment;
import com.geostar.smackandroid.message.ChatContract.Presenter;
import com.geostar.smackandroid.message.data.dao.ChatMessage;
import com.geostar.smackandroid.utils.Utils;

public class ChatFragment extends BaseFragment implements OnRefreshListener,
		ChatContract.View {

	private ChatAdapter mAdapter;

	private List<ChatMessage> mMsgLists = new ArrayList<ChatMessage>();

	private EditText mMsgInput;
	private Button mMsgSendBtn;
	private ImageButton mFileChooseBtn;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private ChatContract.Presenter mPresenter;

	private static final int FILE_SELECT_CODE = 0x009;

	/** 聊天对象 */
	private String mChatOjb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_chat_room, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v
				.findViewById(R.id.swiperefresh);
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
		super.onActivityCreated(savedInstanceState);

		if (mChatOjb != null) {
			getActivity().setTitle(mChatOjb.split("@")[0]);
		}

		initListView();

	}

	/** 刷新List */
	protected void initListView() {
		if (mPresenter == null) {
			Utils.logDebug("Connect is not ready!!!");
			return;
		} else {
			Utils.logDebug("mPresent is ok...");
		}
		mMsgLists = mPresenter.getAllMessages();
		mAdapter = new ChatAdapter(getActivity(), mMsgLists);
		// if(firstInMsg != null){
		// for(String msgContent : firstInMsg){
		// mMsgLists.add(new Message("me", msgContent));
		// }
		// }
		getListView().setItemsCanFocus(true);
		getListView().setAdapter(mAdapter);
	}

	private void receiverANewMessage(ChatMessage msg) {
		if (getListView().getAdapter() == null) {
			initListView();
		}
		mMsgLists.add(msg);
		mAdapter.notifyDataSetChanged();
		getListView().setSelection(mMsgLists.size() - 1);
	}

	@Override
	public void onRefresh() {
		initListView();
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
	 * 聊天消息列表Adapter 直接使用Smack 的 Message 对象为其数据item
	 * 
	 * @author jianghanghang
	 *
	 */
	class ChatAdapter extends BaseAdapter {

		private static final int MAX_MSG_SIZE = 100;

		private List<ChatMessage> mMsgDatas;

		private LayoutInflater mInflater;

		public ChatAdapter(Context context, List<ChatMessage> mMsgDatas) {
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
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.listitem_chat_msg_all_com, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ChatMessage msg = getItem(position);
			if (msg.getTo().contains(mPresenter.getCurrentUser())) {
				convertView.findViewById(R.id.chat_msg_mine).setVisibility(
						View.GONE);
				convertView.findViewById(R.id.chat_msg_others).setVisibility(
						View.VISIBLE);
				holder.msgOther.setText(msg.getBody());
			} else {// 自己发出的消息
				convertView.findViewById(R.id.chat_msg_mine).setVisibility(
						View.VISIBLE);
				convertView.findViewById(R.id.chat_msg_others).setVisibility(
						View.GONE);
				holder.msgMine.setText(msg.getBody());
			}
			return convertView;
		}

		
		
//		@Override
//		public boolean isEnabled(int position) {
//			return false;
//		}


		class ViewHolder {

			ImageView iconOther, iconMine;
			TextView msgOther, msgMine;

			public ViewHolder(View convertView) {
				iconMine = (ImageView) convertView.findViewById(
						R.id.chat_msg_mine).findViewById(R.id.iv_icon);
				msgMine = (TextView) convertView.findViewById(
						R.id.chat_msg_mine).findViewById(R.id.tv_msg_content);

				iconOther = (ImageView) convertView.findViewById(
						R.id.chat_msg_others).findViewById(R.id.iv_icon);
				msgOther = (TextView) convertView.findViewById(
						R.id.chat_msg_others).findViewById(R.id.tv_msg_content);
			}
		}
	}

	// private void registerMessageListener() {
	// // TODO Auto-generated method stub
	// StanzaListener Messagelistener = new StanzaListener() {
	//
	// };
	// mXmppService.getXMPPConnection().addAsyncStanzaListener(Messagelistener,
	// MessageWithBodiesFilter.INSTANCE);
	// }

	/**
	 * 发送消息
	 */
	protected void sendMessage() {
		String msg = mMsgInput.getText().toString();
		if (TextUtils.isEmpty(msg)) {
			Toast.makeText(getActivity(), "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}

		mPresenter.sendMessage(msg);

		mMsgInput.setText("");

	}

	@Override
	public void postNewMessageFromNoneUiThread(final List<ChatMessage> newMsgs) {
		if (getView() != null) {
			getView().post(new Runnable() {
				@Override
				public void run() {
					postNewMessage(newMsgs);
				}
			});
		}
	}

	@Override
	public void postNewMessage(List<ChatMessage> newMsgs) {
		mMsgLists.addAll(newMsgs);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
			getListView().invalidate();
		} else {
			initListView();
			mAdapter.notifyDataSetChanged();
		}
		getListView().setSelection(mMsgLists.size() - 1);
	}

}
