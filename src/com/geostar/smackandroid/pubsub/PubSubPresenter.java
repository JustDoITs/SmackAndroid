package com.geostar.smackandroid.pubsub;

import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import android.util.Log;

import com.geostar.smackandroid.service.XMPPService;

/**
 * 
 * @author jianghanghang
 *
 * 目前没有存储，所以订阅列表数据每次全部替换
 */
public class PubSubPresenter implements PubSubContract.Presenter,ItemEventListener<Item> {

	private static final String TAG = "PubSubPresenter";
	
	private static boolean hasRegisterListener = false;

	private AbstractXMPPConnection mConnection;
	
	private PubSubContract.View mPubsView;

	private PubSubManager mPubsubManager;
	
	public PubSubPresenter(AbstractXMPPConnection conn, PubSubContract.View contactView) {
		super();
		this.mConnection = conn;
		this.mPubsView = contactView;
		contactView.setPresenter(this);
		if(mConnection != null && mConnection.isConnected()){
			mPubsubManager = new PubSubManager(mConnection);
		}else{
			System.out.println("------ ERROR: connnect not connected!!!");
		}
	}

	@Override
	public void start(){
		if(mConnection != null && mConnection.isConnected()){
			mPubsubManager = new PubSubManager(mConnection);
			// TODO: 
		}
		
		List<Affiliation> afs = null;
		try {
			afs = mPubsubManager.getAffiliations();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			e.printStackTrace();
			// TODO： 细分异常
			mPubsView.handleGetDataException(e);
		}
		if(afs == null){
			// TODO: 
			mPubsView.showNoSubscribleNode();
		}else{
			mPubsView.showAllSubscribleNode(afs);
			
			// TODO: 针对每一节点记录是否订阅
			if(!hasRegisterListener){
				registerEventListenerForAllNode();
				hasRegisterListener = true;
			}
		}
	}

	/** 
	 * 为每一个节点注册监听, 这里没有考虑到新加节点的注册
	 * <br/> 需要避免重复注册
	 */
	public void registerEventListenerForAllNode() {
		if(mConnection != null && mConnection.isConnected()){
			mPubsubManager = new PubSubManager(mConnection);
			// TODO: 
		}
		
		List<Affiliation> afs = null;
		try {
			afs = mPubsubManager.getAffiliations();
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final List<Affiliation> ff = afs; 
		XMPPService.doInBackground(new Runnable() {
			@Override
			public void run() {
//				if(!hasRegisterListener){
					regListenersToNode(ff);
//					hasRegisterListener = true;
//				}
			}
		});
	}

	private void regListenersToNode(List<Affiliation> afs) {
		Node node = null;
		for(Affiliation af : afs){
			try {
				node = mPubsubManager.getNode(af.getNodeId());
			} catch (NoResponseException | XMPPErrorException
					| NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(node != null && node instanceof LeafNode){
				// 为每一个节点注册监听,
				Log.e(TAG,"addItemEventListener== node =" + node.getId());
//				node.removeItemEventListener(this);
				node.addItemEventListener(this);
			}
		}
	}

	@Override
	public void onServiceConnected(AbstractXMPPConnection conn) {
//		mConnection = conn;
//		mRoster = Roster.getInstanceFor(conn);
//		List<RosterEntry> ent = getAllRosterEntrys();
//		if(ent != null){
//			mContactView.showContactList(ent);
//		}
	}
	

	@Override
	public PubSubManager getPubSubManager() {
		return mPubsubManager;
	}

	@Override
	public List<Affiliation> getAffiliations() throws NoResponseException, XMPPErrorException, NotConnectedException {
		return mPubsubManager.getAffiliations();
	}
	
	@Override
	public void subscribeToNode(String nodeId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 来自 ItemEventListener 
	 * <br/>
	 */
	@Override
	public void handlePublishedItems(ItemPublishEvent<Item> items) {
		// TODO 
		Log.e(TAG,"handlePublishedItems:" + items.getNodeId() + items.getItems().get(0).getId());
		String nodeId = items.getNodeId();
		mPubsView.onNewPubMsgComeFromBackgroudThread(nodeId,items.getItems().size());
	}



}
