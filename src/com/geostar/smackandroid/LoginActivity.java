package com.geostar.smackandroid;

import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geostar.smackandroid.manager.login.ILoginManager;
import com.geostar.smackandroid.manager.login.ILoginManager.OnLoginCallback;
import com.geostar.smackandroid.manager.login.LoginManagerImpl;
import com.geostar.smackandroid.service.NewXMPPService;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;
import com.geostar.smackandroid.xmpp.XMPPLoginCallback;

public class LoginActivity extends Activity {

	private static final int MSG_LOGIN_FAILED = 0x002;
	private static final int MSG_LOGIN_SUCCESS = 0x001;
	protected static final String TAG = null;
	private EditText mUserNameEt,mPasswordEt;
	private Button mSubmitBtn;
	
	private NewXMPPService mXmppService;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGIN_SUCCESS:
				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this,AfterLoginActivity.class);
				startActivity(intent);
				finish();
				break;
			case MSG_LOGIN_FAILED:
				Toast.makeText(LoginActivity.this, "登陆失败，继续潜水", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
	private ServiceConnection mServiceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mXmppService = ((NewXMPPService.XMPPBinder)service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		startService(new Intent(this,NewXMPPService.class) );
		bindService(new Intent(this,NewXMPPService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
		
		
		mUserNameEt = (EditText) findViewById(R.id.et_username);
		mPasswordEt = (EditText) findViewById(R.id.et_password);
		mSubmitBtn = (Button) findViewById(R.id.btn_submit_or_reg);
		mSubmitBtn.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) {
				if(mXmppService != null){
					try {
						loginOrRegister();
					} catch (SmackException | IOException | XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
	}


	private void loginOrRegister() throws SmackException, IOException, XMPPException {
		AbstractXMPPConnection conn = mXmppService.getXMPPConnection();
		String name = mUserNameEt.getText().toString();
		String passwd = mPasswordEt.getText().toString();
		
		mXmppService.connectToXMPPServerUserDefaultSet(name,passwd);
		
		ILoginManager loginManager = (ILoginManager) mXmppService.getManager(NewXMPPService.LOGIN_MANAGER);
		
		if(loginManager == null){
			System.out.println("------------------ not ready -------------");
			return;
		}
		OnLoginCallback callback = new OnLoginCallback() {
			
			@Override
			public void onSuccess() {
				mHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
			}
			
			@Override
			public void onFailed() {
				mHandler.sendEmptyMessage(MSG_LOGIN_FAILED);
			}
		};
		loginManager.login(name, passwd, callback);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
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
		if(mServiceConnection != null){
			unbindService(mServiceConnection);
			mServiceConnection = null;
		}
		super.onDestroy();
	}
}
