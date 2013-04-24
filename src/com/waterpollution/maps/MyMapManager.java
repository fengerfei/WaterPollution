package com.waterpollution.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.waterpollution.ComplaintDetailActivity;
import com.waterpollution.R;
import com.waterpollution.application.WPApplication;
import com.waterpollution.util.ImageUtil;
import com.waterpollution.vo.ComplaintEntity;


//我们的地图管理类用于将地图管理从界面剥离----

public class MyMapManager {
	BMapManager mBMapMan = null;
	MapView mMapView  = null;
	private MapController mMapController =null;
	private Drawable da = null;
	private Drawable newnode = null;	
	private Context context;
	private Context actConText;
	private NodeSearch nodesearch;
    // 定位相关
	WPApplication wpapp;
	LocationClient mLocClient;	
    MyLocationOverlay myLocationOverlay = null;   //定位图层
    public LocationData locData = null;				//定位数据�?
    public GraphicsOverlay graphicsOverlay = null;
	public OverlayListPos olp = null;			  //范围点图�?
	public ChangeActListener actListenner = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	public ComplaintEntity complaintinfo =null;
	public TextView tvCount = null;
	public List<ComplaintEntity> complaintEntitylist =null;
	//定位修正处理 ---原则上是根据GSP定位和百度的定位差来�?
	
	
	public MyMapManager(WPApplication app,Context con,Context actCon){
		context =con;
		actConText = actCon;
		 wpapp =app;
	        if (wpapp.mapManager == null) {
	        	app.initEngineManager(con);
	        }
    		mBMapMan =app.mapManager;	        
		
	}
	public void start(){
		if (mBMapMan != null){
			mBMapMan.start();
		}
		
		if (nodesearch != null){
			nodesearch.ReInit();
		}
		if (complaintEntitylist != null){
			if (wpapp != null){
				wpapp.complaintEntitylist = complaintEntitylist;
			}
		}
        mMapView.onResume();
	}
	public void stop(){
        mMapView.onPause();
        if (mLocClient != null){
        	mLocClient.stop();
        }
		if (mBMapMan != null){
			mBMapMan.stop();
		}
	}
	
	public void ReGetRound(){
		if (mMapView == null) return;
        GetRoundList(locData.latitude,locData.longitude,null,0);
        mMapView.refresh();
        MoveToLocData();		
	}
	public void ShowSatellite(Boolean show){
		if (mMapView == null) return;
		mMapView.setSatellite(show);
	}
	public void SetMapView(MapView map,ChangeActListener Listenner){
		mMapView =map;
		actListenner =Listenner;
		mMapView.setBuiltInZoomControls(true);
		mMapController  = mMapView.getController();
		da =  context.getResources().getDrawable(R.drawable.icon_marka);
		newnode = context.getResources().getDrawable(R.drawable.icon_marka_green);
        //设置启用内置的缩放控�?-------
		MapController mMapController=mMapView.getController();

	}
	private void iniLocation(){
		if (mMapView == null) return;
	       mLocClient = wpapp.mLocationClient;
	        myListener = new MyLocationListenner();
	        
	        mLocClient.registerLocationListener( myListener );
	        
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);
	        option.setAddrType( "all" );//返回的定位结果包含地址信息
	        option.setCoorType( "bd09ll" );//返回的定位结果是百度经纬�?默认值gcj02
	        option.setScanSpan(5000); //设置发起定位请求的间隔时间为5000ms
	        option.disableCache( true );//禁止启用缓存定位
	        option.setPoiNumber(5);      //最多返回POI个数 
	        option.setPoiDistance(1000); //poi 查询距离          
	        option.setPoiExtraInfo( true ); //是否需要POI的电话和地址等详细信�?   
		
	        mLocClient.setLocOption(option);
	        mLocClient.start();
			
	        if (mLocClient != null && mLocClient.isStarted())
	        	mLocClient.requestLocation();
	        else 
	        	Log.d("LocSDK3", "locClient is null or not started");
	        
	        
	        myLocationOverlay = new MyLocationOverlay(mMapView);
			locData = new LocationData();
		    myLocationOverlay.setData(locData);
	        
			mMapView.getOverlays().add(myLocationOverlay);
		    graphicsOverlay = new GraphicsOverlay(mMapView);
	        mMapView.getOverlays().add(graphicsOverlay);

			myLocationOverlay.enableCompass();
			mMapView.refresh();		
	}
	public void inidata(OnTouchListener TouchListener,boolean Location,OnLongClickListener longListener ){
		if (mMapView == null) return;
		int type =0;
		if (Location){
			iniLocation();
			type =1;
		}
 
		
		mMapView.setOnTouchListener(TouchListener);        
		if (longListener!= null){
			mMapView.setLongClickable(true);
			mMapView.setOnLongClickListener(longListener);
		}
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
       GeoPoint point = new GeoPoint(( int)(30.545651* 1E6),(int )(104.081123* 1E6));
        //用给定的经纬度构造一个GeoPoint，单位是微度 (�?* 1E6)
       mMapController.setCenter(point); //设置地图中心�?
       mMapController.setZoom(14); //设置地图zoom级别 	
       //AddNodeSearch();
       
	}
	public void AddNodeSearch(int type){
		nodesearch = new NodeSearch(type);
	}
	public void AddressSearch(String Addr){
		nodesearch.AddrSearch(Addr);
	}
	
    public void ShowFlag(Context  context,int x2, int y2,ChangeActListener Listener) {
		// TODO Auto-generated method stub
		OverlayListPos ListPos = null;
		//Toast.makeText(context,"msg:x2" +x2+"y2"+y2, Toast.LENGTH_SHORT).show();
		
		
		//((HomemapActivity)context).mMapView.getOverlays().clear();
		Drawable marker = newnode;	
		if (olp == null)
		{
			ListPos =new OverlayListPos(marker, context,Listener);
			olp = ListPos;
		    mMapView.getOverlays().add(olp);			
		}
		else{
			ListPos =olp;
		}
			
		
	    GeoPoint p = mMapView.getProjection().fromPixels(x2, y2);
	    LocationData pcenter = locData;
	    GeoPoint p2 = new GeoPoint((int)(pcenter.latitude*1E6), (int)(pcenter.longitude*1E6));
	    //判断是否超出
	    double line =DistanceUtil.getDistance(p, p2);
	    if (line >2000){
	    	Toast.makeText(context,"举报点位置超出了圆圈范围，请您重新选择。", Toast.LENGTH_SHORT).show();
	    	return;
	    }
	    if (complaintinfo==null){
	    	complaintinfo = new ComplaintEntity();
	    }
		complaintinfo.latitude =(float)(p.getLatitudeE6()/1E6);
		complaintinfo.longitude =(float)(p.getLongitudeE6()/1E6);
		
		
	   	nodesearch.GetInfo(p,olp);
	}
    
	static public double getDistance(double longt1, double lat1, double longt2,double lat2) {
		double PI = Math.PI; // 圆周�?
		double R = 6371.229; // 地球的半�?
		/**
		longitude   经度
		latitude 维度
		*/

		double x, y, distance;
	        x = (longt2 - longt1)*PI*R*Math.cos(((lat1+lat2)/2)*PI/180)/180;
	        y = (lat2 - lat1)*PI*R/180;
	        distance = Math.hypot(x, y);
	        return distance;
	    }	
	
	   
	public void GetRoundList(double latitude ,double longitude, MapOffset offset,int MewRange){
		if (olp == null)
		{
			olp =new OverlayListPos(da, actConText,actListenner);
		    mMapView.getOverlays().add(olp);			
		}
		
		olp.clearItem();
		int oldRage=0; 
		if (MewRange !=0){
			oldRage = wpapp.mapRoundList.range;
			wpapp.mapRoundList.range =MewRange; 
		}
		
		
		wpapp.mapRoundList.GetRoundList(latitude, longitude);
		
		if (MewRange !=0){
			wpapp.mapRoundList.range =oldRage;
		}
		
		ComplaintEntity complaintE;
		if (wpapp.complaintEntitylist ==null){
			return;
		}
		complaintEntitylist =wpapp.complaintEntitylist; 
		for (int i=0;i< wpapp.complaintEntitylist.size();i++)
		{
			complaintE=wpapp.complaintEntitylist.get(i);
			if (offset!=null){
				complaintE.latitude = (float)offset.GSPtoBDLatitude(complaintE.latitude);
				complaintE.longitude = (float)offset.GSPtoBDLatitude(complaintE.longitude);
				
			}
			
			GeoPoint point = new GeoPoint(( int)(complaintE.latitude* 1E6),(int )(complaintE.longitude* 1E6));
			
			OverlayItem item = new OverlayItem(point,complaintE.title,complaintE.address+";"+complaintE.count);
			item.setMarker(da);
			olp.addItem(item);
		}
		if (tvCount != null){
			String scount = Integer.toString(wpapp.complaintEntitylist.size());
			tvCount.setText(scount );
		}
	   // mMapView.refresh();				
		
	}

    public class OverlayListPos extends ItemizedOverlay<OverlayItem> {
        public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
        public List<Bitmap> mapList = new ArrayList<Bitmap>();
        Bitmap amap = null;
        Bitmap cmap = null;
        Bitmap aminmap = null;
        
        private Context mContext;
         PopupOverlay pop = null;
        private OverlayItem CurrItem = null;
        private OverlayItem newnode = null;
        public Activity CurrAct = null;
        
        private ChangeActListener changeactlistener =null;
        
        Toast mToast = null;
        
        public OverlayListPos(Drawable marker,Context context,ChangeActListener listener){
            super(marker);
            this.mContext = context; 

            changeactlistener = listener;
            try {
            amap = BitmapFactory.decodeStream(context.getAssets().open("Bmarker11.png")).copy(Bitmap.Config.ARGB_8888, true);
            aminmap =BitmapFactory.decodeStream(context.getAssets().open("Bmarker12.png")).copy(Bitmap.Config.ARGB_8888, true);
            cmap = BitmapFactory.decodeStream(context.getAssets().open("Bmarker13.png")).copy(Bitmap.Config.ARGB_8888, true);        	
            } catch (IOException e) {
                e.printStackTrace();
            }

            pop = new PopupOverlay( mMapView,new PopupClickListener() {
                
                @Override
                public void onClickedPopup(int index) {
                    if (index==0){
                    	if (CurrItem == newnode){
                    		pop.hidePop();                        	
                    		olp.clearnewnode();
                    		mMapView.refresh();		
                    	}
        			    
                    	CurrItem =null;
                    }else if (index==2){
                    	pop.hidePop();
                    	//打开新的录入窗口
                    	if (CurrItem == newnode){

                    		changeactlistener.ChangeAct(1);
                    		
                    	}else{
                    		
                    		//举报详情还未�?
                    		Intent ent =new Intent(mContext, ComplaintDetailActivity.class);
                    		Bundle bundle = new Bundle();
               			 	bundle.putString("title",CurrItem.getTitle());               			 
               			 	ent.putExtras(bundle);                    		
               			 mContext.startActivity(ent);
                    		
                    		//mContext.startActivity(new Intent(mContext, NewComplaintAcivity.class));
                    	}
                    	
                    	
                    	
                    }
                    	
                }
            });          
            populate();
            
        }   
        protected Bitmap changeSize(Bitmap map,float size){
            //按比例缩�?
        	
            int w =map.getWidth();
            int h =map.getHeight();
            Matrix matrix = new Matrix();   //矩阵，用于图片比例缩�? 
            matrix.postScale((float)w*2, (float)h*2);    //设置高宽比例（三维矩阵）  
            return Bitmap.createBitmap(map, 0, 0, w, h, matrix, false); 

        }
        public void ShowNewNodePop(){
        	if (newnode ==null) return;
        	int index = mGeoList.indexOf(newnode);
        	if (index <0) return;
        	onTap(index);
        }
        protected boolean onTap(int index) {
            //Toast.makeText(mContext,"PoP", Toast.LENGTH_SHORT).show();
        	 Bitmap[] bmps = new Bitmap[3];
             CurrItem =mGeoList.get(index);
             	if (CurrItem==newnode){
             	bmps[0] =amap;
             	}
             	else{
             		bmps[0] =aminmap;
             	}
             		
             	//b =changeSize(b,2);
             	bmps[1] =mapList.get(index);//changeSize(b,2); //ImageUtil.TextToBmp(b,CurrItem.getTitle(),CurrItem.getSnippet());//changeSize(b,1);
             	bmps[2] =cmap;//changeSize(c,2);             	
                pop.showPopup(bmps, CurrItem.getPoint(), 45);
             return true;        	
        }
        public boolean onTap(GeoPoint pt, MapView mapView){
            if (pop != null){
                pop.hidePop();              
                CurrItem =null;
            }
            super.onTap(pt,mapView);
            return false;
        }        
        public void addItem(OverlayItem item){
        	addItem(item,false);
        }
        public void addItem(OverlayItem item,boolean isnewnode){
            mGeoList.add(item);
            if (isnewnode){
            	newnode = item;
            }
			try {
				
				Bitmap bmap = BitmapFactory.decodeStream(mContext.getAssets().open("Bmarker4.png")).copy(Bitmap.Config.ARGB_8888, true);
	            bmap =ImageUtil.TextToBmp(bmap,item.getTitle(),item.getSnippet());
	            mapList.add(bmap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            populate();
        }
        public void clearItem(){
            mGeoList.clear();
            newnode =null;
            mapList.clear();
            populate();
        }        
        public void removeItem(int index){
        	OverlayItem r= mGeoList.remove(index);
        	mapList.remove(index);
        	if (r==newnode){
        		newnode =null;
        	}
            populate();
        }
        public void clearnewnode(){
        	if (newnode == null) return;
        	int i =mGeoList.indexOf(newnode);
        	mGeoList.remove(i);
        	mapList.remove(i);
        	newnode =null;
        	populate();
        }

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return mGeoList.get(arg0);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}        
   
    }     
    public class DrawCircle{
    	public DrawCircle(double latitude,double longitude,GraphicsOverlay nOverlay){
        	int lat = (int) (latitude*1E6);
    	   	int lon = (int) (longitude*1E6); 
    	   	
    	   	GeoPoint pt1 = new GeoPoint(lat, lon);
    	  //构建点并显示
      		Geometry circleGeometry = new Geometry();
      	
      		circleGeometry.setCircle(pt1, 2200);    	   	
    	   	
      		Symbol circleSymbol = new Symbol();
     		Symbol.Color circleColor = circleSymbol.new Color();
     		circleColor.red = 128;
     		circleColor.green = 128;
     		circleColor.blue = 255;
     		circleColor.alpha = 64;
      		circleSymbol.setSurface(circleColor,1,3);
      		
      		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
      		if (nOverlay==null){
      			
      		}
      			
      		nOverlay.removeAll();	
      		nOverlay.setData(circleGraphic);   		
    	}
    }  
	public void Destroy(){
        if (mLocClient != null)
            mLocClient.stop();	
        if (myListener != null){
        	try{
        		try {
                	mLocClient.unRegisterLocationListener(myListener);
					
				} catch (Exception e) {
					// TODO: handle exception
				}
        		
        	}finally{
            	myListener =null;
        		
        	}
        	
        }
	     mMapView.destroy();
	}
	
	
	/**
	 * 定义地址查询�?
	 * 
	 */
	public class NodeSearch{
		private MKSearch mSearch;
		private MKSearchListener listener;
		private OverlayListPos currolp;
		private GeoPoint p;
		public NodeSearch(int type){
	        mSearch = new MKSearch();
	        if (type ==1){
	        	listener =new NodeSearchListener();
	        }
	        else {
	        	listener =new AddressSearchListener();
	        }
	        
	        mSearch.init(mBMapMan, listener);

		}
		public void ReInit(){
			//重新注册，也就是抢占mBMapMan中的监听
			mSearch.init(mBMapMan, listener);
		}
		public void AddrSearch(String Addr){
			mSearch.suggestionSearch(Addr);
		}
		public void GetInfo(GeoPoint ptCenter,OverlayListPos olp){
			currolp = olp;
			p = ptCenter;
			int r =mSearch.reverseGeocode(ptCenter);			
			if (r>0){
				r= r-1;
			}
		}
		public class CommListener implements MKSearchListener{

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub					
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
					int arg1) {
				// TODO Auto-generated method stub
				
			}
			
		}
		
		public class AddressSearchListener extends CommListener{
			MKSuggestionResult MKres = null;
			int rescount =0;
			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub
				if (arg1 != 0) {
					if (MKres.getSuggestionNum() <=rescount) {
						 MKres = null;
						 rescount =0;
						return;
					}
					String city =MKres.getSuggestion(rescount).city;
					String key =MKres.getSuggestion(rescount).key;
					rescount++;
					mSearch.geocode(key, city);					
					return;
				}
			    if (arg0==null){
			    	return;
			    }
			    MKres = null;
			    rescount =0;
			    double  latitude = arg0.geoPt.getLatitudeE6()/1E6;
			    double longitude = arg0.geoPt.getLongitudeE6()/1E6;
			    
			    //根据点取范围50公里
			    GetRoundList(latitude,longitude,null,20000);
			    mMapController.setZoom(12); 			
			    mMapController.animateTo(new GeoPoint((int)(latitude* 1e6), (int)(longitude *  1e6)), mHandler.obtainMessage(1));
	               
			   mMapView.refresh();						
			}
			
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				// TODO Auto-generated method stub
				MKres =null;
				if (arg1 != 0 || res == null) {
                    return;
                }
				MKres = res;
				rescount =0;
				
				String city =res.getSuggestion(0).city;
				String key =res.getSuggestion(0).key;
//				if (!city.equals("")){
//					key = city;
//					city ="";
//				}
				mSearch.geocode(key, city);
				//poiSearchInCity(city,key);
			}			
			
		}
		
		public class NodeSearchListener   extends CommListener{

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				// TODO Auto-generated method stub
				//得到地址信息
				if (arg1 != 0) {
					return;
				}
			    Drawable marker = newnode;	
			    if (complaintinfo==null){
			    	complaintinfo = new ComplaintEntity();
			    }
        		complaintinfo.address =arg0.strAddr;
        				
			   	OverlayItem item= new OverlayItem(p,"点击输入举报内容",arg0.strAddr);

			   	item.setMarker(marker);	    
			   	olp.clearnewnode();
			   	olp.addItem(item,true);

			   mMapView.refresh();		
				
				
				
			}

			
		}		
		
	}
	
	/**
	 * 根据查询名称查询范围列出所有的信息
	 */
	public void LoactionNode(String address){
		
		
		
	}
	
    /**
  * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
  */
	Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //Toast.makeText(actConText, "msg:" +msg.what, Toast.LENGTH_SHORT).show();
        };
    };   
    public class MyLocationListenner implements BDLocationListener {
    	MapOffset offset = null;
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            
        	//latitude =30.545651;
        	//longitude =104.081123;            
            if ((latitude <0.00001) && (longitude <0.00001)){
            	latitude =30.545651;
            	longitude =104.081123;            	
            }

            	if (Math.abs(locData.latitude-latitude)>0.00001 || Math.abs(locData.longitude-longitude)>0.00001){
            		locData.latitude = latitude;
            		locData.longitude = longitude;            		
            		offset = new MapOffset(latitude, longitude);
            	}else{
            		return;
            	}
            	
			    if (complaintinfo==null){
			    	complaintinfo = new ComplaintEntity();
			    }            	
            	
			    complaintinfo.city_name = location.getCity();
            	
            //得到周围点列�?
            GetRoundList(locData.latitude,locData.longitude,offset,0);
            	
          //其他定位方法
            
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            myLocationOverlay.setData(locData);
            
            
            mMapView.refresh();
            MoveToLocData();
        }

        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }	
        
    }
    
    public void MoveToLocData(){
    	if (locData == null) return;
    	
        mMapController.animateTo(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)), mHandler.obtainMessage(1));
        new DrawCircle(locData.latitude,locData.longitude,graphicsOverlay);
        mMapView.refresh();
    	
    }
    
    //地图偏移修正 只能处理定位�?
    
    public class MapOffset{
		double OffsetLatitude;
		double OffsetLongitude;
    	boolean IsOffset =false;
    	public MapOffset(double bdLatitude,double bdLongitude){
    		double gspLatitude;
    		double gspLongitude;
    		
            String serviceString = Context.LOCATION_SERVICE;  
            LocationManager locationManager = (LocationManager)actConText.getSystemService(serviceString);
            
            String provider = LocationManager.GPS_PROVIDER;    
            Location alocation = locationManager.getLastKnownLocation(provider); 
            
            if (alocation != null){//判断位置是否存在  
            	gspLatitude =alocation.getLatitude();
            	gspLongitude =alocation.getLongitude();
            	OffsetLatitude = bdLatitude-gspLatitude;
            	OffsetLongitude = bdLongitude-gspLongitude;
            	IsOffset =true;
            	return;
            }  
            provider = LocationManager.NETWORK_PROVIDER;    
          	alocation = locationManager.getLastKnownLocation(provider);            
            if (alocation != null){//判断位置是否存在  
            	gspLatitude =alocation.getLatitude();
            	gspLongitude =alocation.getLongitude();
            	OffsetLatitude = bdLatitude-gspLatitude;
            	OffsetLongitude = bdLongitude-gspLongitude;
            	IsOffset =true;
            	return;
            }              
          	          		
    	}
    	
    	public double BDtoGSPLatitude(double Latitude){
    		return Latitude;
//    		if (IsOffset){
//    			return Latitude-OffsetLatitude;
//    		}else return Latitude;
    		
    	}
    	public double BDtoGSPLongitude(double Longitude){
    		return Longitude;
//    		if (IsOffset){
//    			return Longitude-OffsetLongitude;
//    		}else return Longitude;
    	}
    	public double GSPtoBDLatitude(double Latitude){
    		 return Latitude;
//    		if (IsOffset){
//    			return Latitude+OffsetLatitude;    			
//    		}else return Latitude;
    	}
    	public double GSPtoBDLongitude(double Longitude){
    		return Longitude;
//    		if (IsOffset){
//    			return Longitude+OffsetLongitude;   			
//    		}else return Longitude;
    	}    	
    }
}
