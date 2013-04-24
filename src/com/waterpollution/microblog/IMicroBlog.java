/**
 * 
 */
package com.waterpollution.microblog;

import org.json.JSONObject;

import com.weibo.sdk.android.net.RequestListener;


import android.app.Activity;


/**
 * @author Administrator
 * 第三方微博接口
 */
public interface IMicroBlog {
	public void getInstance();
	public void authorize(Activity mainActivity);
	public void logOut();
	public JSONObject getUserInfo();
	public void forward();
	public boolean isOauth();
	public void sendWeibo(String text,String picFilePath, String latitude, String longitude, RequestListener listener);
	public String getUserId();
	public String getNickName();
	public boolean isSendWeiboSuccess();
}
