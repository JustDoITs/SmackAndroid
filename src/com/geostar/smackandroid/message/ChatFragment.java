package com.geostar.smackandroid.message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.geostar.smackandroid.utils.FileUtils;
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
				// 选取文件发送
				Utils.showFileChooserFromFragment(ChatFragment.this, FILE_SELECT_CODE);
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
		
		mMsgLists.clear();
		// 因为getAllMessages从greenDao 返回，List 不支持addAll，所以不用“=”，而是直接add
		mMsgLists.addAll(mPresenter.getAllMessages());
//		if(mAdapter == null){
			mAdapter = new ChatAdapter(getActivity(), mMsgLists);
//		}
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

		private long mLastTime;
		
		// 10分钟
		private final long defaultTimeDis = 1000*60*10;
		
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
			
			TextView text ;
			ImageView msgImage;
			ChatMessage msg = getItem(position);
			if (msg.getTo().contains(mPresenter.getCurrentUser())) {
				convertView.findViewById(R.id.chat_msg_mine).setVisibility(
						View.GONE);
				convertView.findViewById(R.id.chat_msg_others).setVisibility(
						View.VISIBLE);
				text = holder.msgOther;
				msgImage = holder.fileMsgIconOther;
				
			} else {// 自己发出的消息
				convertView.findViewById(R.id.chat_msg_mine).setVisibility(
						View.VISIBLE);
				convertView.findViewById(R.id.chat_msg_others).setVisibility(
						View.GONE);
				text = holder.msgMine;
				msgImage = holder.fileMsgIconMine;
			}
			if(msg.getMsgType().equals(ChatMessage.Type.text)){
				text.setText(msg.getBody());
				text.setVisibility(View.VISIBLE);
				msgImage.setVisibility(View.GONE);
			}else{// 文件
				text.setVisibility(View.GONE);
				msgImage.setVisibility(View.VISIBLE);
				msgImage.setOnClickListener(createOnClickListener(msg));
				msgImage.setImageResource(getImageByType(msg.getMsgType()));
			}
			
			// 设置时间
			if( Math.abs( mLastTime - msg.getTime() ) > defaultTimeDis ){
				holder.time.setText(Utils.formatTimeDefault(msg.getTime()));
				holder.time.setVisibility(View.VISIBLE);
				mLastTime = msg.getTime();
			}else{
				holder.time.setText("");
				holder.time.setVisibility(View.INVISIBLE);
			}
			
			return convertView;
		}


		private int getImageByType(ChatMessage.Type type) {
			if(type.equals(ChatMessage.Type.image)){
				return R.drawable.chat_image;
			}else if(type.equals(ChatMessage.Type.voice)){
				return R.drawable.chat_voice;
			}else if(type.equals(ChatMessage.Type.video)){
				return R.drawable.chat_video;
			}/*else if(type.equals(ChatMessage.Type.file)){
				return R.drawable.chat_file;
			}*/
			return R.drawable.chat_file;
		}


		class ViewHolder {

			ImageView iconOther, iconMine;
			TextView msgOther, msgMine;
			TextView time;
			ImageView fileMsgIconMine,fileMsgIconOther;

			public ViewHolder(View convertView) {
				iconMine = (ImageView) convertView.findViewById(
						R.id.chat_msg_mine).findViewById(R.id.iv_icon);
				msgMine = (TextView) convertView.findViewById(
						R.id.chat_msg_mine).findViewById(R.id.tv_msg_content);

				iconOther = (ImageView) convertView.findViewById(
						R.id.chat_msg_others).findViewById(R.id.iv_icon);
				msgOther = (TextView) convertView.findViewById(
						R.id.chat_msg_others).findViewById(R.id.tv_msg_content);
				
				fileMsgIconMine = (ImageView) convertView.findViewById(
						R.id.chat_msg_mine).findViewById(R.id.iv_msg_icon);
				
				fileMsgIconOther = (ImageView) convertView.findViewById(
						R.id.chat_msg_others).findViewById(R.id.iv_msg_icon);
			
				time = (TextView) convertView.findViewById(R.id.time);
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

	/** 点击聊天中的文件  */
	public OnClickListener createOnClickListener(final ChatMessage msg) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String filePath = msg.getBody();
				File fileToview = new File(filePath);
				String mimeType = FileUtils.getMimeType(fileToview);
				
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(fileToview), mimeType);
				startActivity(intent);
			}
		};
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

	@Override
	public void refreshMessageList() {
		onRefresh();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 文件选择
		if( requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK ){
			String filepath = null;
//			if(data.getData().getScheme().contains("file")){
//				File f = new File(data.getData().getPath());
//				filepath = f.getAbsolutePath();
//			}else{
			filepath = FileUtils.getPath(getContext(),data.getData());
//			}
			Utils.logDebug("DataUri:" + data.getDataString() + ";file:" + filepath);
			mPresenter.sendFile(filepath);
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

}
