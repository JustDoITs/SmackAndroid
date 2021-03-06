package com.geostar.smackandroid;

import org.jivesoftware.smack.AbstractXMPPConnection;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.geostar.smackandroid.config.Configuration;
import com.geostar.smackandroid.service.XMPPLoginCallback;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public class LoginActivity extends Activity {

	private static final int MSG_LOGIN_FAILED = 0x002;
	private static final int MSG_LOGIN_SUCCESS = 0x001;
	protected static final String TAG = null;
	private EditText mUserNameEt,mPasswordEt;
	private Button mSubmitBtn;
	private CheckBox mPasswordRemCb;
	
	private XMPPService mXmppService;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGIN_SUCCESS:
				if(Configuration.getInstance(LoginActivity.this).isRememberPwdEnable()){
					Configuration.getInstance(LoginActivity.this).setSavedUserName(mUserNameEt.getText().toString().trim());
					Configuration.getInstance(LoginActivity.this).setSavedPassword(mPasswordEt.getText().toString().trim());
				}
				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				break;
			case MSG_LOGIN_FAILED:
				Exception e = (Exception) msg.obj;
				Toast.makeText(LoginActivity.this, "登陆失败，请重试：" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
			mXmppService = ((XMPPBinder)service).getService();
			if(mXmppService.getXMPPConnection() != null && mXmppService.getXMPPConnection().isConnected()){
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
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

		startService(new Intent(this,XMPPService.class) );
		bindService(new Intent(this,XMPPService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
		
		
		mUserNameEt = (EditText) findViewById(R.id.et_username);
		mPasswordEt = (EditText) findViewById(R.id.et_password);
		mSubmitBtn = (Button) findViewById(R.id.btn_submit_or_reg);
		mSubmitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mXmppService != null){
					loginOrRegister();
				}
			}
		});
		mPasswordRemCb = (CheckBox) findViewById(R.id.cb_remember_password);
		boolean remPwd = Configuration.getInstance(LoginActivity.this).isRememberPwdEnable();
		mPasswordRemCb.setChecked(remPwd);
		if(remPwd){
			mUserNameEt.setText(Configuration.getInstance(LoginActivity.this).getSavedUserName());
			mPasswordEt.setText(Configuration.getInstance(LoginActivity.this).getSavedPassword());
		}
		mPasswordRemCb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Configuration.getInstance(LoginActivity.this).setPasswordRememberEnable(mPasswordRemCb.isChecked());
			}
		});
	}


	
	private void loginOrRegister() {
		AbstractXMPPConnection conn = mXmppService.getXMPPConnection();
		String name = mUserNameEt.getText().toString();
		String passwd = mPasswordEt.getText().toString();
		XMPPLoginCallback callback = new XMPPLoginCallback() {
			
			@Override
			public void onLoginSuccess() {
				mHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
			}
			
			@Override
			public void onLoginFailed(Exception e) {
				Message msg = new Message();
				msg.what = MSG_LOGIN_FAILED;
				msg.obj = e ;
				mHandler.sendMessage(msg);
			}
		};
		mXmppService.login(name, passwd, callback);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
