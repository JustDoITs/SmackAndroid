package com.geostar.smackandroid.frag;

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

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.adapter.SubsAdapter;
import com.geostar.smackandroid.adapter.SubsAdapter.ContentProv;

public class PubsubFragment extends BaseFragment implements OnRefreshListener{

	private static final String TAG = "PubsubFragment";
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private Button mSendTestPubBtn;
	private PubSubManager pubSubMgr;
	
	/** */
	private List<Affiliation> mPubs;
	
	public PubsubFragment(MainActivity activity) {
		super(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pub_sub_layout, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSendTestPubBtn = (Button) v.findViewById(R.id.btn_send_test_subscribe);
		mSendTestPubBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sendTestPubMsg();
			}
		});
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		
		try {
			loadMySubs();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		testInfomation();

//		pubSubMgr.get
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

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		// 加载可用订阅列表
//		loadAllPubs();
		
	}

	
	private void loadMySubs() throws NoResponseException, XMPPErrorException, NotConnectedException {
		// TODO Auto-generated method stub
		if(!checkConnection()){
			return;
		}
		if(pubSubMgr == null){
			pubSubMgr = new PubSubManager(getXMPPService().getXMPPConnection());
		}
		List<Affiliation> items = pubSubMgr.getAffiliations();
		SubsAdapter<Affiliation> adapter = new SubsAdapter<Affiliation>(getContext(), items, new ContentProv<Affiliation>() {

			@Override
			public String getText1(Affiliation obj) {
				return obj.getNodeId();
			}

			@Override
			public String getText2(Affiliation obj) {
				return obj.getType().toString();
			}

			@Override
			public OnClickListener getBtnClick(Affiliation obj) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		getListView().setAdapter(adapter);
	}


	private List<DiscoverItems.Item> mDisItems;
	private void loadAllPubs() {
		// TODO Auto-generated method stub
		if(!checkConnection()) {
			return;
		}
		
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
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.pubs_discover_all_item, null);
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

	@Override
	public void onServiceDisconnected(ComponentName name) {
		super.onServiceDisconnected(name);
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
//		getListView().postDelayed(null, 1000);
		try {
			loadMySubs();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	/**
	 * 发送测试消息
	 */
	private void sendTestPubMsg() {
		// TODO Auto-generated method stub
		if(!checkConnection()){
			return ;
		}

		if(pubSubMgr == null){
			pubSubMgr = new PubSubManager(getXMPPService().getXMPPConnection());
		}
		LeafNode node = null;
		try {
			node = pubSubMgr.getNode("testNode");
			// Publish an Item, let service set the id
			node.send(new Item());
			// Publish an Item with the specified id
			node.send(new Item("123abc"));
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.pub_sub_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	

}
