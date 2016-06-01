package com.geostar.smackandroid;

import org.jivesoftware.smack.AbstractXMPPConnection;

public interface OnConnectionMake {
	
	void onServiceConnected(AbstractXMPPConnection conn);
}
