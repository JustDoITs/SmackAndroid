package com.geostar.smackandroid.pubsub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geostar.smackandroid.R;
import com.geostar.smackandroid.base.BaseFragment;
import com.geostar.smackandroid.pubsub.PubSubContract.Presenter;
import com.geostar.smackandroid.pubsub.mock.ViewSubMsgActivity;

/**
 * 订阅子页面
 * <br/> * 列表显示已订阅
 * @author jianghanghang
 *
 */
public class PubSubFragment extends BaseFragment implements PubSubContract.View,OnRefreshListener{

	private static final String TAG = "PubsubFragment";
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	/** 订阅关系数据  */
	private List<Affiliation> mPubs;

	private Subscription mSubcription;
	
	private PubSubContract.Presenter mPresenter;
	
	private SubsAdapter mSubsAdapter;
	
	private Map<String,Integer> hasNewMessageNode = new HashMap<String,Integer>();

	
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
				removeNewMessgeFlag(pubNode);
				mSubsAdapter.notifyDataSetChanged();
				goViewPubMessage(pubNode);
			}
		});
		
		onRefresh();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		mPresenter = presenter;
	}

	@Override
	public void showAllSubscribleNode(List<Affiliation> rels) {
		// TODO Auto-generated method stub
		if(rels == null) return;
		
		// 获取订阅关系
		mPubs = rels;
		mSubsAdapter = new SubsAdapter(getContext(), mPubs);

		getListView().setAdapter(mSubsAdapter);
	}

	@Override
	public void showNoSubscribleNode() {
		Toast.makeText(getContext(), "没有订阅任何节点", 3).show();
	}

	@Override
	public void updateSubscribleNode(List<Affiliation> rels) {
		// TODO Auto-generated method stub
		showAllSubscribleNode(rels);
	}


	@Override
	public void handleGetDataException(Exception e) {
		// TODO 细分处理各种异常，并给出提示
		Toast.makeText(getContext(), "与服务器通讯异常", 3).show();
	}
	
	
	@Override
	public void onNewPubMsgComeFromBackgroudThread(final String nodeId,final int howMany) {
		// TODO Auto-generated method stub
		if(getView() != null){
			getView().post(new Runnable() {
				
				@Override
				public void run() {
					addNewMessageFlag(nodeId, howMany);
					if(mSubsAdapter != null){
						mSubsAdapter.notifyDataSetChanged();
//						getListView().invalidate();
					}
				}
			});
		}
	}
	
	
	@Override
	public void onResume() {
		if(mPresenter != null){
			mPresenter.start();
		}
		registerForContextMenu(getListView());
		super.onResume();
	}
	

	@Override
	public void onRefresh() {
		if(mPresenter != null){
			try {
				updateSubscribleNode(mPresenter.getAffiliations());
			} catch (NoResponseException | XMPPErrorException
					| NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handleGetDataException(e);
			}
		}
		mSwipeRefreshLayout.setRefreshing(false);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.pub_sub_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_add_sub:
			showSubcribeDialog();
			break;
		case R.id.action_add_test_node:
			showAddNodeDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAddNodeDialog() {
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
							doAddaNode(nodeName);
						}else{
							Toast.makeText(PubSubFragment.this.getActivity(), "没有输入内容", 2).show();
						}
					}
				})
				.setNegativeButton("取消", null)
				.create();
		dialog.show();
	}
	
	private void doAddaNode(String nodeName) {
		if(mPresenter == null) return;
		
		LeafNode leaf = null;
		try {
			leaf = mPresenter.getPubSubManager().getNode(nodeName);
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
			leaf = (LeafNode) mPresenter.getPubSubManager().createNode(nodeName, form);
			updateSubscribleNode(mPresenter.getAffiliations());
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
	
	private void showSubcribeDialog() {
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
							doSubcribleNode(nodeName);
						}else{
							Toast.makeText(PubSubFragment.this.getActivity(), "没有输入内容", 2).show();
						}
					}
				})
				.setNegativeButton("取消", null)
				.create();
		dialog.show();
	}

	protected void doSubcribleNode(String nodeName) {
		if(mPresenter.getPubSubManager() == null) return;
		
		Node node = null;
		try {
			node = mPresenter.getPubSubManager().getNode(nodeName);
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
		
		String curJid = mPresenter.getCurrentUser();
		boolean isSubcribled = false;
		for(Subscription s : allRels){
			if(curJid.contains(s.getJid())){
				isSubcribled = true;
				break;
			}
		}
		
		if(node == null) return;
		// 为新添加的节点添加订阅消息推送通知
		node.addItemEventListener((PubSubPresenter)mPresenter);
		if(isSubcribled) {
			getView().post(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getActivity(), "已订阅，不用重复订阅。", 3).show();
				}
			});
			return;
		}
		
		try {
			mSubcription = node.subscribe(mPresenter.getCurrentUser());
			Log.d(TAG,"-------------------- Subscription:" + mSubcription.toString());
//			Subscription	subscribe(String jid, SubscribeForm subForm)
//			The user subscribes to the node using the supplied jid and subscription options.
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			getView().post(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getActivity(), "订阅失败：" + e.getMessage(), 3).show();
				}
			});
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
		try {
			mPresenter.getPubSubManager().deleteNode(mPubs.get(info.position).getNodeId());
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
		Intent intent = new Intent(PubSubFragment.this.getActivity(),ViewSubMsgActivity.class);
		intent.putExtra("node", pubNode);
		startActivity(intent);
	}


	/** 添加未读计数 */
	private void addNewMessageFlag(String nodeId,int num){
		int unreadTotal = num;
		if(hasNewMessageNode.containsKey(nodeId)){
			unreadTotal += hasNewMessageNode.get(nodeId).intValue();
			
		}
		hasNewMessageNode.put(nodeId, unreadTotal);
	}
	
	private void removeNewMessgeFlag(String nodeId){
		hasNewMessageNode.remove(nodeId);
	}
	
	
	class SubsAdapter extends BaseAdapter {

		private List<Affiliation> mDatas;
		private Context mContext;
	 	
		public SubsAdapter(Context context,List<Affiliation> mDatas) {
			super();
			this.mDatas = mDatas;
			mContext = context;
		}

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Affiliation getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(convertView ==  null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_pubs_node_item, null);
				holder = new Holder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (Holder) convertView.getTag();
			}
			Affiliation item = getItem(position);
			holder.title.setText(item.getNodeId());
			holder.desc.setText(item.getType().toString());
			holder.addSubs.setVisibility(View.GONE);
			if(hasNewMessageNode.keySet().contains(item.getNodeId())){
				holder.redDot.setText(hasNewMessageNode.get(item.getNodeId())  + "");
				holder.redDot.setVisibility(View.VISIBLE);
			}else{
				holder.redDot.setVisibility(View.GONE);
			}
			return convertView;
		}
		
		class Holder {
			TextView title;
			TextView desc;
			Button addSubs;
			Button redDot;
			public Holder(View convertView) {
				this.title = (TextView) convertView.findViewById(R.id.tv_nodename);
				this.desc = (TextView) convertView.findViewById(R.id.tv_desc);
				this.addSubs = (Button) convertView.findViewById(R.id.btn_subcrible);
				this.redDot = (Button) convertView.findViewById(R.id.btn_redot);
			}
		}

	}

}
