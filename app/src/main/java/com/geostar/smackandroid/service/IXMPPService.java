package com.geostar.smackandroid.service;

import org.jivesoftware.smack.AbstractXMPPConnection;

/**
 * 后台服务需要提供的方法
 * @author jianghanghang
 *
 */
public interface IXMPPService {

	/**
	 * 获取可用的XMPP 连接
	 * @return
	 */
	AbstractXMPPConnection getXMPPConnection();
	
	/**
	 *  登出-- 断开连接
	 */
	void logout();
	
	
}
