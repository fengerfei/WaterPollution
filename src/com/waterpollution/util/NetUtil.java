package com.waterpollution.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.waterpollution.R;
import com.waterpollution.vo.RequestVo;

/**
 * 
 * @author liu
 * 
 */
public class NetUtil {
	private static final String TAG = "NetUtil";
	private static Header[] headers = new BasicHeader[11];
	static {
		headers[0] = new BasicHeader("Appkey", "");
		headers[1] = new BasicHeader("Udid", "");
		headers[2] = new BasicHeader("Os", "");
		headers[3] = new BasicHeader("Osversion", "");
		headers[4] = new BasicHeader("Appversion", "");
		headers[5] = new BasicHeader("Sourceid", "");
		headers[6] = new BasicHeader("Ver", "");
		headers[7] = new BasicHeader("Userid", "");
		headers[8] = new BasicHeader("Usersession", "");
		headers[9] = new BasicHeader("Unique", "");
		headers[10] = new BasicHeader("Cookie", "");
		
	}
	
	/*
	 * 
	 */
	public static Object post(RequestVo vo) {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = vo.context.getString(R.string.app_host).concat(vo.context.getString(vo.requestUrl));
		Logger.d(TAG, "Post " + url);
		HttpPost post = new HttpPost(url);
		post.setHeaders(headers);
		try {
			if (vo.requestDataMap != null) {
				Map<String, String> map = vo.requestDataMap;
				ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
				for (Map.Entry<String, String> entry : map.entrySet()) {
					BasicNameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
					pairList.add(pair);
				}
				HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8");
				post.setEntity(entity);
			}
			HttpResponse response = client.execute(post);
			String result = EntityUtils.toString(response.getEntity(), "UTF-8");
			Logger.d("TAG", result);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				setCookie(response);
//				String result = EntityUtils.toString(response.getEntity(), "UTF-8");
				return vo.xmlParser.parseXML(response.getEntity().getContent());
			}
		} catch (ClientProtocolException e) {
			Logger.e(TAG, e.getLocalizedMessage(), e);
		} catch (Exception e) {
			Logger.e(TAG, e.getLocalizedMessage(), e);
		}
 		return null;
	}

	/**
	 * 添加Cookie
	 * @param response
	 */
	private static void setCookie(HttpResponse response) {
		if (response.getHeaders("Set-Cookie").length > 0) {
			Logger.d(TAG, response.getHeaders("Set-Cookie")[0].getValue()) ;
			headers[10] = new BasicHeader("Cookie", response.getHeaders("Set-Cookie")[0].getValue());
		}
	}

	
	/**
	 * 
	 * @param vo
	 * @return
	 */
	public static Object get(RequestVo vo) {
		DefaultHttpClient client = new DefaultHttpClient();
		String url = vo.context.getString(R.string.app_host).concat(vo.context.getString(vo.requestUrl));
		if (vo.requestDataMap != null) {
			Map<String, String> map = vo.requestDataMap;
			StringBuffer params = new StringBuffer("?");
			for (Map.Entry<String, String> entry : map.entrySet()) {
				params.append(entry.getKey());
				params.append("=");
				params.append(entry.getValue());
				params.append("&");
			}
			url += params.substring(0, params.length()-1);
		}
		Logger.d(TAG, "Get " + url);
		HttpGet get = new HttpGet(url);
		get.setHeaders(headers);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				setCookie(response);
//				String result = EntityUtils.toString(response.getEntity(), "UTF-8");
//				Logger.d(TAG, result);
				return vo.xmlParser.parseXML(response.getEntity().getContent());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			return false;
		}
		return true;
	}
	
	//utf-8转码
	public static String StringToUnicode(String s){
		
		String r ="";
		try {
			r = URLEncoder.encode(s,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return r;
	}	
}
