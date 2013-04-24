package com.waterpollution;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.waterpollution.adapter.ComplaintDetailAdapter;
import com.waterpollution.parser.ComplaintParser;
import com.waterpollution.util.Constant;
import com.waterpollution.util.ImageUtil;
import com.waterpollution.util.NetUtil;
import com.waterpollution.vo.ComplaintEntity;
import com.waterpollution.vo.ComplaintInfo;
import com.waterpollution.vo.ListModeVo;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class ComplaintDetailActivity extends BaseActivity {
	private String complaintName;
	private List<Drawable> complaintList;
	private ComplaintEntity complaintEntity;
	
	private Gallery complaintImageList;
	private TextView complaintDetailName;
	private TextView complaintDetailAddr;
	private TextView complaintDetailCount;
	private TextView reComplaint;
	private ListView lvCommentList;
	
	private Button btnAddComment;
	private Button btnForward;
	private Button btnwhat;
	private Button btnAgree;
	private Button btnDisagree;
	
	private int listSelected=-1; 
	
	View view;
	PopupWindow pop;
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.textViewReComplaint:
			Intent intent = new Intent(ComplaintDetailActivity.this,NewComplaintActivity.class);
			intent.putExtra("address", complaintEntity.address);
			intent.putExtra("Latitude", complaintEntity.latitude);
			intent.putExtra("Longitude", complaintEntity.longitude);
			intent.putExtra("city_name", complaintEntity.city_name);
			startActivity(intent);
			break;
		}
	}
	@Override
	protected void onHeadRightButton(View v){
		initView();
	}

	@Override
	protected void findViewById() {
		complaintDetailName = (TextView)findViewById(R.id.textViewComplaintDetailName);
		complaintDetailAddr = (TextView)findViewById(R.id.textViewComplaintdetailAddr);
		complaintDetailCount = (TextView)findViewById(R.id.TextViewComplaintDetailCount);
		reComplaint = (TextView)findViewById(R.id.textViewReComplaint);
		complaintImageList = (Gallery) findViewById(R.id.galleryComplaintDetail);
		lvCommentList = (ListView) findViewById(R.id.listViewComplaintList);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_complaint_detail);
		setTitle(R.string.titleComplaintDetail);
		
		setHeadLeftBackgroundResource(R.drawable.backbtn);
		application.setLeftButtonIndex(Constant.LeftButton.BACK);
		setHeadLeftVisibility(View.VISIBLE);
		
		selectedBottomTab(Constant.HOME);
	}

	@Override
	protected void processLogic() {
		getComplaintDetail();
		complaintDetailName.setText(String.format(getString(R.string.complaintDetailName), complaintName));
		complaintDetailAddr.setText(String.format(getString(R.string.complaintDetailAddr), complaintEntity.address));
		complaintDetailCount.setText(String.format(getString(R.string.complaintDetailCount), complaintEntity.count));
		
		
		complaintList = new ArrayList<Drawable>();
		loadImageList();
		complaintImageList.setAdapter(new ImageAdapter(ComplaintDetailActivity.this,complaintList));
		complaintImageList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				Intent intent = new Intent(ComplaintDetailActivity.this,ImageBrowerActivity.class);
				intent.putExtra(ImageBrowerActivity.INTENTBITMAP, ImageUtil.drawableToBitmap(complaintList.get(arg2)));	
				startActivity(intent);
			}});
		
		ComplaintDetailAdapter cda = new ComplaintDetailAdapter(ComplaintDetailActivity.this,complaintEntity.complaintInfoList);
		lvCommentList.setAdapter(cda);		
		initPopupWindow();
		setHeadRightBackgroundResource(R.drawable.fresh1);
		setHeadRightText("");
	}
	
	
	private void loadImageList(){
		Drawable image=null;
		ComplaintInfo ci=null;
		if (complaintEntity.complaintInfoList!=null)
		{
			for(int i=0;i<complaintEntity.complaintInfoList.size()-1;i++){
				ci = complaintEntity.complaintInfoList.get(i);
				if (!ci.image.equals("")){
					try {
						image = ImageUtil.getDrawableFromUrl(ci.image);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (image!=null){
						complaintList.add(image);
						image=null;
					}
				}
			}
		}
	}
	
	private void initPopupWindow()
	{
		view = this.getLayoutInflater().inflate(R.layout.popup_window, null);
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setOutsideTouchable(true);
		btnAddComment = (Button) view.findViewById(R.id.btnAddComment);
		btnForward = (Button) view.findViewById(R.id.btnForward);
		btnwhat = (Button) view.findViewById(R.id.btnWhat);
		btnAgree = (Button) view.findViewById(R.id.btnAgree);
		btnDisagree = (Button) view.findViewById(R.id.btnDisAgree);
		
		OnClickListener popUpClickListener=new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listSelected!=-1){
					switch(v.getId()){
					case R.id.btnAddComment:
						Intent intent = new Intent(ComplaintDetailActivity.this,ComplaintCommentActivity.class);
						intent.putExtra("complaintId", String.valueOf(complaintEntity.complaintInfoList.get(listSelected).complaintID));  
						startActivity(intent);
						break;
					case R.id.btnForward:
						Intent intent1 = new Intent(ComplaintDetailActivity.this,ReBroadCastActivity.class);
						String content = "我要对位于"+complaintEntity.address+"的"+ complaintEntity.title+"进行举报：";
						intent1.putExtra("weiboConent", content);  
						intent1.putExtra("latitude",String.valueOf(complaintEntity.latitude));  
						intent1.putExtra("longitude",String.valueOf(complaintEntity.longitude));  
						startActivity(intent1);
						break;
					case R.id.btnWhat:
						break;
					case R.id.btnAgree:
						postAgree(true,complaintEntity.complaintInfoList.get(listSelected));
						break;
					case R.id.btnDisAgree:
						postAgree(false,complaintEntity.complaintInfoList.get(listSelected));
						break;
					}
				}
				pop.dismiss();
			}
		};
		view.setOnClickListener(popUpClickListener);
		btnAddComment.setOnClickListener(popUpClickListener);
		btnForward.setOnClickListener(popUpClickListener);
		btnwhat.setOnClickListener(popUpClickListener);
		btnAgree.setOnClickListener(popUpClickListener);
		btnDisagree.setOnClickListener(popUpClickListener);
	}
	
	
	private void postAgree(boolean agree,ComplaintInfo complaintInfo){
		application.mbProxy.getUserInfo();
		String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=bycomment"+
						 "&id="+NetUtil.StringToUnicode(complaintInfo.complaintID)+
						 "&user_id="+NetUtil.StringToUnicode(application.mbProxy.getUserId())+
						 "&nick_name="+NetUtil.StringToUnicode(application.mbProxy.getNickName())+
						 "&content="+NetUtil.StringToUnicode("")+
						 "&argee="+NetUtil.StringToUnicode((String.valueOf((agree?(complaintInfo.agree+1):complaintInfo.agree))))+
						 "&disargee=" +NetUtil.StringToUnicode(String.valueOf(agree?complaintInfo.disagree:(complaintInfo.disagree+1)));
		
		String s ="";
		httpClient hc = new httpClient();
		try{
			s = hc.PostHttpData(httpcon, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ComplaintParser cr =new ComplaintParser();

		try {
			s = cr.parsecomplaintPost(new ByteArrayInputStream(s.getBytes()), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (s.equals("0")){
			//提交成功
			 Intent nt = new Intent();
			 Bundle bundle = new Bundle();
			 bundle.putBoolean("regetround",true);
			 nt.putExtras(bundle);
			 ComplaintDetailActivity.this.setResult(RESULT_OK, nt);
			 ComplaintDetailActivity.this.finish();
			 Toast.makeText(ComplaintDetailActivity.this,"成功",Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(ComplaintDetailActivity.this,"保存失败，请检查网络",Toast.LENGTH_LONG).show();
		}
		processLogic();
	}
	
	private void getComplaintDetail(){
		complaintName = getIntent().getExtras().getString("title");
		if (complaintName == null){
			ListModeVo vo = (ListModeVo)getIntent().getExtras().getSerializable("listModeVo");
			complaintName = vo.getTitle();
		}
		complaintName=(complaintName==null)?"":complaintName;
		String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=byread"+
						 "&object_name="+ NetUtil.StringToUnicode(complaintName)+
						 "&pi="+"0"+
						 "&psize="+"1000000000";
		InputStream is = null;
		try {
			httpClient hc =new httpClient();
			is = hc.GetHttpData(httpcon, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ComplaintParser cr =new ComplaintParser();
		try {
			complaintEntity = cr.parsecomplaintEntityDetail(is, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		initView();
	}
	

	@Override
	protected void setListener() {
		reComplaint.setOnClickListener(this);
		lvCommentList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listSelected = arg2;
				if(pop.isShowing())
				{
					pop.dismiss();
				}
				else
				{ 					
					pop.showAsDropDown(arg1); 
				}
			}});
	}
	
	
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		public List<Drawable> imageList;
		public ImageAdapter(Context mContext,List<Drawable> imageList){
			this.mContext=mContext;
			this.imageList = imageList;
		}	  
		@Override
		public int getCount() {
			return imageList.size();
		}
		@Override
		public Object getItem(int position) {
			return imageList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView view = new ImageView(this.mContext); 
			Drawable bitmap = imageList.get(position);
			//将ID告诉ImageView,它就能找到图片 
			view.setImageDrawable(bitmap);			
			view.setLayoutParams(new Gallery.LayoutParams(120,120)); 
			view.setScaleType(ImageView.ScaleType.FIT_CENTER); 
			return view; 
		}
	}
}
