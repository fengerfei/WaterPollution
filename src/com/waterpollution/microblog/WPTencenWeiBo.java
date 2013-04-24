package com.waterpollution.microblog;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

public class WPTencenWeiBo implements IMicroBlog {	
	//private static final String CLINETID = "801344612";
	//private static final String CLIENTSECRET = "f1400790716a069c33ece9e92714d9a4";
	private static final String CLINETID = "801325131";
	private static final String CLIENTSECRET = "43052cdaa262ca2815dbe711024f9daa";
//	private String redirectUriTen="http://42.120.23.245/"; 
//	private String redirectUriTen="http://www.tencent.com/zh-cn/index.shtml";
//	private String redirectUriTen="http://www.vvbox.com.cn"; 
	private String redirectUriTen="http://www.wushuiditu.org";
//	private String redirectUriTen=""; 
    private OAuthV2 authV2 = null;
    public Activity currentActivity;
    public JSONObject userInfo;
    private String uid;
    private String nickName;
    private boolean sendweibosuccess = false;
	@Override
	public void getInstance() {
		authV2 = new OAuthV2(redirectUriTen);
		authV2.setClientId(CLINETID);
		authV2.setClientSecret(CLIENTSECRET);
		authV2.setClientIP(getHostIp());
	}

	@Override
	public void authorize(Activity mainActivity) {
		this.currentActivity = mainActivity;
		if(!isOauchTen()){
			//关闭OAuthV2Client中的默认开启的QHttpClient。
	        OAuthV2Client.getQHttpClient().shutdownConnection();
        	Intent intent = new Intent(currentActivity, OAuthV2AuthorizeWebView.class);//创建Intent，使用WebView让用户授权
            intent.putExtra("oauth", authV2);
            currentActivity.startActivityForResult(intent,2);
		}
	}

	@Override
	public void logOut() {
		WPWeiBoAccessTokenKeeper.clear(WPTencenWeiBo.this.currentActivity);
		WPWeiBoAccessTokenKeeper.KeepWeibotype(WPTencenWeiBo.this.currentActivity, WPMicroBlogConstant.UNKNOW);
		userInfo = null;
	}

	@Override
	public JSONObject getUserInfo() {
		if (userInfo==null){
			UserAPI userAPI=new UserAPI(OAuthConstants.OAUTH_VERSION_2_A);
			 try {
                String response=userAPI.info(authV2, WPMicroBlogConstant.RETURNTYPE_JSON);
                try {
                	userInfo = new JSONObject(response);
                	if (userInfo.getString("errcode").equals("0")){
                		JSONObject data= new JSONObject(userInfo.getString("data"));
                		uid = data.getString("openid");
                		nickName = data.getString("nick");
                		userInfo.put("WPnick",data.getString("nick"));
                		userInfo.put("WPHeadUrl",data.getString("head")+"/50");
                	}
    			} catch (JSONException e) {
    				userInfo = null;
    				e.printStackTrace();
    			}
                userAPI.shutdownConnection();				 
            } catch (Exception e) {
                e.printStackTrace();
            }
			return userInfo;
		}else{
			return userInfo;
		}
	}
	
	public static String getHostIp() {	
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
						.hasMoreElements();) {
					InetAddress inetAddress = ipAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {						
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException ex) {
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void forward() {
		

	}
	
		
	/**
     * 腾讯微博接入是否已经验证
     * @return
     */
    private boolean isOauchTen() {
    	boolean b = false;
    	WPWeiBoAccessTokenKeeper.readAccessTokenTencen(currentActivity,authV2);
    	if(authV2 != null && authV2.getStatus()==0 && !authV2.getAccessToken().equals("")){
    		b = true;
    	}
    	return b;
	}

	@Override
	public boolean isOauth() {		
		return isOauchTen();
	}

	@Override
	public void sendWeibo(String text, String picFilePath, String latitude, String longitude, RequestListener listener) {
		sendweibosuccess = false;
		TAPI tapi = new TAPI(OAuthConstants.OAUTH_VERSION_2_A);
		String response;
		 try {
			 if (picFilePath.equals("")){
				 response=tapi.add(authV2, "json", text, getHostIp(),longitude,latitude,"0"); 
			 }else{
				 response=tapi.addPic(authV2, "json", text, getHostIp(),longitude,latitude,picFilePath,"0"); 
			 }
           tapi.shutdownConnection();
            JSONObject res = new JSONObject(response);
            if (!res.getString("errcode").equals("0")){
            	Toast.makeText(this.currentActivity.getApplicationContext(),"发送微博失败！", Toast.LENGTH_LONG).show();
            }else{
            	sendweibosuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
		return sendweibosuccess;
	}
}
