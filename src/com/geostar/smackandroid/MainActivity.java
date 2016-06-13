package com.geostar.smackandroid;

import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.chat.ChatFragment;
import com.geostar.smackandroid.chat.ChatPresenter;
import com.geostar.smackandroid.contacts.RosterFragment;
import com.geostar.smackandroid.contacts.RosterPresenter;
import com.geostar.smackandroid.pubsub.PubSubFragment;
import com.geostar.smackandroid.pubsub.PubSubPresenter;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;
import com.geostar.smackandroid.utils.Utils;

/**
 * 登录之后进入
 * @author jianghanghang
 *
 */
public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";

	private static final int PAGE_NUM_3 = 3;
	
	private static final int PAGE_ROSTER = 0;
	private static final int PAGE_CHAT = 1;
	private static final int PAGE_PUBSUB = 2;

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private int mCurrentPage = -1;
	
	private XMPPService mXmppService;
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
//			for(int i = 0; i<mSectionsPagerAdapter.getCount(); i++){
//				((BaseFragment)mSectionsPagerAdapter.getItem(i)).onServiceDisconnected(name);
//			}
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPBinder)service).getService();
//			for(int i = 0; i<mSectionsPagerAdapter.getCount(); i++){
//				((BaseFragment)mSectionsPagerAdapter.getItem(i)).onServiceConnected(mXmppService.getXMPPConnection());
//			}
			// 当连接到服务时，设置Presenter，因为登录后一般是已连接的，可以确保mXmppService.getXMPPConnection() 不为null
			RosterFragment frag = (RosterFragment)mSectionsPagerAdapter.getItem(PAGE_ROSTER);
			frag.setChatMsgSubject(mXmppService);
			BasePresenter contactPresenter = new RosterPresenter(mXmppService.getXMPPConnection(), frag);
//			contactPresenter.onServiceConnected(mXmppService.getXMPPConnection());
			
			ChatFragment cfrag = (ChatFragment)mSectionsPagerAdapter.getItem(PAGE_CHAT);
			frag.setChatMsgSubject(mXmppService);
			ChatPresenter chatPresenter = new ChatPresenter(mXmppService.getXMPPConnection(), cfrag);
			
			PubSubFragment pf = (PubSubFragment)mSectionsPagerAdapter.getItem(PAGE_PUBSUB);
			BasePresenter pubSubPresenter = new PubSubPresenter(mXmppService.getXMPPConnection(), pf);
//			pubSubPresenter.onServiceConnected(mXmppService.getXMPPConnection());
			
			getActionBar().setTitle(mXmppService.getXMPPConnection().getUser().split("@")[0]);
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.logDebug(TAG, "OnCreate" + " savedInstanceState " + savedInstanceState);
		setContentView(R.layout.activity_after_login);
		if(savedInstanceState != null){
			mCurrentPage = savedInstanceState.getInt("page");
		}
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		getActionBar().setTitle(getResources().getString(R.string.app_name));
		initActionBar();
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mCurrentPage = position;
//						getActionBar().setTitle(mSectionsPagerAdapter.getPageTitle(position));
						getActionBar().selectTab(getActionBar().getTabAt(position));
					}
				});
		mViewPager.setCurrentItem(mCurrentPage);
		
		Intent intent = new Intent(this,XMPPService.class);
		bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
	}

	@Override
	protected void onSaveInstanceState(Bundle arg0) {
		Utils.logDebug(TAG, "onSaveInstanceState");
		arg0.putInt("page", mCurrentPage);
		super.onSaveInstanceState(arg0);
	}
	
	private void initActionBar() {
		// TODO Auto-generated method stub
		final ActionBar actionBar = getActionBar();

		// Specify that tabs should be displayed in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(Tab tab,
					android.app.FragmentTransaction fragmenttransaction) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab,
					android.app.FragmentTransaction fragmenttransaction) {}

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction fragmenttransaction) {}
		};

		// Add 3 tabs, specifying the tab's text and TabListener
		for (int i = 0; i < PAGE_NUM_3; i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i).toString())
					.setTabListener(tabListener));
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private ArrayMap<String,BaseFragment> mFragments = new ArrayMap<String, BaseFragment>();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public BaseFragment getItem(int position) {
			if(!mFragments.containsKey(""+ position)){
				mFragments.put(""+position, createFragmentItem(position));
			}
			return mFragments.get(""+position);
		}

		@Override
		public int getCount() {
			return PAGE_NUM_3;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case PAGE_ROSTER:
				return getString(R.string.contacts).toUpperCase(l);
			case PAGE_CHAT:
				return getString(R.string.chat_and_messages).toUpperCase(l);
			case PAGE_PUBSUB:
				return getString(R.string.pub_and_sub).toUpperCase(l);
			}
			return null;
		}
	}
	
	private BaseFragment createFragmentItem(int position) {
		BaseFragment frag = null;
		switch (position) {
		case 0:
			frag = new RosterFragment();
			break;
		case 1:
			frag = new ChatFragment();
			break;
		case 2:
			frag = new PubSubFragment();
			break;
		default:
			break;
		}
		return frag;
	}
	

	@Override
	protected void onResume() {
		Utils.logDebug(TAG, "onResume");
		if(mXmppService != null && !mXmppService.getXMPPConnection().isConnected()) {
			mXmppService.reconnect();
		}
		if(mCurrentPage != -1){
			mViewPager.setCurrentItem(mCurrentPage,true);
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Utils.logDebug(TAG, "onDestroy");
		if(mServiceConnection !=null){
			unbindService(mServiceConnection);
			mServiceConnection =null;
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.after_login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch (mViewPager.getCurrentItem()) {
		case 0:
		case 1:
			menu.findItem(R.id.action_switch_account).setVisible(true);
			menu.findItem(R.id.action_exit).setVisible(true);
			break;
		case 2:
			menu.findItem(R.id.action_switch_account).setVisible(false);
			menu.findItem(R.id.action_exit).setVisible(false);
			break;
		default:
			break;
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_switch_account) {
			mXmppService.logout();
			startActivity(new Intent(this,LoginActivity.class));
			finish();
			return true;
		}
		if (id == R.id.action_exit) { // 退出应用
			mXmppService.logout();//登出 
			mXmppService.stopSelf();//停止服务
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
