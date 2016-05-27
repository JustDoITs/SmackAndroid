package com.geostar.smackandroid.service;

import org.jivesoftware.smack.AbstractXMPPConnection;

public interface IXMPPConnectionStateChanged {

	void onXMPPConnect(boolean isAvalable,AbstractXMPPConnection conn);
}
