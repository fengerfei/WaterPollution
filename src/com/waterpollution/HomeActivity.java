package com.waterpollution;

import com.waterpollution.maps.ChangeActListener;
import com.waterpollution.maps.MyMapManager;
import com.waterpollution.util.Constant;
import com.waterpollution.vo.ComplaintEntity;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

import com.waterpollution.application.WPApplication;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.mapapi.map.MapView;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends BaseActivity implements OnDoubleTapListener, OnGestureListener{
	//地图相关
	private GestureDetector gestureScanner = new GestureDetector(this);

	MyMapManager myMapManage = null;	
 
    //加功能点
	float x = 0;
	float y = 0;
	protected long time;	
	protected boolean isLongPress = false;	
	protected boolean useLongPress = false;
	public static final int LONGPRESSINTERVAL = 1000;
    public TextView textsaynosum = null;
    public TextView textcontent = null;
    public TextView textclose = null;
    public TextView textcllocation = null;
    ProgressDialog mpDialog;
    public OnLongClickListener longClcikListener = new OnLongClickListener(){
    	
		@Override
		public boolean onLongClick(View v) {
						
			if (!isLongPress && useLongPress){
				isLongPress = true;
				useLongPress = false;
				myMapManage.ShowFlag(HomeActivity.this,(int) x, (int) y,new OnChangeActListenner());
			}
			return false;
		}

    };

	public OnTouchListener TouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				x = event.getX();
				y = event.getY();
			   //				time = System.currentTimeMillis();
				isLongPress = false;
				useLongPress = true;
				gestureScanner.onTouchEvent(event);
				return false;
				//break;
			case MotionEvent.ACTION_UP:
				if (isLongPress){
					myMapManage.olp.ShowNewNodePop();
					isLongPress = false;
					useLongPress = false;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float x0 = event.getX();
				float y0 = event.getY();
				float xx =Math.abs(x-x0)+Math.abs(y-y0);
				if (xx >2){
					useLongPress = false;
				}
				break;
			default:
				break;
			}
			return gestureScanner.onTouchEvent(event);
		}
	};	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textclose:
			textclose.setVisibility(View.INVISIBLE);
			textcontent.setVisibility(View.INVISIBLE);
			break;
		case R.id.textcontent:
			textclose.setVisibility(View.INVISIBLE);
			textcontent.setVisibility(View.INVISIBLE);
			break;			
		case R.id.textcllocation:
			myMapManage.MoveToLocData();
			break;
		}		
	}

	@Override
	protected void findViewById() {
	    textsaynosum = (TextView)super.findViewById(R.id.textsaynosum);
	    textcontent  = (TextView)super.findViewById(R.id.textcontent);
	    textclose = (TextView)super.findViewById(R.id.textclose);
	    textcllocation  = (TextView)super.findViewById(R.id.textcllocation);		
	}
	@Override
	protected void processLogic() {
		if(application.mbProxy.isOauth()){
			MapView mMapView = null;		
			mMapView=(MapView)findViewById(R.id.bmapsView);
			myMapManage.tvCount = textsaynosum;
			myMapManage.SetMapView(mMapView,new  OnChangeActListenner());
			
	        //myMapManage.inidata(TouchListener,true,longClcikListener);
		
			
				mpDialog = new ProgressDialog(HomeActivity.this);
				mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
				mpDialog.setTitle(getString(R.string.loadTitle));//设置标题  
				mpDialog.setMessage(getString(R.string.LoadContent));  
				mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
				mpDialog.setCancelable(false);//设置进度条是否可以按退回键取消 
				mpDialog.show();  
				new Thread(){  
	                public void run(){  
	                	try{
	                		
	                	
	                    try{  
	                    	myMapManage.inidata(TouchListener,true,longClcikListener);

	                    }catch(Exception ex){  
	                    	ex.printStackTrace();
	                    }
	                	}
	                	finally{
	                    	mpDialog.dismiss();	                		
	                	}
	                }  
	            }.start();  
	            
	            myMapManage.AddNodeSearch(1);
			}			
	
	}
	@Override	
	protected void onHeadSatelliteButton(View v) {
		if(!application.mbProxy.isOauth()){
			popupLogin();
			return;
		}
		super.onHeadSatelliteButton(v);
		myMapManage.ShowSatellite(true);
	}
	@Override
	protected void onHeadStandardButton(View v) {
		if(!application.mbProxy.isOauth()){
			popupLogin();
			return;
		}
		super.onHeadStandardButton(v);
		myMapManage.ShowSatellite(false);
	}
	@Override
	protected void setListener() {
		textclose.setOnClickListener(this);
		textcllocation.setOnClickListener(this);		
		textcontent.setOnClickListener(this);
	}

	@Override
	protected void loadViewLayout() {
		if(!application.mbProxy.isOauth()){
			setHeadRightText(getString(R.string.navLogin));
			selectedBottomTab(Constant.HOME);
		setContentView(R.layout.activity_nouser_home);
		setCenterButtonVisible(View.INVISIBLE);
		return;
	}		
		myMapManage = new MyMapManager( (WPApplication)this.getApplication(),getApplication(),HomeActivity.this);
		//注意：请在试用setContentView前初始化BMapManager对象，否则会报错
		
		setContentView(R.layout.activity_home);
		setHeadLeftVisibility(View.VISIBLE);
		application.setLeftButtonIndex(Constant.LeftButton.LIST_MODE);
		setHeadRightText(getString(R.string.navLogin));
		selectedBottomTab(Constant.HOME);

		
	}
	
	@Override
	protected void onDestroy(){
			if(myMapManage != null)
	        myMapManage.Destroy();

	        super.onDestroy();
	}
	@Override
	protected void onPause(){
		if(myMapManage != null)
	        myMapManage.stop();
	        super.onPause();
	}
	@Override
	protected void onResume(){
		if(myMapManage != null)
	        myMapManage.start();

	        super.onResume();
	}

	
	public class OnChangeActListenner implements ChangeActListener {

		@Override
		public void ChangeAct(int Type) {
			 Intent nt = new Intent(HomeActivity.this, NewComplaintActivity.class);
			 Bundle bundle = new Bundle();
			 ComplaintEntity info = myMapManage.complaintinfo;
			 bundle.putString("address",info.address);
			 bundle.putFloat("Longitude",info.longitude);
			 bundle.putFloat("Latitude",info.latitude);
			 bundle.putString("city_name",info.city_name);
			 nt.putExtras(bundle);
			//HomeActivity.this.startActivity(nt);
			 HomeActivity.this.startActivityForResult(nt, 1);
			// TODO Auto-generated method stub			
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if  (data == null) return;
		boolean result = data.getExtras().getBoolean("regetround");
		 if (result){
			 //重新刷新
			 myMapManage.ReGetRound();
		 }
		 
		 
	}
    
    public class NotifyLister extends BDNotifyListener{
        public void onNotify(BDLocation mlocation, float distance) {
        }
    }
    
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
