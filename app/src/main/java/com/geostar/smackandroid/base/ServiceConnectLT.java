package com.geostar.smackandroid.base;

import org.jivesoftware.smack.AbstractXMPPConnection;

public interface ServiceConnectLT {
	void onServiceConnected(AbstractXMPPConnection conn);
}
