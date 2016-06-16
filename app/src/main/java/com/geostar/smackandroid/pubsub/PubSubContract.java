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

		/**
		 * 显示订阅列表 <br/>
		 * <b> 目前showAllSubscribleNode 和 updateSubscribleNode 实现相同
		 * @param rels
		 */
		void showAllSubscribleNode(List<Affiliation> rels);

		void showNoSubscribleNode();
		
		/**
		 * 显示订阅列表 <br/>
		 * <b> 目前showAllSubscribleNode 和 updateSubscribleNode 实现相同
		 * @param rels
		 */
		void updateSubscribleNode(List<Affiliation> rels);
		
		void handleGetDataException(Exception e);
		
		/**
		 * 当订阅的节点有新的消息来到时，更新UI
		 * <br/> 注意更新UI需要在主线程中执行
		 * @param nodeId
		 * @param howMany
		 */
		void onNewPubMsgComeFromBackgroudThread(String nodeId,int howMany);
		
//		void updataContactPresenceState(String who,Presence presence);

	}

	interface Presenter extends BasePresenter {

		String getCurrentUser();
		
		/**
		 * 返回订阅管理器的实例
		 * @return
		 */
		PubSubManager getPubSubManager();
		
		/**
		 * 获取当前登录用户所有的订阅关系
		 * @return 
		 * @throws NoResponseException
		 * @throws XMPPErrorException
		 * @throws NotConnectedException
		 */
		List<Affiliation> getAffiliations() throws NoResponseException, XMPPErrorException, NotConnectedException;
		
		/**
		 * 为当前用户订阅一个节点<br/>
		 * 如果已经订阅，则不会重复订阅
		 * @param nodeId
		 */
		void subscribeToNode(String nodeId);
	}
	
}
