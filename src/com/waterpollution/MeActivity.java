package com.waterpollution;

import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import com.waterpollution.adapter.MeAdapter;
import com.waterpollution.parser.ComplaintParser;
import com.waterpollution.util.AsyncImageLoader;
import com.waterpollution.util.AsyncImageLoader.ImageCallback;
import com.waterpollution.util.Constant;
import com.waterpollution.vo.ComplaintInfo;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MeActivity extends BaseActivity {
	private ImageView user_headicon;
	private TextView user_nick;
	public JSONObject userInfo;
	private List<ComplaintInfo> infoList;
	private AsyncImageLoader asyncImageLoader;
	
	private ListView lvInfoList;
	ProgressDialog mpDialog;

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.head_right:initView();
			break;			
		}
	}

	@Override
	protected void findViewById() {
		user_headicon = (ImageView) findViewById(R.id.user_headicon);
		user_nick = (TextView) findViewById(R.id.user_nick);	
		asyncImageLoader = new AsyncImageLoader();
		
		lvInfoList = (ListView)findViewById(R.id.listViewMeComplaintInfo);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_me);
		setHeadLeftVisibility(View.INVISIBLE);
		setHeadLeftBackgroundResource(R.drawable.backbtn);
		setHeadRightBackgroundResource(R.drawable.fresh1);	
		setHeadRightVisibility(View.VISIBLE);
		setTitle(R.string.titleMe);
		selectedBottomTab(Constant.ME);
		if(!application.mbProxy.isOauth()){
			setContentView(R.layout.activity_nouser_me);
			return;
		}
	}

	@Override
	protected void processLogic(){
		setHeadRightText("");
		if (application.mbProxy.isOauth()){
			mpDialog = new ProgressDialog(MeActivity.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
			mpDialog.setTitle(getString(R.string.loadTitle));//设置标题  
			mpDialog.setMessage(getString(R.string.LoadContent));  
			mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
			mpDialog.setCancelable(false);//设置进度条是否可以按退回键取消 
			mpDialog.show();  
			new Thread(){  
                public void run(){  
                    try{  
                    	userInfo = application.mbProxy.getUserInfo();
                    	getcomplaintInfolist();
                    	handler.sendEmptyMessage(0);
                    }catch(Exception ex){  
                    	ex.printStackTrace();
                    }  
                }  
            }.start();  
			
		}
	}
	
	private void getcomplaintInfolist(){
		String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=byuser&user_id="+application.mbProxy.getUserId();
		InputStream is = null;
		try {
			httpClient hc =new httpClient();
			is = hc.GetHttpData(httpcon, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ComplaintParser cr =new ComplaintParser();
		try {
			infoList = cr.parsecomplaintInfoByUid(is, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		mpDialog.dismiss();
	}

	@Override
	protected void setListener() {
		headRightBtn.setOnClickListener(this);		
	}
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if (userInfo==null){
				mpDialog.dismiss();
				return;
			}
			try {
				lvInfoList.setAdapter(new MeAdapter(MeActivity.this,infoList){});
				user_nick.setText(userInfo.getString("WPnick"));
				Drawable cachedImage;
				cachedImage = asyncImageLoader.loadDrawable(userInfo.getString("WPHeadUrl"),user_headicon, new ImageCallback(){	                
	                public void imageLoaded(Drawable imageDrawable,ImageView imageView, String imageUrl) {
	                    imageView.setImageDrawable(imageDrawable);
	                }
	            });
				if (cachedImage != null) {
					user_headicon.setImageDrawable(cachedImage);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	};

}
