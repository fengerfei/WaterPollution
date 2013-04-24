package com.waterpollution.util;

import com.waterpollution.R;

import android.content.res.Resources;
import android.os.Environment;

public class Constant {
	
	public static final int HOME = 1;
	public static final int SEARCH = 2;
	public static final int RANKING = 3;
	public static final int ME = 4;
	public static final int SETTING = 5;
	public static final String SDPATH = Environment.getExternalStorageDirectory().getPath();
	public static int defaultIndex = 1;	
	
	public final static int FAILED = 1;
	public final static int SUCCESS = 1;
	public final static int NET_FAILED = 2;
	public final static int TIME_OUT = 3;
	
	public static final class LeftButton{
		public static final int LIST_MODE = 0;
		public static final int MAP_MODE = 1;
		public static final int BACK = 2;
	}
	
	public static final class URLResource{
		public static final String URL_SEARCH_BY_RANGE = 
				Resources.getSystem().getString(R.string.app_host)
				+"/Iphone1.2/index.php?m=compliant&a=bylatlong" +
				"&longitude=${longitude}&latitude=${latitude}&range=${range}";
	}
}