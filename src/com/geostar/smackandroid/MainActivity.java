package com.geostar.smackandroid;

import java.util.Locale;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.geostar.smackandroid.frag.BaseFragment;
import com.geostar.smackandroid.frag.ContactFragment;
import com.geostar.smackandroid.frag.MessageFragment;
import com.geostar.smackandroid.frag.PubsubFragment;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

public class MainActivity extends FragmentActivity {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private XMPPService mXmppService;
	
	private  XMPPService getXmppService(){
		return mXmppService;
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			for(int i = 0; i<mSectionsPagerAdapter.getCount(); i++){
				((BaseFragment)mSectionsPagerAdapter.getItem(i)).onServiceDisconnected(name);
			}
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPBinder)service).getService();
			for(int i = 0; i<mSectionsPagerAdapter.getCount(); i++){
				((BaseFragment)mSectionsPagerAdapter.getItem(i)).onServiceConnected(name, service);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_after_login);
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		getActionBar().setTitle(mSectionsPagerAdapter.getPageTitle(0));
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						getActionBar().setTitle(mSectionsPagerAdapter.getPageTitle(position));
					}
				});
		
		Intent intent = new Intent(this,XMPPService.class);
		bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private ArrayMap<String,Fragment> mFragments = new ArrayMap<String, Fragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(!mFragments.containsKey(""+ position)){
				mFragments.put(""+position, createFragmentItem(position));
			}
			return mFragments.get(""+position);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.contacts).toUpperCase(l);
			case 1:
				return getString(R.string.chat_and_messages).toUpperCase(l);
			case 2:
				return getString(R.string.pub_and_sub).toUpperCase(l);
			}
			return null;
		}
	}
	
	public Fragment createFragmentItem(int position) {
		BaseFragment frag = null;
		switch (position) {
		case 0:
			frag = new ContactFragment(this);
			break;
		case 1:
			frag = new MessageFragment(this);
			break;
		case 2:
			frag = new PubsubFragment(this);
			break;
		default:
			break;
		}
		return frag;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.after_login, menu);
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
		if(mXmppService != null && !mXmppService.getConnection().isConnected()) {
			mXmppService.reconnect();
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		if(mServiceConnection !=null){
			unbindService(mServiceConnection);
			mServiceConnection =null;
		}
		super.onDestroy();
	}

}
