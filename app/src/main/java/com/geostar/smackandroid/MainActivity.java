package com.geostar.smackandroid;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geostar.smackandroid.base.BaseFragment;
import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.chat.ChatRecordFragment;
import com.geostar.smackandroid.chat.ChatRecordPresenter;
import com.geostar.smackandroid.contacts.RosterFragment;
import com.geostar.smackandroid.contacts.RosterPresenter;
import com.geostar.smackandroid.pubsub.PubSubFragment;
import com.geostar.smackandroid.pubsub.PubSubPresenter;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;
import com.geostar.smackandroid.utils.Utils;

import java.util.Locale;

/**
 * 登录之后进入
 * @author jianghanghang
 *
 */
public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";

	private static final int PAGE_SIZE_3 = 3;
	
	private static final int PAGE_ROSTER = 0;
	private static final int PAGE_CHAT = 1;
	private static final int PAGE_PUBSUB = 2;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private LinearLayout mMenuBar;
	private ViewPager mViewPager;
	
	private int mCurrentPage = -1;
	
	private XMPPService mXmppService;
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPBinder)service).getService();
			Utils.logDebug("OnServiceConnected... at MainActivity");
			// 当连接到服务时，设置Presenter，因为登录后一般是已连接的，可以确保mXmppService.getXMPPConnection() 不为null
			RosterFragment frag = (RosterFragment)getSectionAdapter().getItem(PAGE_ROSTER);
			frag.setChatMsgSubject(mXmppService);
			BasePresenter contactPresenter = new RosterPresenter(mXmppService.getXMPPConnection(), frag);
			Utils.logDebug("RosterFragment : " + frag + "; Adapter:" + getSectionAdapter() + "; RosterPresenter:" + contactPresenter);

			ChatRecordFragment cfrag = (ChatRecordFragment)getSectionAdapter().getItem(PAGE_CHAT);
			frag.setChatMsgSubject(mXmppService);
			ChatRecordPresenter chatPresenter = new ChatRecordPresenter(mXmppService.getXMPPConnection(), cfrag);
			
			PubSubFragment pf = (PubSubFragment)getSectionAdapter().getItem(PAGE_PUBSUB);
			BasePresenter pubSubPresenter = new PubSubPresenter(mXmppService.getXMPPConnection(), pf);

		}
	};

	private int mCurrentMenu;
	private OnClickListener mOnMenuBarClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(mCurrentMenu == v.getId()){
				return;
			}
			changeMenuBarSelectedState(v.getId());
		}
	};

	private synchronized SectionsPagerAdapter getSectionAdapter(){
		if(mSectionsPagerAdapter == null){
			mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		}
		return mSectionsPagerAdapter;
	}
	
	
	private void changeMenuBarSelectedState(int selectId){
		findViewById(mCurrentMenu).setActivated(false);
		findViewById(selectId).setActivated(true);
		mCurrentMenu = selectId;
		
		int page = -1;
		switch (selectId) {
		case R.id.ib_menu_contact:
			page = PAGE_ROSTER;
			break;
		case R.id.ib_menu_msg:
			
			page = PAGE_CHAT;
			break;
		case R.id.ib_menu_pubsub:
			page = PAGE_PUBSUB;
			break;
		default:
			break;
		}
		if(page!= -1){
			mViewPager.setCurrentItem(page);
		}
	}
	
	private int getPageMenuId(int position) {
		int id = -1;
		switch (position) {
		case 0:
			id = R.id.ib_menu_contact;
			break;
		case 1:
			id = R.id.ib_menu_msg;
			break;
		case 2:
			id = R.id.ib_menu_pubsub;
			break;
		default:
			break;
		}
		return id;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.logDebug(TAG, "OnCreate" + " savedInstanceState " + savedInstanceState);
		setContentView(R.layout.activity_after_login);
		final Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(myToolbar);
		if(savedInstanceState != null){
			mCurrentPage = savedInstanceState.getInt("page");
		}

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(getSectionAdapter());

		mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						mCurrentPage = position;
                        ((TextView)myToolbar.findViewById(R.id.tv_title)).setText(mSectionsPagerAdapter.getPageTitle(position));
						changeMenuBarSelectedState(getPageMenuId(position));
					}


				});
		mViewPager.setCurrentItem(mCurrentPage);
		
		Intent intent = new Intent(this,XMPPService.class);
		bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
		
		initMenuBar();


	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void initMenuBar() {
		mMenuBar = (LinearLayout) findViewById(R.id.ll_menu_bar);
		for(int i = 0 ; i<mMenuBar.getChildCount(); i++){
			View v = mMenuBar.getChildAt(i);
			if(v instanceof ImageButton){
				v.setOnClickListener(mOnMenuBarClickListener);
			}
		}
		mCurrentMenu = R.id.ib_menu_contact;
		changeMenuBarSelectedState(mCurrentMenu);
	}

	@Override
	protected void onSaveInstanceState(Bundle arg0) {
		Utils.logDebug(TAG, "onSaveInstanceState");
		arg0.putInt("page", mCurrentPage);
		super.onSaveInstanceState(arg0);
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
				BaseFragment frag  = createFragmentItem(position);
				mFragments.put(""+position, frag);
				Utils.logDebug("Create Fragment : adapter=" + this.toString() + frag.toString());
			}
			return mFragments.get(""+position);
		}

		@Override
		public int getCount() {
			return PAGE_SIZE_3;
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
			frag = new ChatRecordFragment();
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
			exitApp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	private void exitApp() {
		mXmppService.logout();//登出 
		mXmppService.stopSelf();//停止服务
		finish();
	}

}
