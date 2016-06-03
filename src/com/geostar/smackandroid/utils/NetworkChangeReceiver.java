package com.geostar.smackandroid.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态改变监听
 * <br/>
 * 一次网络状态的变化一般会发送两个（多个）广播（即onReceive会调用多次），一个断开，一个连接，所以实现OnNetworkChangeListener 时需要注意忽略不必要的广播
 * @author jianghanghang
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkReceiver";
	
	private boolean isNetworkAvailable = false;
	
	private List<OnNetworkChangeListener> mListener = new ArrayList<NetworkChangeReceiver.OnNetworkChangeListener>();
	
	public void addNetworkChangeListener(OnNetworkChangeListener listener){
		if( !mListener.contains(listener) ){
			mListener.add(listener);
		}
	}
	
	public void remove(OnNetworkChangeListener listener){
		if( mListener.contains(listener) ){
			mListener.remove(listener);
		}
	}
	
	public NetworkChangeReceiver() {
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		ConnectivityManager connectMgr =((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
		
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			
			NetworkInfo affectNetInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			NetworkInfo activeNetInfo = connectMgr.getActiveNetworkInfo();
			if(activeNetInfo == null){//没有可用网络连接
				notifyNetworkNotAvailable();
				return;
			}else{
				notifyNetworkAvailable();
			}
			if( affectNetInfo.getType() == ConnectivityManager.TYPE_WIFI 
					&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE ){
				Utils.logDebug("切换至移动数据");
				notifyNetworkChangeWifi2MobileData();
			}else if( /*affectNetInfo.getType() == ConnectivityManager.TYPE_MOBILE 
					&& */activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI ){
				Utils.logDebug("切换至WIFI");
				notifyNetworkToWifi();
			}
			Utils.logDebug("affectNetInfo: " + affectNetInfo.toString());
			Utils.logDebug("activeNetInfo: " + activeNetInfo.toString());
			
		}
	}

	private void notifyNetworkToWifi() {
		if( mListener != null ){
			for(OnNetworkChangeListener ls : mListener){
				ls.onWifiConnected();
			}
		}
	}

	private void notifyNetworkChangeWifi2MobileData() {
		if(mListener != null){
			for(OnNetworkChangeListener ls : mListener){
				ls.onWifiSwitch2Mobile();
			}
		}
	}

	private void notifyNetworkAvailable() {
		if(mListener != null){
			for(OnNetworkChangeListener ls : mListener){
				ls.onConnectAvaliable();
			}
		}
	}

	private void notifyNetworkNotAvailable() {
		if(mListener != null){
			for(OnNetworkChangeListener ls : mListener){
				ls.onConnectLost();
			}
		}
	}
	
	/**
	 * 网络状态变化监听方法区
	 * @author jianghanghang
	 *
	 */
	public interface OnNetworkChangeListener{
		/**
		 * 由Wifi网络切换到移动数据
		 */
		void onWifiSwitch2Mobile();
//		void onMoblieSwitch2Wifi();
		/**
		 * Wifi连接时（不一定是从移动数据切换至wifi）
		 */
		void onWifiConnected();
		
		void onConnectLost();
		
		void onConnectAvaliable();
	}

}
