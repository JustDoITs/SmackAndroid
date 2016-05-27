package com.geostar.smackandroid.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.geostar.smackandroid.R;
import com.geostar.smackandroid.manager.IManager;
import com.geostar.smackandroid.manager.im.IMManagerImpl;
import com.geostar.smackandroid.manager.login.LoginManagerImpl;

public class NewXMPPService extends Service {

	private static final String TAG = "XMPPService";
    private AbstractXMPPConnection mXmppConnection;

    private final IBinder mBinder = new XMPPBinder();
    
    private Map<String,IManager> mManagers;
    
    private List<IXMPPConnectionStateChanged> mConnectChangeListeners;
	
    public static final String IM_MANAGER = "IMManagerImpl";
    public static final String LOGIN_MANAGER = "LoginManagerImpl";
    

    @Override
    public void onCreate() {
    	mManagers = new ArrayMap<>();
    	initialManagers();
    	super.onCreate();
    }
    
    private void initialManagers() {
    	mManagers.put(IM_MANAGER, new IMManagerImpl());
    	mManagers.put(LOGIN_MANAGER, new LoginManagerImpl());
    	
    	addServiceConnectionListener(mManagers.get(LOGIN_MANAGER));
    	addServiceConnectionListener(mManagers.get(IM_MANAGER));
	}
    
    
    public IManager getManager(String managerStr){
    	if(mManagers.containsKey(managerStr)){
    		return mManagers.get(managerStr);
    	}
		return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 连接到服务器
     * @param config
     * @throws SmackException
     * @throws IOException
     * @throws XMPPException
     */
    public void connectToXMPPServer(ConnectionConfiguration config) throws SmackException, IOException, XMPPException{
    	
    	SmackConfiguration.DEBUG = true;
    	if(config instanceof XMPPTCPConnectionConfiguration){
    		mXmppConnection = new XMPPTCPConnection((XMPPTCPConnectionConfiguration) config);
    	}
    	// TODO: 
    	mXmppConnection.addConnectionListener(mDefaultXmppconnectListener );
    	IManager.executeBackgroudWork(new Runnable() {
			@Override
			public void run() {
				try {
					mXmppConnection.connect();
				} catch (SmackException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XMPPException e) {
					e.printStackTrace();
				}
			}
		});
    }
    
    /**
     * 连接到服务器
     * @param config
     * @throws SmackException
     * @throws IOException
     * @throws XMPPException
     */
    public void connectToXMPPServerUserDefaultSet(String username,String password) throws SmackException, IOException, XMPPException{
    	Log.d(TAG,"connectToXMPPServerUserDefaultSet");
        String hostIp = getResources().getString(R.string.server_ip_address);
        String serviceName = getResources().getString(R.string.server_name);
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
        		.setUsernameAndPassword(username, password)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(hostIp)
                .setServiceName(serviceName)
                .setResource(getString(R.string.defalut_resource_name))
                .setPort(getResources().getInteger(R.integer.server_port))
                .build();
        connectToXMPPServer(config);
    }

	public void addServiceConnectionListener(IXMPPConnectionStateChanged listener){
    	if(mConnectChangeListeners == null){
    		mConnectChangeListeners = new ArrayList<IXMPPConnectionStateChanged>();
    	}
    	mConnectChangeListeners.add(listener);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"Onbind");
        return mBinder;
    }

    public class XMPPBinder extends Binder {
        public NewXMPPService getService(){
            return NewXMPPService.this;
        }
    }

    public AbstractXMPPConnection getXMPPConnection() {
        return mXmppConnection;
    }

    
    private ConnectionListener mDefaultXmppconnectListener = new ConnectionListener() {
		
		@Override
		public void reconnectionSuccessful() {
			notifyConnectChanged(true);
		}
		
		@Override
		public void reconnectionFailed(Exception e) {
			notifyConnectChanged(false);
		}
		
		@Override
		public void reconnectingIn(int seconds) {
			
		}
		
		@Override
		public void connectionClosedOnError(Exception e) {
			notifyConnectChanged(false);
		}
		
		@Override
		public void connectionClosed() {
			notifyConnectChanged(false);
			
		}
		
		@Override
		public void connected(XMPPConnection connection) {
			notifyConnectChanged(true);
		}
		
		@Override
		public void authenticated(XMPPConnection connection, boolean resumed) {
			notifyConnectChanged(true);
		}
	};
	
	private void notifyConnectChanged(boolean isAV){
		for(IXMPPConnectionStateChanged lst :mConnectChangeListeners){
			lst.onXMPPConnect(isAV,mXmppConnection);
		}
	}
    
    
}
