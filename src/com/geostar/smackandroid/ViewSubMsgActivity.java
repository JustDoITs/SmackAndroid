package com.geostar.smackandroid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.geostar.smackandroid.adapter.SubMessageAdapter;
import com.geostar.smackandroid.adapter.SubMessageAdapter.ContentProv;
import com.geostar.smackandroid.service.XMPPService;
import com.geostar.smackandroid.service.XMPPService.XMPPBinder;

/**
 * 查看订阅消息
 * @author jianghanghang
 *
 */
public class ViewSubMsgActivity extends ListActivity implements OnRefreshListener {

	protected static final String TAG = "ViewSubMsgActivity";

	/** 订阅节点名称 */
	private String mNodeName;
	
	private XMPPService mXmppService;
	
	private List<Item> mSubMessages = new ArrayList<Item>();
	
	private SubMessageAdapter<Item> adapter ;
	
	private SwipeRefreshLayout mSwipeFresh;
	
	private PubSubManager mPubsubManager ;
	/** 订阅状态，订阅信息  */
	private Subscription mSubcription;
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mXmppService = ((XMPPBinder)service).getService();
			mPubsubManager = new PubSubManager(mXmppService.getXMPPConnection());
			Node node = null;
			try {
				node = mPubsubManager.getNode(mNodeName);
			} catch (NoResponseException | XMPPErrorException
					| NotConnectedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(node == null) return;
			
			node.addItemEventListener(new ItemEventListener <Item>(){

				@Override
				public void handlePublishedItems(ItemPublishEvent<Item> items) {
					// TODO Auto-generated method stub
					if(items!= null && items.getItems().size() > 0){
						Log.d(TAG,"-------------------- handlePublishedItems: size=" +
									items.getItems().size() + "; "+ items.getItems().get(0).toString());
						mSubMessages.addAll(0,items.getItems());
						// 倒序显示，最近的在最上面
						getListView().post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								adapter.notifyDataSetChanged();
							}
						});
					}
				}
				
			});
			try {
				mSubcription = node.subscribe(mXmppService.getXMPPConnection().getUser());
				Log.d(TAG,"-------------------- Subscription:" + mSubcription.toString());
				refreshSubMessages();
//				Subscription	subscribe(String jid, SubscribeForm subForm)
//				The user subscribes to the node using the supplied jid and subscription options.
			} catch (NoResponseException | XMPPErrorException
					| NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if( getIntent() != null ){
			mNodeName = getIntent().getStringExtra("node");
			if(TextUtils.isEmpty(mNodeName)){
				finish();
			}
		}
		setContentView(R.layout.activity_view_sub_msg);
		getActionBar().setTitle(mNodeName);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mSwipeFresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
		mSwipeFresh.setOnRefreshListener(this);
		
		Intent intent = new Intent(this,XMPPService.class);
		bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
		adapter = new SubMessageAdapter<Item>(this,mSubMessages,new ContentProv<Item>() {

			@Override
			public String getText1(Item obj) {
				return obj.getId();
			}

			@Override
			public String getText2(Item obj) {
				return obj.toString();
			}

			@Override
			public OnClickListener getBtnClick(Item obj) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		getListView().setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_sub_msg, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if(id == android.R.id.home){
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		if (id == R.id.action_settings) {
			View diaView = LayoutInflater.from(this).inflate(R.layout.test_pub_sub_input_msg_layout, null);
			final EditText input = (EditText) diaView.findViewById(R.id.et_content_input);
			AlertDialog dialog = new AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Dialog)
					.setTitle("输入发布的消息的内容")
					.setView(diaView)
					.setPositiveButton("发送", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String msg = input.getText().toString();
							if(!TextUtils.isEmpty(msg)){
								sendTestNodeData(msg,mNodeName);
							}else{
								Toast.makeText(ViewSubMsgActivity.this, "输入内容", 2).show();
							}
						}
					})
					.setNegativeButton("取消", null)
					.create();
			dialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		if(mServiceConnection !=null){
			unbindService(mServiceConnection);
			mServiceConnection =null;
		}
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		refreshSubMessages();
		
		mSwipeFresh.setRefreshing(false);
	}


	/** 刷新订阅消息列表 */
	private void refreshSubMessages() {
		// TODO Auto-generated method stub
		if(mXmppService != null && mXmppService.getXMPPConnection() != null 
				&& mXmppService.getXMPPConnection().isConnected() && adapter != null){
			PubSubManager mang = new PubSubManager(mXmppService.getXMPPConnection());
			Node node = null;
			try {
				node = mang.getNode(mNodeName);
				if(node == null) return;
				if(node instanceof LeafNode){
					LeafNode lfn = (LeafNode) node;
					
//					mSubMessages = lfn.getItems("hanlyjiang@geo-hanly");
					Collection<? extends Item> items = lfn.getItems(mSubcription.getId());
					mSubMessages.clear();
					mSubMessages.addAll(items);
					// 倒序显示，最近的在最上面
					Collections.reverse(mSubMessages);
					
					adapter.notifyDataSetChanged();
					// refresh
				}else{
					Toast.makeText(this, "不是子节点", 2).show();
				}
			} catch (NoResponseException | XMPPErrorException
					| NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}else{
			Toast.makeText(this, "未连接，请登录后再试", 5).show();
		}
		
	}

	/**
	 * 发布一条数据到当前节点 
	 * @param msg
	 * @param node
	 */
	private void sendTestNodeData(String msg,String node) {
		if(mPubsubManager == null) return;
		try {
			LeafNode leaf = mPubsubManager.getNode(node);
			leaf.send(new PayloadItem(msg ,
					// TODO: Payload .....
					new SimplePayload("msg", "pubsub:msg:state", "I m ok ")));
			
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			e.printStackTrace();
			Toast.makeText(this, "发生错误：" + e.getMessage(), 5).show();
		}
	}
}
