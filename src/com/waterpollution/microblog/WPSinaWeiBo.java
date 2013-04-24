package com.waterpollution.microblog;



import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.HttpManager;
import com.weibo.sdk.android.net.RequestListener;
import com.waterpollution.BaseActivity;

public class WPSinaWeiBo implements IMicroBlog {
	//private static final String CONSUMER_KEY = "443506509";
	private static final String CONSUMER_KEY = "1486675045";
	//private String redirectUriSina="http://www.sina.com"; 
	private String redirectUriSina="http://www.vvbox.com.cn"; 
	private Weibo mWeibo;
	public static Oauth2AccessToken accessToken;
	public Activity currentActivity;
	public JSONObject userInfo;
	private String uid;
	private String nickName;
	@Override
	public void getInstance() {
		mWeibo = Weibo.getInstance(CONSUMER_KEY, redirectUriSina);
	}
	
	@Override
	public void authorize(Activity mainActivity) {
		currentActivity = mainActivity;
		if (!isOauthSina(mWeibo)){
			mWeibo.authorize(currentActivity, new AuthDialogListener());
		}else{
			getUserId(accessToken.getToken());
		}
	}

	@Override
	public void logOut() {
		WPWeiBoAccessTokenKeeper.clear(WPSinaWeiBo.this.currentActivity);
		WPWeiBoAccessTokenKeeper.KeepWeibotype(WPSinaWeiBo.this.currentActivity, WPMicroBlogConstant.UNKNOW);
		accessToken = null;
		userInfo = null;
	}

	@Override
	public JSONObject getUserInfo() {
		if (userInfo==null){
			String url = "https://api.weibo.com/2/users/show.json";
			getUserId(accessToken.getToken());
			WeiboParameters weiboParameters = new WeiboParameters();
			weiboParameters.add("access_token", accessToken.getToken());
			weiboParameters.add("uid", uid);
			try {
				String result = HttpManager.openUrl(url,HttpManager.HTTPMETHOD_GET, weiboParameters, null);				
				userInfo = new JSONObject(result);	
				userInfo.put("WPnick",userInfo.getString("screen_name"));
				nickName = userInfo.getString("screen_name");
				userInfo.put("WPHeadUrl",userInfo.getString("profile_image_url"));
			} catch (JSONException e) {
				e.printStackTrace();
				userInfo=null;
			} catch (WeiboException e) {
				userInfo=null;
				e.printStackTrace();
			}
			return userInfo;
		}else{
			return userInfo;
		}
	}

	@Override
	public void forward() {
		// TODO Auto-generated method stub

	}
	
	private boolean isOauthSina(Weibo weibo){
    	boolean b = false;    	
    	accessToken = WPWeiBoAccessTokenKeeper.readAccessToken(currentActivity);
    	if(weibo != null && accessToken.isSessionValid()){
    		b = true;
    	}
    	return b;
    }
	
	
	public void getUserId(String token) {
			String url = "https://api.weibo.com/2/account/get_uid.json";
			WeiboParameters weiboParameters = new WeiboParameters();
			weiboParameters.add("access_token", token);
			try {
				String result = HttpManager.openUrl(url,HttpManager.HTTPMETHOD_GET, weiboParameters, null);
				JSONObject obj = new JSONObject(result);
				uid = obj.getString("uid");
			} catch (WeiboException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	 class AuthDialogListener implements WeiboAuthListener {

	        @Override
	        public void onComplete(Bundle values) {
	        	uid = values.getString("uid");
	            String token = values.getString("access_token");
	            String expires_in = values.getString("expires_in");	            
	            WPSinaWeiBo.accessToken = new Oauth2AccessToken(token, expires_in);
	            if (WPSinaWeiBo.accessToken.isSessionValid()) {
	                WPWeiBoAccessTokenKeeper.keepAccessToken(WPSinaWeiBo.this.currentActivity,accessToken);	
	                WPWeiBoAccessTokenKeeper.KeepWeibotype(WPSinaWeiBo.this.currentActivity, WPMicroBlogConstant.SINA);
	                Toast.makeText(WPSinaWeiBo.this.currentActivity, "认证成功", Toast.LENGTH_SHORT).show();   
	                BaseActivity bsa = (BaseActivity)WPSinaWeiBo.this.currentActivity;
	                bsa.UpdateControlByUserState();
	                bsa.initView();
	            }
	        }

	        @Override
	        public void onError(WeiboDialogError e) {	        	
	            Toast.makeText(WPSinaWeiBo.this.currentActivity.getApplicationContext(),
	                    "认证错误 : " + e.getMessage(), Toast.LENGTH_LONG).show();
	        }

	        @Override
	        public void onCancel() {
	            Toast.makeText(WPSinaWeiBo.this.currentActivity.getApplicationContext(), "认证取消",
	                    Toast.LENGTH_LONG).show();
	        }

	        @Override
	        public void onWeiboException(WeiboException e) {
	            Toast.makeText(WPSinaWeiBo.this.currentActivity.getApplicationContext(),
	                    "认证异常 : " + e.getMessage(), Toast.LENGTH_LONG)
	                    .show();
	        }
	    }

	@Override
	public boolean isOauth() {		
		return isOauthSina(mWeibo);
	}

	@Override
	public void sendWeibo(String text, String picFilePath, String latitude, String longitude, RequestListener listener) {
		StatusesAPI statusApi = new StatusesAPI(accessToken); 
		if (picFilePath.equals("")){
			statusApi.update(text, latitude, longitude, listener);
		}else{
			statusApi.upload(text, picFilePath, latitude, longitude,  listener);
		}
	}
	

	@Override
	public String getUserId() {
		return uid;		
	}

	@Override
	public String getNickName() {
		return nickName;
	}

	@Override
	public boolean isSendWeiboSuccess() {
		return false;
	}
}
