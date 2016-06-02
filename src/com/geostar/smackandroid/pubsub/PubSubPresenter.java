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


public class PubSubPresenter implements PubSubContract.Presenter,ItemEventListener<Item> {

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
			// TODO: 
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
					// 为每一个节点注册监听
					node.addItemEventListener(this);
				}
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
	public void handlePublishedItems(ItemPublishEvent<Item> items) {
		// TODO 有问题，暂时屏蔽
//		String nodeId = items.getNodeId();
//		mPubsView.notifyNewPubMessageFromBackgroud(nodeId,items.getItems().size());
	}



}
