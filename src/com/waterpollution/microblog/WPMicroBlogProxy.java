package com.waterpollution.microblog;

import org.json.JSONObject;

import com.weibo.sdk.android.net.RequestListener;


import android.app.Activity;

public class WPMicroBlogProxy implements IMicroBlog {

	private IMicroBlog microBlog;
	
	public WPMicroBlogProxy(){}
	
	public WPMicroBlogProxy(IMicroBlog _microBlog){
		microBlog=_microBlog;			
		getInstance();
	}
	
	public void setMicroBlog(IMicroBlog _microBlog){
	    microBlog=_microBlog;	
	    getInstance();
	}
	
	@Override
	public void getInstance() {
		microBlog.getInstance();
	}

	@Override
	public void authorize(Activity mainActivity) {
		microBlog.authorize(mainActivity);
	}

	@Override
	public void logOut() {		
		microBlog.logOut();
	}

	@Override
	public JSONObject getUserInfo() {
		return microBlog.getUserInfo();
	}

	@Override
	public void forward() {
		microBlog.forward();
	}

	@Override
	public boolean isOauth() {
		if (microBlog==null){
			return false;			
		}else{		
			return microBlog.isOauth();
		}
	}

	@Override
	public void sendWeibo(String text, String picFilePath, String latitude, String longitude, RequestListener listener) {
		microBlog.sendWeibo(text, picFilePath, latitude, longitude, listener);		
	}

	@Override
	public String getUserId() {
		return microBlog.getUserId();
	}

	@Override
	public String getNickName() {
		// TODO Auto-generated method stub
		return microBlog.getNickName();
	}

	@Override
	public boolean isSendWeiboSuccess() {
		return microBlog.isSendWeiboSuccess();
	}
	
}
