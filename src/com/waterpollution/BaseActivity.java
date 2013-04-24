package com.waterpollution;


import java.util.List;
import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.webview.OAuthV2AuthorizeWebView;
import com.umeng.analytics.MobclickAgent;
import com.waterpollution.application.WPApplication;
import com.waterpollution.microblog.IMicroBlog;
import com.waterpollution.microblog.WPMicroBlogConstant;
import com.waterpollution.microblog.WPSinaWeiBo;
import com.waterpollution.microblog.WPTencenWeiBo;
import com.waterpollution.microblog.WPWeiBoAccessTokenKeeper;
import com.waterpollution.util.CommonUtil;
import com.waterpollution.util.Constant;
import com.waterpollution.util.Logger;
import com.waterpollution.util.NetUtil;
import com.waterpollution.util.ThreadPoolManager;
import com.waterpollution.vo.RequestVo;


public abstract class BaseActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "BaseActivity";
	public static final int NOT_LOGIN = 403;
	
	private ImageView home;
	private ImageView search;
	private ImageView ranking;
	private ImageView me;
	private ImageView setting;
	protected ProgressDialog progressDialog;
	protected Context context;
	private LinearLayout layout_content;

	/** ContentView */
	private View inflate;
	private LinearLayout head_layout; // TitleLayout
	protected TextView headLeftBtn;
	protected TextView headRightBtn;
	private Button btnStandard;
	private Button btnSatellite;
	private TextView head_title;
	private ButtomClick buttomClick;
	public WPApplication application;	
	private IMicroBlog weiboInstance;	
	private ThreadPoolManager threadPoolManager;
	private List<BaseTask> record = new Vector<BaseTask>();
	
	public BaseActivity() {
		threadPoolManager = ThreadPoolManager.getInstance();
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
		.build());
		
		application = (WPApplication) getApplication();
		application.addActvity(this);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(R.layout.frame);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.header);
		Resources res = getResources();
        Drawable drawable = res.getDrawable(R.color.clrGrey);
        this.getWindow().setBackgroundDrawable(drawable);
		buttomClick = new ButtomClick();
		layout_content = (LinearLayout) super.findViewById(R.id.frame_content);
		head_layout = (LinearLayout) super.findViewById(R.id.head_layout);
		head_title = (TextView) super.findViewById(R.id.head_title);
		headLeftBtn = (TextView) super.findViewById(R.id.head_left);
		headRightBtn = (TextView) super.findViewById(R.id.head_right);
		btnStandard = (Button)super.findViewById(R.id.btnStandard);
		btnSatellite = (Button)super.findViewById(R.id.btnSatellite);
		btnSatellite.setOnClickListener(buttomClick);
		btnStandard.setOnClickListener(buttomClick);
		headLeftBtn.setOnClickListener(buttomClick);
		headRightBtn.setOnClickListener(buttomClick);
		context = getApplicationContext();
 		initView();
 	}
	@Override
	protected void onPause(){

	        super.onPause();
	        MobclickAgent.onPause(this);        
	}
	@Override
	protected void onResume(){


	        super.onResume();
	        MobclickAgent.onResume(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
 		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}
	
	@Override
	public void setContentView(int layoutResID) {
		inflate = getLayoutInflater().inflate(layoutResID, null);
		setContentView(inflate);
	}

	public void setContentView(View view) {
		layout_content.removeAllViews();
		layout_content.addView(inflate);
	}

	@Override
	public View findViewById(int id) {
		return inflate.findViewById(id);
	}

	/**
	 * 
	 */
	public void initView() {		
		if (isLoadBottomTab()) {
			loadBottomTab();
			selectedBottomTab(Constant.defaultIndex);
		}
		userLogin();
		loadViewLayout();
		UpdateControlByUserState();
		if(application.mbProxy.isOauth()){
			findViewById();		
			setListener();
			processLogic();	
		}
	}
	
	private void userLogin(){
		if(application.weibotype!=WPMicroBlogConstant.UNKNOW){
			application.mbProxy.authorize(this);
		}
		UpdateControlByUserState();
	}

	@Override
	public void setTitle(CharSequence title) {
		head_title.setText(title);
	}

	@Override
	public void setTitle(int titleId) {
		head_title.setText(titleId);
	}

	/**
	 * 
	 */
	private void loadBottomTab() {
		ImageView localImageView1 = (ImageView) super.findViewById(R.id.imgHome);
		this.home = localImageView1;
		ImageView localImageView2 = (ImageView) super.findViewById(R.id.imgRanking);
		this.ranking = localImageView2;
		ImageView localImageView3 = (ImageView) super.findViewById(R.id.imgSearch);
		this.search = localImageView3;
		ImageView localImageView4 = (ImageView) super.findViewById(R.id.imgMe);
		this.me = localImageView4;
		ImageView localImageView5 = (ImageView) super.findViewById(R.id.imgSetting);
		this.setting = localImageView5;
		this.home.setOnClickListener(buttomClick);
		this.ranking.setOnClickListener(buttomClick);
		this.search.setOnClickListener(buttomClick);
		this.me.setOnClickListener(buttomClick);
		this.setting.setOnClickListener(buttomClick);
	}

	protected void selectedBottomTab(int paramViewId) {
		this.home.setBackgroundResource(R.drawable.bar_home_normal);
		this.search.setBackgroundResource(R.drawable.bar_search_normal);
		this.ranking.setBackgroundResource(R.drawable.bar_ranking_normal);
		this.me.setBackgroundResource(R.drawable.bar_me_normal);
		this.setting.setBackgroundResource(R.drawable.bar_setting_normal);
		setCenterButtonVisible(View.INVISIBLE);
		switch (paramViewId) {
		case Constant.HOME:
			this.home.setBackgroundResource(R.drawable.bar_home_selected);
			Constant.defaultIndex = Constant.HOME;
			if(this instanceof HomeActivity){
				setCenterButtonVisible(View.VISIBLE);
			}
			break;
		case Constant.SEARCH:
			this.search.setBackgroundResource(R.drawable.bar_search_selected);
			Constant.defaultIndex = Constant.SEARCH;
			break;
		case Constant.RANKING:
			this.ranking.setBackgroundResource(R.drawable.bar_ranking_selected);
			Constant.defaultIndex = Constant.RANKING;
			break;
		case Constant.ME:
			this.me.setBackgroundResource(R.drawable.bar_me_selected);
			Constant.defaultIndex = Constant.ME;
			break;
		case Constant.SETTING:
			this.setting.setBackgroundResource(R.drawable.bar_setting_selected);
			Constant.defaultIndex = Constant.SETTING;
			break;
		}
	}

	protected void setCenterButtonVisible(int visible){
		btnSatellite.setVisibility(visible);
		btnStandard.setVisibility(visible);
	}
	protected Boolean isLoadBottomTab() {
		return true;
	}

	
	protected void showProgressDialog() {
		if ((!isFinishing()) && (this.progressDialog == null)) {
			this.progressDialog = new ProgressDialog(this);
		}
		this.progressDialog.setTitle(getString(R.string.loadTitle));
		this.progressDialog.setMessage(getString(R.string.LoadContent));
		this.progressDialog.show();
	}

	
	protected void closeProgressDialog() {
		if (this.progressDialog != null)
			this.progressDialog.dismiss();
	}

	/**
	 * 
	 */
	protected abstract void findViewById();

	/**
	 * 
	 */
	protected abstract void loadViewLayout();

	
	protected abstract void processLogic();

	/**
	 * 
	 */
	protected abstract void setListener();

	private class ButtomClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgHome:
				startActivity(new Intent(BaseActivity.this, HomeActivity.class));
				break;
			case R.id.imgRanking:
				startActivity(new Intent(BaseActivity.this, RankingActivity.class));
				break;
			case R.id.imgSearch:
				startActivity(new Intent(BaseActivity.this, SearchmapActivity.class));
				break;
			case R.id.imgMe:
				startActivity(new Intent(BaseActivity.this, MeActivity.class));
				break;
			case R.id.imgSetting:
				startActivity(new Intent(BaseActivity.this, SettingActivity.class));
				break;
			case R.id.head_left:
				onHeadLeftButton(v);
				break;
			case R.id.head_right:
				onHeadRightButton(v);
				break;
			case R.id.btnStandard:
				onHeadStandardButton(v);
				break;
			case R.id.btnSatellite:
				onHeadSatelliteButton(v);
				break;
			}
		}
	}

	protected void onHeadLeftButton(View v) {
		if(!application.mbProxy.isOauth()){
			popupLogin();
			return;
		}
		switch (application.getLeftButtonIndex()) {
		case 0:
			startActivity(new Intent(BaseActivity.this, ListModeActivity.class));
			application.setLeftButtonIndex(Constant.LeftButton.MAP_MODE);
			break;
		case 1:
			startActivity(new Intent(BaseActivity.this, HomeActivity.class));
			application.setLeftButtonIndex(Constant.LeftButton.LIST_MODE);
			break;
		default:
			finish();
		}
	}

	protected void onHeadSatelliteButton(View v) {
		btnStandard.setBackgroundResource(R.drawable.standard);
		btnSatellite.setBackgroundResource(R.drawable.satellitepressed);
	}

	protected void onHeadStandardButton(View v) {
		btnStandard.setBackgroundResource(R.drawable.standardpressed);
		btnSatellite.setBackgroundResource(R.drawable.satellite);
	}

	protected void onHeadRightButton(View v) {
		if(!application.mbProxy.isOauth()){
			popupLogin();
			return;
		}
		if (headRightBtn.getText()==this.getString(R.string.navLogin)){
			popupLogin();
		}
		else{
			userLogOut();
		}		
	}
	
	public void popupLogin(){
		new AlertDialog.Builder(this) 
        .setTitle(getString(R.string.loginTitle))
        .setItems(getResources().getStringArray(R.array.listloginPopup), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	 switch (which) {
                 case 0:
                	 weiboInstance = new WPSinaWeiBo();
                     break;
                 case 1:
                	 weiboInstance = new WPTencenWeiBo();
                     break;
                 }
            	 application.mbProxy.setMicroBlog(weiboInstance);
            	 application.mbProxy.authorize(BaseActivity.this);
            }})       	
        .setNegativeButton("取消", null)  
        .show();
	}
	
	public void UpdateControlByUserState(){
		if (application.mbProxy.isOauth()){
			headRightBtn.setText(this.getString(R.string.navLogout));
			
		}else{
			headRightBtn.setText(this.getString(R.string.navLogin));
		}
	}

	private void userLogOut(){		
		application.mbProxy.logOut();
		Toast.makeText(this, "注销成功", Toast.LENGTH_SHORT).show();
		UpdateControlByUserState();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode==2) {
    		//腾讯微博授权
            if (resultCode==OAuthV2AuthorizeWebView.RESULT_CODE)    {
                OAuthV2 oAuth=(OAuthV2) data.getExtras().getSerializable("oauth");
                if(oAuth != null && oAuth.getStatus()==0 && !oAuth.getAccessToken().equals("")){
	                WPWeiBoAccessTokenKeeper.keepAccessToken(this,oAuth);
	                WPWeiBoAccessTokenKeeper.KeepWeibotype(this, WPMicroBlogConstant.TENCENT);
	                Toast.makeText(this, "认证成功", Toast.LENGTH_SHORT).show();
	                UpdateControlByUserState();
	                initView();
                }
            }
    	}
    }
	
	protected void setHeadLeftText(CharSequence text) {
		headLeftBtn.setText(text);
	}

	protected void setHeadLeftText(int resid) {
		headLeftBtn.setText(resid);
	}

	protected void setHeadLeftBackgroundResource(int resid) {
		headLeftBtn.setBackgroundResource(resid);
	}

	protected void setHeadLeftVisibility(int visibility) {
		headLeftBtn.setVisibility(visibility);
	}

	protected void setHeadRightText(CharSequence text) {
		headRightBtn.setText(text);
	}

	protected void setHeadRightText(int resid) {
		headRightBtn.setText(resid);
	}

	protected void setHeadRightBackgroundResource(int resid) {
		headRightBtn.setBackgroundResource(resid);
	}

	protected void setHeadRightVisibility(int visibility) {
		headRightBtn.setVisibility(visibility);
	}

	protected void setHeadBackgroundResource(int resid) {
		head_layout.setBackgroundResource(resid);
	}

	@SuppressWarnings("deprecation")
	protected void BackgroundDrawable(Drawable d) {
		head_layout.setBackgroundDrawable(d);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		application.removeActvity(this);
		ranking = null;
		home = null;
		setting = null;
		search = null;
		me = null;
		context = null;
		layout_content = null;
		inflate = null;
		head_layout = null;
		headLeftBtn = null;
		headRightBtn = null;
		head_title = null;
		buttomClick = null;
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		application = null;
	}
	
	public abstract interface DataCallback<T> {
		public abstract void processData(T paramObject, boolean paramBoolean);
	}
	
	class BaseHandler extends Handler {
 		private Context context;
		private DataCallback callBack;
		private RequestVo reqVo;

		public BaseHandler(Context context, DataCallback callBack, RequestVo reqVo) {
			this.context = context;
			this.callBack = callBack;
			this.reqVo = reqVo;
		}

		public void handleMessage(Message msg) {
			closeProgressDialog();
			if (msg.what == Constant.SUCCESS) {
				if (msg.obj == null) {
					CommonUtil.showInfoDialog(context, getString(R.string.net_error));
				} else {
					callBack.processData(msg.obj, true);
				}
			} else if (msg.what == Constant.NET_FAILED) {
				CommonUtil.showInfoDialog(context, getString(R.string.net_error));
			}
			
			Logger.d(TAG, "recordSize:" + record.size());
		}
	}

	class BaseTask implements Runnable {
		private Context context;
		private RequestVo reqVo;
		private Handler handler;

		public BaseTask(Context context, RequestVo reqVo, Handler handler) {
			this.context = context;
			this.reqVo = reqVo;
			this.handler = handler;
		}

		@Override
		public void run() {
			Object obj = null;
			Message msg = Message.obtain();
			try {
				if (NetUtil.hasNetwork(context)) {
					if(reqVo.method.equals(RequestVo.METHOD_GET)){
						obj = NetUtil.get(reqVo);
					}else{
						obj = NetUtil.post(reqVo);
					}
					if (obj instanceof Status) {
						Intent intent = new Intent(BaseActivity.this, HomeActivity.class);
						intent.putExtra("notlogin", "notlogin");
						startActivityForResult(intent, NOT_LOGIN);
					} else {
						msg.what = Constant.SUCCESS;
						msg.obj = obj;
						handler.sendMessage(msg);
						record.remove(this);
					}
				} else {
					msg.what = Constant.NET_FAILED;
					msg.obj = obj;
					handler.sendMessage(msg);
					record.remove(this);
				}
			} catch (Exception e) {
				record.remove(this);
				throw new RuntimeException(e);
			}
		}

	}
	
	protected void getDataFromServer(RequestVo reqVo, DataCallback<?> callBack) {
		showProgressDialog();
		BaseHandler handler = new BaseHandler(this, callBack, reqVo);
		BaseTask taskThread = new BaseTask(this, reqVo, handler);
		record.add(taskThread);
		this.threadPoolManager.addTask(taskThread);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (this instanceof HomeActivity) {
				exit();
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		AlertDialog.Builder build=new AlertDialog.Builder(this);
		build.setTitle("注意")
				.setMessage("您确定要退出吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						application.exit();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
	}
}
