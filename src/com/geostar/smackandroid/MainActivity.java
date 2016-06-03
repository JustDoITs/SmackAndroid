package com.geostar.smackandroid;

import java.util.List;
import java.util.Locale;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.chat.ChatFragment;
import com.geostar.smackandroid.contacts.RosterFragment;
import com.geostar.smackandroid.contacts.RosterContract;
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

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private XMPPService mXmppService;
	
	private int mCurrentPage = -1;
	
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
			for(int i = 0; i<mSectionsPagerAdapter.getCount(); i++){
				((BaseFragment)mSectionsPagerAdapter.getItem(i)).onServiceConnected(mXmppService.getXMPPConnection());
			}
			RosterFragment frag = (RosterFragment)mSectionsPagerAdapter.getItem(0);
			frag.setChatMsgSubject(mXmppService);
			BasePresenter contactPresenter = new RosterPresenter(
					mXmppService.getXMPPConnection(), frag);
			contactPresenter.onServiceConnected(mXmppService.getXMPPConnection());
			// 注册消息监听
			
			PubSubFragment pf = (PubSubFragment)mSectionsPagerAdapter.getItem(2);
			BasePresenter pubSubPresenter = new PubSubPresenter(
					mXmppService.getXMPPConnection(), pf);
			pubSubPresenter.onServiceConnected(mXmppService.getXMPPConnection());
			
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
		// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(tab.getPosition());

			}

			@Override
			public void onTabUnselected(Tab tab,
					android.app.FragmentTransaction fragmenttransaction) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabReselected(Tab tab,
					android.app.FragmentTransaction fragmenttransaction) {
				// TODO Auto-generated method stub

			}
		};

		// Add 3 tabs, specifying the tab's text and TabListener
		for (int i = 0; i < 3; i++) {
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
	
	private BaseFragment createFragmentItem(int position) {
		BaseFragment frag = null;
		AbstractXMPPConnection conn = mXmppService==null?null:mXmppService.getXMPPConnection();
		switch (position) {
		case 0:
			frag = new RosterFragment(conn);
			break;
		case 1:
			frag = new ChatFragment(conn);
			break;
		case 2:
			frag = new PubSubFragment(conn);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.after_login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_switch_account) {
			mXmppService.logout();
			startActivity(new Intent(this,LoginActivity.class));
			finish();
			return true;
		}
		if (id == R.id.action_exit) {
			mXmppService.logout();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
