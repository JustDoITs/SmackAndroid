package com.geostar.smackandroid.manager.login;

public interface ILoginManager {
	
	public interface OnLoginCallback{
		/**
		 * 不要执行UI操作
		 */
		void onSuccess();
		/**
		 * 不要执行UI操作
		 */
		void onFailed();
	}
	
	public void login(String username,String password,OnLoginCallback callback);
	
	public void register(String username,String password,OnLoginCallback callback);
	
	public void logout();
	// TODO:
	public void addOnLoginStateChangedListener();
}
