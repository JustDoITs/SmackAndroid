package com.geostar.smackandroid;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public class ChatActivity extends Activity {

	private XMPPService mXmppService;
	
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
			
		}
	};
	
	private String mChatOjb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(mChatOjb == null){
			Intent args = getIntent();
			if(args == null){
				finish();
			}
			mChatOjb = args.getStringExtra("user");
			if(TextUtils.isEmpty(mChatOjb)){
				finish();
			}
		}
		
		setContentView(R.layout.chat_room_layout);
		bindService(new Intent(this,XMPPService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
	
		getActionBar().setTitle(mChatOjb);
		loadChatView();
	}

	
	
	
	private void loadChatView() {
		// TODO Auto-generated method stub
		
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(mXmppService != null && mServiceConnection != null){
			unbindService(mServiceConnection);
		}
		super.onDestroy();
	}
}
