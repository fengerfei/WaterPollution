package com.waterpollution;

import com.baidu.mapapi.map.MapView;
import com.waterpollution.application.WPApplication;
import com.waterpollution.maps.ChangeActListener;
import com.waterpollution.maps.MyMapManager;
import com.waterpollution.util.Constant;
import com.waterpollution.vo.ComplaintEntity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchmapActivity extends BaseActivity  implements OnDoubleTapListener, OnGestureListener{
	//地图相关
	private GestureDetector gestureScanner = new GestureDetector(this);

	MyMapManager myMapManage = null;	
	private EditText editSearchName ;
	private Button  btnSearch  ;
	
//	@Override
//	public void onCreate(Bundle paramBundle) {
//		myMapManage = new MyMapManager( (WPApplication)this.getApplication(),getApplication(),SearchmapActivity.this);
//
//		super.onCreate(paramBundle);
//
//        //注意：请在试用setContentView前初始化BMapManager对象，否则会报错
//		MapView mMapView = null;		
//		mMapView=(MapView)findViewById(R.id.bmapsView);
//		myMapManage.SetMapView(mMapView,new  OnChangeActListenner());
//        myMapManage.inidata(null,false,null);
//        //根据传入值进行定位
//        
//        
//	}	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnSearch:
				String addr = editSearchName.getText().toString();
				if (!addr.equals("")){
					myMapManage.AddressSearch(addr);
				}
				break;
			}
		}

		@Override
		protected void findViewById() {
			  editSearchName = (EditText)findViewById(R.id.editSearchName);
			   btnSearch  = (Button)findViewById(R.id.btnSearch);				
			// TODO Auto-generated method stub

		}
		public class OnChangeActListenner implements ChangeActListener {

			@Override
			public void ChangeAct(int Type) {
				if (myMapManage.complaintinfo==null) return;
				 Intent nt = new Intent(SearchmapActivity.this, NewComplaintActivity.class);
				 Bundle bundle = new Bundle();
				 ComplaintEntity info = myMapManage.complaintinfo;
				 bundle.putString("address",info.address);
				 bundle.putFloat("Longitude",info.longitude);
				 bundle.putFloat("Latitude",info.longitude);
				 bundle.putString("city_name",info.city_name);
				 nt.putExtras(bundle);
				 SearchmapActivity.this.startActivity(nt);
			}
			
		}		

		@Override
		protected void loadViewLayout() {
			if(!application.mbProxy.isOauth()){
				setContentView(R.layout.activity_search);
				setTitle(R.string.titleSearch);				
				setHeadLeftVisibility(View.INVISIBLE);
				setHeadRightVisibility(View.INVISIBLE);			
				selectedBottomTab(Constant.SEARCH);				
				return;
			}			
			myMapManage = new MyMapManager( (WPApplication)this.getApplication(),getApplication(),SearchmapActivity.this);
	        //注意：请在试用setContentView前初始化BMapManager对象，否则会报错

			setContentView(R.layout.activity_searchmap);

			setTitle(R.string.titleSearch);
			setHeadLeftVisibility(View.INVISIBLE);
			setHeadRightVisibility(View.INVISIBLE);
			selectedBottomTab(Constant.SEARCH);
			

			MapView mMapView = null;		
			mMapView=(MapView)findViewById(R.id.bmapsView);
			myMapManage.SetMapView(mMapView,new  OnChangeActListenner());
	        myMapManage.inidata(null,false,null);
	        myMapManage.AddNodeSearch(0);
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
		@Override
		protected void processLogic() {
			// TODO Auto-generated method stub

		}

		@Override
		protected void setListener() {
			// TODO Auto-generated method stub
			btnSearch.setOnClickListener(this);
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
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
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
