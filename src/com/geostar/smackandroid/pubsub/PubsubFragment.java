package com.geostar.smackandroid.pubsub;

import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.geostar.smackandroid.BaseFragment;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.pubsub.adapter.SubsAdapter;
import com.geostar.smackandroid.pubsub.adapter.SubsAdapter.ContentProv;

/**
 * 订阅子页面
 * <br/> * 列表显示已订阅
 * @author jianghanghang
 *
 */
public class PubsubFragment extends BaseFragment implements OnRefreshListener{


	private static final String TAG = "PubsubFragment";
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private PubSubManager pubSubMgr;
	
	/** 订阅关系数据  */
	private List<Affiliation> mPubs;

	private Subscription mSubcription;

	public PubsubFragment(AbstractXMPPConnection conn) {
		super(conn);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_pub_sub, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);// 使Fragment可以添加menu到ActionBar
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Affiliation affiliation = (Affiliation) parent.getAdapter().getItem(position);
				// 获取订阅节点
				String pubNode = affiliation.getNodeId();
				goViewPubMessage(pubNode);
			}
		});
		
		try {
			loadMySubs();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), "获取订阅列表失败", 5).show();
		}
		
	}

	
	@Override
	public void onResume() {
		registerForContextMenu(getListView());
		super.onResume();
	}
	
	private void loadMySubs() throws NoResponseException, XMPPErrorException, NotConnectedException {
		if(!checkConnection()){
			return;
		}
		if(pubSubMgr == null){
			pubSubMgr = new PubSubManager(getXMPPConnection());
		}
		// 获取订阅关系
		mPubs = pubSubMgr.getAffiliations();
		SubsAdapter<Affiliation> adapter = new SubsAdapter<Affiliation>(getContext(), mPubs, new ContentProv<Affiliation>() {

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

	private void createNode(String nodeName) {
		if(pubSubMgr == null) return;
		LeafNode leaf = null;
		try {
			leaf = pubSubMgr.getNode(nodeName);
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if(leaf!= null){
			Toast.makeText(getContext(), "节点已经存在了！！！", 5).show();
			return;
		}
		
		ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
		form.setAccessModel(AccessModel.open);
		form.setDeliverPayloads(false);
		form.setNotifyRetract(true);
		form.setPersistentItems(true);
		form.setSubscribe(true);
		try {
			leaf = (LeafNode) pubSubMgr.createNode(nodeName, form);
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getContext(), "节点创建异常！！！", 5).show();
		}
		if(leaf == null) {
			Toast.makeText(getContext(), "节点创建失败！！！", 5).show();
			return;
		}
	}


	@Override
	public void onRefresh() {
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
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.pub_sub_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add_sub:
			subcribeANode();
			break;
		case R.id.action_add_test_node:
			
			View diaView = LayoutInflater.from(this.getActivity()).inflate(R.layout.test_pub_sub_input_msg_layout, null);
			final EditText input = (EditText) diaView.findViewById(R.id.et_content_input);
			AlertDialog dialog = new AlertDialog.Builder(this.getActivity()
					,android.R.style.Theme_DeviceDefault_Dialog)
					.setTitle("创建节点：输入节点名")
					.setView(diaView)
					.setPositiveButton("创建", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nodeName = input.getText().toString();
							if(!TextUtils.isEmpty(nodeName)){
								createNode(nodeName);
							}else{
								Toast.makeText(PubsubFragment.this.getActivity(), "没有输入内容", 2).show();
							}
						}
					})
					.setNegativeButton("取消", null)
					.create();
			dialog.show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void subcribeANode() {
		View diaView = LayoutInflater.from(this.getActivity()).inflate(R.layout.test_pub_sub_input_msg_layout, null);
		final EditText input = (EditText) diaView.findViewById(R.id.et_content_input);
		AlertDialog dialog = new AlertDialog.Builder(this.getActivity()
				,android.R.style.Theme_DeviceDefault_Dialog)
				.setTitle("订阅节点-输入节点名")
				.setView(diaView)
				.setPositiveButton("订阅", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String nodeName = input.getText().toString();
						if(!TextUtils.isEmpty(nodeName)){
							doSubcribleANode(nodeName);
						}else{
							Toast.makeText(PubsubFragment.this.getActivity(), "没有输入内容", 2).show();
						}
					}
				})
				.setNegativeButton("取消", null)
				.create();
		dialog.show();
	}

	protected void doSubcribleANode(String nodeName) {
		if(pubSubMgr == null) return;
		
		Node node = null;
		try {
			node = pubSubMgr.getNode(nodeName);
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<Subscription> allRels = null;
		try {
			allRels = node.getSubscriptions();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(allRels == null) return;
		
		String curJid = getXMPPConnection().getUser();
		boolean isSubcribled = false;
		for(Subscription s : allRels){
			if(curJid.contains(s.getJid())){
				isSubcribled = true;
				break;
			}
		}
		
		if(node == null) return;
		
		node.addItemEventListener(new ItemEventListener <Item>(){

			@Override
			public void handlePublishedItems(ItemPublishEvent<Item> items) {
				// TODO Auto-generated method stub
				if(items!= null && items.getItems().size() > 0){
					Log.d(TAG,"-------------------- handlePublishedItems: size=" +
								items.getItems().size() + "; "+ items.getItems().get(0).toString());
					Toast.makeText(getActivity(), "收到一条订阅消息：" + items.getItems().get(0).getId(), 3).show();
				}
			}
		});
		if(isSubcribled) {
			Toast.makeText(getActivity(), "已订阅，不用重复订阅。", 3).show();
			return;
		}
		
		try {
			mSubcription = node.subscribe(getXMPPConnection().getUser());
			Log.d(TAG,"-------------------- Subscription:" + mSubcription.toString());
//			Subscription	subscribe(String jid, SubscribeForm subForm)
//			The user subscribes to the node using the supplied jid and subscription options.
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getActivity(), "订阅失败：" + e.getMessage(), 3).show();
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {
		unregisterForContextMenu(getListView());
		super.onStop();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		new MenuInflater(getActivity()).inflate(R.menu.pub_sub_context_menu, menu);
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		 AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		 switch (item.getItemId()) {
		case R.id.action_delete_node:
			deleteNode(info);
			break;

		default:
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void deleteNode(AdapterContextMenuInfo info) {
		// TODO Auto-generated method stub
		try {
			pubSubMgr.deleteNode(mPubs.get(info.position).getNodeId());
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getActivity(), "删除失败：" + e.getMessage(), 3).show();
			e.printStackTrace();
		}
		// refresh
		onRefresh();
	}

	private void goViewPubMessage(String pubNode) {
		Intent intent = new Intent(PubsubFragment.this.getActivity(),ViewSubMsgActivity.class);
		intent.putExtra("node", pubNode);
		startActivity(intent);
	}
}
