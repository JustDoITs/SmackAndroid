package com.geostar.smackandroid.service;


/**
 * Created by jianghanghang on 2016/5/24.
 */
public interface XMPPLoginCallback {

    public void onLoginSuccess();

    public void onLoginFailed(Exception e);

}
