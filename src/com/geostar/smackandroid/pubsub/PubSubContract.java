package com.geostar.smackandroid.pubsub;

import java.util.List;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.PubSubManager;

import com.geostar.smackandroid.base.BasePresenter;
import com.geostar.smackandroid.base.BaseView;

public interface PubSubContract {

	interface View extends BaseView<Presenter> {

		void showAllSubscribleNode(List<Affiliation> rels);

		void showNoSubscribleNode();
		
		void updateSubscribleNode(List<Affiliation> rels);
		
		void handleGetDataException(Exception e);
		
		void notifyNewPubMessageFromBackgroud(String nodeId,int howMany);
		
//		void updataContactPresenceState(String who,Presence presence);

	}

	interface Presenter extends BasePresenter {

		PubSubManager getPubSubManager();
		
		List<Affiliation> getAffiliations() throws NoResponseException, XMPPErrorException, NotConnectedException;
		
	}
	
}
