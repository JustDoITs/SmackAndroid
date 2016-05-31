package com.geostar.smackandroid.frag;

import java.util.List;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.geostar.smackandroid.MainActivity;
import com.geostar.smackandroid.R;
import com.geostar.smackandroid.ViewSubMsgActivity;
import com.geostar.smackandroid.adapter.SubsAdapter;
import com.geostar.smackandroid.adapter.SubsAdapter.ContentProv;

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
	
	public PubsubFragment(MainActivity activity) {
		super(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.pub_sub_layout, null);
		mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);// 使Fragment可以添加menu到ActionBar
		
		try {
			loadMySubs();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(), "获取订阅列表失败", 5).show();
		}
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				Affiliation affiliation = (Affiliation) parent.getAdapter().getItem(position);
				// 获取订阅节点
				String pubNode = affiliation.getNodeId();
				Intent intent = new Intent(PubsubFragment.this.getActivity(),ViewSubMsgActivity.class);
				intent.putExtra("node", pubNode);
				startActivity(intent);
			}
		});
	}


	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		super.onServiceConnected(name, service);
		// 加载可用订阅列表
//		loadAllPubs();
		
	}

	
	private void loadMySubs() throws NoResponseException, XMPPErrorException, NotConnectedException {
		if(!checkConnection()){
			return;
		}
		if(pubSubMgr == null){
			pubSubMgr = new PubSubManager(getXMPPService().getXMPPConnection());
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
		// TODO Auto-generated method stub
		if(pubSubMgr == null) return;
		LeafNode leaf = null;
		try {
			leaf = pubSubMgr.createNode(nodeName);
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(leaf == null) return;
		ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
		form.setAccessModel(AccessModel.open);
		form.setDeliverPayloads(false);
		form.setNotifyRetract(true);
		form.setPersistentItems(true);
		form.setMaxItems(9999999);
		form.setSubscribe(true);
		form.setPublishModel(PublishModel.open);
		try {
			leaf.sendConfigurationForm(form);
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		new SimplePayload("book", "pubsub:test:book", "Two Towers")));
	}


	@Override
	public void onServiceDisconnected(ComponentName name) {
		super.onServiceDisconnected(name);
		
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add_sub:
			
			break;
		case R.id.action_add_test_node:
			
			View diaView = LayoutInflater.from(this.getActivity()).inflate(R.layout.pub_sub_test_input_msg_layout, null);
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
	
	

	

}
