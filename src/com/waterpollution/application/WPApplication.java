package com.waterpollution.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.waterpollution.maps.MapRoundList;
import com.waterpollution.microblog.IMicroBlog;
import com.waterpollution.microblog.WPMicroBlogConstant;
import com.waterpollution.microblog.WPMicroBlogProxy;
import com.waterpollution.microblog.WPSinaWeiBo;
import com.waterpollution.microblog.WPTencenWeiBo;
import com.waterpollution.microblog.WPWeiBoAccessTokenKeeper;
import com.waterpollution.util.Constant;
import com.waterpollution.util.Logger;
import com.waterpollution.vo.ComplaintEntity;

public class WPApplication extends Application {

	/** 缓存路径 */
	private static String cacheDir;

	private List<Activity> records = new ArrayList<Activity>();
	private static final String TAG = "WPApplication";
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener;
	public WPMicroBlogProxy mbProxy = null;
	public BMapManager mapManager = null;
	public IMicroBlog weiboInstance;
	public int weibotype;
	public List<ComplaintEntity> complaintEntitylist = new ArrayList<ComplaintEntity>();
	public MapRoundList mapRoundList = new MapRoundList((WPApplication)this);
	private int leftButtonIndex = Constant.LeftButton.BACK;
    public static final String strKey = "2CAC9D5DA871DB230B49AF3F1E7568AA19DE88B0";
	public String CityName;

	@Override
	public void onCreate() {
		super.onCreate();
		initEngineManager(this);
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		if (myListener != null) {
			mLocationClient.registerLocationListener(myListener);
		} // ע�������
		
		initCacheDirPath();
		getConfig();
	}
	public void initEngineManager(Context context) {
        if (mapManager == null) {
        	mapManager = new BMapManager(context);
        }

        if (!mapManager.init(strKey,null)) {
            Toast.makeText(getApplicationContext(), 
                    "初始化错误!", Toast.LENGTH_LONG).show();
        }
	}	
	
	@Override
	//建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		// TODO Auto-generated method stub
	    if (mapManager != null) {
	    	mapManager.destroy();
	    	mapManager = null;
        }
		if( (myListener != null) &&(mLocationClient != null)){
			mLocationClient.unRegisterLocationListener(myListener);
			myListener =null;
		} 
		
		super.onTerminate();
	}	

	private void getConfig() {
		weibotype = WPWeiBoAccessTokenKeeper.readWeibotype(this);
		switch (weibotype) {
		case WPMicroBlogConstant.SINA:
			weiboInstance = new WPSinaWeiBo();
			mbProxy = new WPMicroBlogProxy(weiboInstance);
			break;
		case WPMicroBlogConstant.TENCENT:
			weiboInstance = new WPTencenWeiBo();
			mbProxy = new WPMicroBlogProxy(weiboInstance);
			break;
		case WPMicroBlogConstant.UNKNOW:
			mbProxy = new WPMicroBlogProxy();
			break;
		}
	}

	public static String getCacheDirPath() {
		return cacheDir;
	}

	private void initCacheDirPath() {
		File f;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			f = new File(Environment.getExternalStorageDirectory()
					+ "/.waterpollution/");
			if (!f.exists()) {
				f.mkdir();
			}
		} else {
			f = getApplicationContext().getCacheDir();
		}
		cacheDir = f.getAbsolutePath();
	}

	public void addActvity(Activity activity) {
		records.add(activity);
		Logger.d(TAG, "Current Acitvity Size :" + getCurrentActivitySize());
	}

	public void removeActvity(Activity activity) {
		records.remove(activity);
		Logger.d(TAG, "Current Acitvity Size :" + getCurrentActivitySize());
	}

	public void exit() {
		for (Activity activity : records) {		
			activity.finish();
		}
	}

	public int getCurrentActivitySize() {
		return records.size();
	}

	public int getLeftButtonIndex() {
		return leftButtonIndex;
	}

	public void setLeftButtonIndex(int index) {
		this.leftButtonIndex = index;
	}
}
