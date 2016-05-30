package com.geostar.smackandroid.xmpp;


import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.roster.Roster;

/**
 * Created by jianghanghang on 2016/5/26.
 */
public interface IXMPPFunc {

    AbstractXMPPConnection getXMPPConnection();

    void connect();

    void reconnect();

    void login(String username,String password,XMPPLoginCallback callback);

    Roster getRoster();



}
