package com.geostar.smackandroid;

import java.util.List;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import com.geostar.smackandroid.service.XMPPService;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends ListActivity {

	private static final String TAG = null;
	
	private List<DiscoverItems.Item> mDisItems;
	private PubSubManager pubSubMgr;
	private List<Affiliation> mPubs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
//		mSendTestPubBtn = (Button) v.findViewById(R.id.btn_send_test_subscribe);
//		mSendTestPubBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				sendTestPubMsg();
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
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
	

	private void loadAllPubs() {
		// TODO Auto-generated method stub
//		if(!checkConnection()) {
//			return;
//		}
		
		if(pubSubMgr == null){
			pubSubMgr = new PubSubManager(getXMPPService().getXMPPConnection());
		}
		DiscoverItems disItems = null;
		try {
			// 获取Root 节点下所有子节点
			disItems = pubSubMgr.discoverNodes("/");
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			e.printStackTrace();
		}
		if(disItems != null){
			mDisItems = disItems.getItems();
			DisPubsAdapter adapter = new DisPubsAdapter(mDisItems);
			getListView().setAdapter(adapter);
		}
		
	}

	class DisPubsAdapter extends BaseAdapter {
		
		private List<DiscoverItems.Item> datas;
		
		public DisPubsAdapter(
				List<org.jivesoftware.smackx.disco.packet.DiscoverItems.Item> datas) {
			super();
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public DiscoverItems.Item getItem(int position) {
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(convertView !=  null){
				convertView = LayoutInflater.from(TestActivity.this).inflate(R.layout.pubs_discover_all_item, null);
				holder = new Holder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			DiscoverItems.Item item = getItem(position);
			holder.title.setText(item.getName());
			holder.desc.setText(item.getNode());
			
			return convertView;
		}
		
		
		class Holder {
			TextView title;
			TextView desc;
			Button addSubs;
			public Holder(View convertView) {
				this.title = (TextView) convertView.findViewById(R.id.tv_nodename);
				this.desc = (TextView) convertView.findViewById(R.id.tv_desc);
				this.addSubs = (Button) convertView.findViewById(R.id.btn_subcrible);
			}
		}
	}
	
	private void testInfomation() {
		if(getXMPPService() == null){
			return;
		}
		if(getXMPPService().getXMPPConnection() == null ){
			Log.e(TAG,"Service is not connected");
			return;
		}
		if(pubSubMgr == null){
			pubSubMgr = new PubSubManager(getXMPPService().getXMPPConnection());
		}
		try {
			mPubs =  pubSubMgr.getAffiliations();
			for(Affiliation a : mPubs){
				Log.d(TAG,"Affiliation" + a.getNodeId());
			}
			
			List<Subscription> subs = pubSubMgr.getSubscriptions();
			for(Subscription s : subs){
				Log.d(TAG,"Subscription:" + s.getJid());
			}
			
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Get the node
		LeafNode node = null;
		try {
			node = pubSubMgr.getNode("testNode");
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(node != null){
			
			node.addItemEventListener(new ItemEventListener<Item>() {
	
				@Override
				public void handlePublishedItems(ItemPublishEvent<Item> event) {
					// TODO Auto-generated method stub
					Log.d(TAG,"Get handlePublishedItems:" + event.getItems());
				}
			});
//			try {
//				node.subscribe(getXMPPService().getXMPPConnection().getUser());
//			} catch (NoResponseException | XMPPErrorException
//					| NotConnectedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		DiscoverItems items = null;
		try {
			items = pubSubMgr.discoverNodes("");
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(items != null){
			for(org.jivesoftware.smackx.disco.packet.DiscoverItems.Item item :items.getItems()){
				Log.d(TAG,"Node:" + item.getNode() + "; id:" +item.getEntityID() 
						+"; Name:" + item.getName() + "; Action:" + item.getAction());
			}
		}
	}

	private XMPPService getXMPPService() {
		// TODO Auto-generated method stub
		return null;
	}
}
