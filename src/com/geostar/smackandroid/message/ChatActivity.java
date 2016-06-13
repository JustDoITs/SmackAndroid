package com.geostar.smackandroid.message;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.geostar.smackandroid.R;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;
import com.geostar.smackandroid.utils.Utils;

/**
 * 聊天
 * @author jianghanghang
 *
 */
public class ChatActivity extends Activity {

	protected static final String TAG = "ChatActivity";
	
	private ChatFragment mChatFragment;
	private ChatPresenter mChatPresenter;
	
	private XMPPService mXmppService;
	
	/** 进入时消息 */
	private List<String> firstInMsg;
	

	private String mChatOjb;
	
	
	public static final String KEY_MSG = "message";
	public static final String KEY_USER = "user";
	
	private  XMPPService getXmppService(){
		return mXmppService;
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Utils.logDebug("onServiceDisconnected = ChatActivity");
			mChatPresenter.destory();
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPBinder)service).getService();
//			registerMessageListener();
			mChatPresenter = new ChatPresenter(mXmppService.getXMPPConnection(), getChatView());
			
		}
	};


	private ChatContract.View getChatView(){
		if(mChatFragment == null){
			mChatFragment = new ChatFragment();
		}
		return mChatFragment;
	};


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		mChatFragment = new ChatFragment();
		
		
//		String inMsg = null;
//		if(mChatOjb == null){
//			Intent args = getIntent();
//			if(args == null){
//				finish();
//			}
//			
//			mChatOjb = args.getStringExtra(KEY_USER);
//			firstInMsg = args.getStringArrayListExtra(KEY_MSG);
//			
//			if(TextUtils.isEmpty(mChatOjb)){
//				finish();
//			}
//		}
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
		if(id == android.R.id.home){
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onResume() {
		if(getXmppService() != null && getXmppService() !=null 
				&& !getXmppService().getXMPPConnection().isConnected()){
			getXmppService().reconnect();
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if(mChatPresenter != null){
			mChatPresenter.destory();
		}
		if(mXmppService != null && mServiceConnection != null){
			unbindService(mServiceConnection);
		}
		super.onDestroy();
	}


}
