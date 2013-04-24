
/**
 * 
 */
package com.waterpollution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.waterpollution.httpClient;
import com.waterpollution.parser.ComplaintParser;
import com.waterpollution.util.Constant;
import com.waterpollution.util.NetUtil;
import com.waterpollution.vo.ComplaintEntity;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;


/**
 * @author Administrator
 *
 */
public class NewComplaintActivity extends BaseActivity {
	private TextView takePhoto;
	private TextView choosePhoto;
	private TextView asynWeibo;
	private EditText complaintName;
	private EditText complaintAddr;
	private EditText weiboContent;
	private boolean needSendWeibo;
	private Spinner spnRange;	
	private ImageView iv_image;
	private String latitude;
	private String longitude; 
	private String picPath="";
	private String cityName;
	private String content;
	private String addressInfo;
	private String weiboInfo;
	private String title;
	private Boolean popSpinner = false;
	private static final String FLAG_ASY="1";
	private static final String FLAG_NOASY="0";
	private static final int TAKE_PICTURE=1;
	private static final int CHOOSE_PICTURE=2;
	Uri photoUri;
	ProgressDialog mpDialog;

	/* (non-Javadoc)
	 * @see com.waterpollution.BaseActivity#findViewById()
	 */
	@Override
	protected void findViewById() {
		spnRange = (Spinner)findViewById(R.id.spnRange);		
		takePhoto = (TextView)super.findViewById(R.id.textViewTakePhoto);
		choosePhoto  = (TextView)super.findViewById(R.id.textViewChoosePhoto);
		asynWeibo =  (TextView)super.findViewById(R.id.textViewChkAsy);
		complaintName = (EditText)super.findViewById(R.id.editTextName);
		complaintAddr = (EditText)super.findViewById(R.id.editTextAddr);
		iv_image = (ImageView)super.findViewById(R.id.imageViewShow);
		weiboContent = (EditText)super.findViewById(R.id.editTextWeiboContent);
		asynWeibo.setTag(NewComplaintActivity.FLAG_ASY); //表示默认同步 
	}
	@Override
	protected void onHeadRightButton(View v){
		if (checkInputValid()){
			mpDialog = new ProgressDialog(NewComplaintActivity.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
			mpDialog.setTitle(getString(R.string.loadTitle));//设置标题  
			mpDialog.setMessage(getString(R.string.LoadContent));  
			mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
			mpDialog.setCancelable(false);//设置进度条是否可以按退回键取消 
			mpDialog.show();  
			new Thread(){  
                public void run(){  
                    try{  
                    	if (needSendWeibo){			
            				content = "我要对位于"+addressInfo+"的"+ title+"进行举报："+weiboInfo;
            				application.mbProxy.sendWeibo(content, picPath, latitude, longitude, new OauthDialogListener());
            			}
            			sendtoServer();
                    }catch(Exception ex){  
                    	ex.printStackTrace();
                    }  
                }  
            }.start();  
		}
	}
	private  class OauthDialogListener implements RequestListener{

		@Override
		public void onComplete(String arg0) {
			try {
				JSONObject res= new JSONObject(arg0);
				if (res.getString("error_code").equals("0")){
					Toast.makeText(NewComplaintActivity.this,"发送微博成功："+arg0,Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onError(WeiboException arg0) {
			Toast.makeText(NewComplaintActivity.this,"发送微博错误："+arg0,Toast.LENGTH_LONG).show();
		}

		@Override
		public void onIOException(IOException arg0) {
			Toast.makeText(NewComplaintActivity.this,"发送微博异常："+arg0,Toast.LENGTH_LONG).show();
		}
	}
	
	private void sendtoServer(){
		
		application.mbProxy.getUserInfo();
		String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=insert&" +
				"id="+java.util.UUID.randomUUID().toString() +
				"&user_id=" + application.mbProxy.getUserId()+
				"&nick_name=" + NetUtil.StringToUnicode(application.mbProxy.getNickName())+
				"&object_name="+NetUtil.StringToUnicode(title)+
				"&object_type="+ "hs"+
				"&address=" +NetUtil.StringToUnicode(addressInfo)+
				"&longitude="+ longitude+
				"&latitude="+latitude+
				"&content="+NetUtil.StringToUnicode(weiboInfo)+
				"&city_name="+NetUtil.StringToUnicode(cityName);
		

		String s ="";
		InputStream is = null;
		InputStream ois =null;		
		try {
			if (!picPath.equals("")){
				is = new FileInputStream(picPath);
			}

		httpClient hc = new httpClient();

		ois = hc.PostHttpData(httpcon, is,"POST");
		ComplaintParser cr =new ComplaintParser();

		s = cr.parsecomplaintPost(ois, "utf-8");
		
		if (is != null){
			is.close();
		}
		
		} catch (Exception e) {
			e.printStackTrace();
		}	
		mpDialog.dismiss();
		if (s.equals("0")){
			//提交成功
			 Intent nt = new Intent();
			 Bundle bundle = new Bundle();
			 bundle.putBoolean("regetround",true);
			 nt.putExtras(bundle);
			 NewComplaintActivity.this.setResult(RESULT_OK, nt);
			 NewComplaintActivity.this.finish();
		}
		else{
			Toast.makeText(NewComplaintActivity.this,"保存失败，请检查网络",Toast.LENGTH_LONG).show();
		}
	}
	
	private boolean checkInputValid(){
		title = complaintName.getText().toString();
		if (title.equals("")){
			new AlertDialog.Builder(this)
				.setTitle("输入提示")
				.setMessage("请输入举报的对象名称")
				.setPositiveButton("确定", null)
				.show(); 
			complaintName.requestFocus();
			return false;
		}
		addressInfo = complaintAddr.getText().toString();
		if (addressInfo.equals("")){
			new AlertDialog.Builder(this)
				.setTitle("输入提示")
				.setMessage("请输入地点")
				.setPositiveButton("确定", null)
				.show(); 
			complaintAddr.requestFocus();
			return false;			
		}
		weiboInfo = weiboContent.getText().toString();
		if (weiboInfo.equals("")){
			new AlertDialog.Builder(this)
				.setTitle("输入提示")
				.setMessage("举报原因不能为空")
				.setPositiveButton("确定", null)
				.show();
			weiboContent.requestFocus();
			return false;			
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.waterpollution.BaseActivity#loadViewLayout()
	 */
	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_complaint);
		setHeadLeftVisibility(View.VISIBLE);
		headLeftBtn.setBackgroundResource(R.drawable.backbtn);
		application.setLeftButtonIndex(Constant.LeftButton.BACK);
		setHeadRightText(getString(R.string.complaintCommit));
		selectedBottomTab(Constant.HOME);			
	}

	/* (non-Javadoc)
	 * @see com.waterpollution.BaseActivity#processLogic()
	 */
	@Override
	protected void processLogic() {
		needSendWeibo = true;
		popSpinner = false;
		Bundle inParam = getIntent().getExtras();
		addressInfo = inParam.getString("address");
		latitude =  Float.toString(inParam.getFloat("Latitude"));
		longitude = Float.toString(inParam.getFloat("Longitude"));
		cityName = inParam.getString("city_name");
		complaintAddr.setText(addressInfo);
		setHeadRightText(R.string.complaintCommit);
		List<String> CharS = new ArrayList<String>();
		List<ComplaintEntity> complaintEntitylist = application.complaintEntitylist;
		
		CharS.add("新增");
		for(ComplaintEntity entity: complaintEntitylist){
			CharS.add(entity.title);
        } 		
		
		ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,CharS);		
		
//		ArrayAdapter.createFromResource(context, textArrayResId, textViewResId) 
				
//				ArrayAdapter.createFromResource(this, 
//				R.array.listModeData, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spnRange.setAdapter(adapter);
		
	}

	/* (non-Javadoc)
	 * @see com.waterpollution.BaseActivity#setListener()
	 */
	@Override
	protected void setListener() {
		takePhoto.setOnClickListener(this);
		choosePhoto.setOnClickListener(this);
		asynWeibo.setOnClickListener(this);
		spnRange.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2>0){
					complaintName.setText(spnRange.getSelectedItem().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
			
		});		
		}
	@Override
	protected void onResume(){
			
	        super.onResume();
	        if (!popSpinner){
	        	spnRange.performClick();
	        	popSpinner = true;
	        }
	        	
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textViewTakePhoto:
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	
			 ContentValues values = new ContentValues();
			 ContentResolver resolver = NewComplaintActivity.this.getContentResolver();
			 String sdStatus = Environment.getExternalStorageState();
	            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
	                
	                return;
	            }			 
	            File file = new File("/sdcard/waterPollution/");
	            file.mkdirs();// 创建文件夹
	            photoUri=  Uri.fromFile(new File("/sdcard/waterPollution/temp.jpg")); 
//             photoUri = resolver.insert(
//                     MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
             openCameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                     photoUri);
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
			break;
		case R.id.textViewChoosePhoto:
			Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
			openAlbumIntent.setType("image/*");
			startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
			break;
		case R.id.textViewChkAsy:
			if (asynWeibo.getTag().toString().equals(NewComplaintActivity.FLAG_ASY)){
				asynWeibo.setTag(NewComplaintActivity.FLAG_NOASY);
				asynWeibo.setBackgroundResource(R.drawable.unsendweibo);
				needSendWeibo = false;
			}else{
				asynWeibo.setTag(NewComplaintActivity.FLAG_ASY);
				asynWeibo.setBackgroundResource(R.drawable.sendweibo);
				needSendWeibo = true;
			}
			break;
		case R.drawable.backbtn:
			break;
		}
	}
	
	@SuppressLint("SdCardPath")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case TAKE_PICTURE:
				String sdStatus = Environment.getExternalStorageState();
	            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
	                
	                return;
	            }
	            Bundle bundle = null;// data.getExtras();
	            Bitmap bitmap = null;
	            
	            if (photoUri ==null){
	    			Toast.makeText(NewComplaintActivity.this,"照相失败，请检查SD卡及存储空间",Toast.LENGTH_LONG).show();
	    			return;
	            }

	        	   Uri uri=photoUri;

	        	    try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	           FileOutputStream b = null;
	            //String fileName = "/sdcard/waterPollution/temp.jpg";
	            String fileNamesize = "/sdcard/waterPollution/tempsize.jpg";
	            
	            if (bitmap.getWidth()>800){
	            	bitmap = Bitmap.createScaledBitmap(bitmap, 800, 600, false);
	            }
	            
//	            try {
//	                b = new FileOutputStream(fileName);
//	                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//	                picPath = fileName;
//
//	 	           	                
//	            } catch (FileNotFoundException e) {
//	                e.printStackTrace();
//	            } finally {
//	                try {
//	                    b.flush();
//	                    b.close();
//	                } catch (IOException e) {
//	                    e.printStackTrace();
//	                }
//	            }
	            try {
		                b = new FileOutputStream(fileNamesize);
		                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
		                picPath = fileNamesize;	 		 	           	                
		            } catch (FileNotFoundException e) {
		                e.printStackTrace();
		            } finally {
		                try {
		                    b.flush();
		                    b.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            } 	 	            
	            
	            
	 	           //对图像进行处理，如果太大那么缩小
//	 	           if (bitmap.getWidth()>800){
//	 	        	   //int size =  (bitmap.getWidth() % 600);
//	 	        	   BitmapFactory.Options options = new BitmapFactory.Options();
//	 	        	   options.inSampleSize = 3;
//	 	        	   bitmap = BitmapFactory.decodeFile(picPath, options);
//	 	        	  try {
//	 		                b = new FileOutputStream(fileNamesize);
//	 		                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//	 		                picPath = fileNamesize;	 		 	           	                
//	 		            } catch (FileNotFoundException e) {
//	 		                e.printStackTrace();
//	 		            } finally {
//	 		                try {
//	 		                    b.flush();
//	 		                    b.close();
//	 		                } catch (IOException e) {
//	 		                    e.printStackTrace();
//	 		                }
//	 		            } 	        	   
//		 	        }	 	     
	 	           
	            
	            iv_image.setImageBitmap(bitmap);
				break;
			case CHOOSE_PICTURE:
				Bitmap bm = null;
			    ContentResolver resolver = getContentResolver();
		        try {
		            Uri originalUri = data.getData();   
		            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); 
		            String[] proj = {MediaStore.Images.Media.DATA};
		            @SuppressWarnings("deprecation")
					Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
		            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		            cursor.moveToFirst();
		            picPath = cursor.getString(column_index);
		            iv_image.setImageBitmap(bm);
		        }catch (IOException e) {
		        }
		        
				break;
			default:
				break;
			}
		}
	}
	
}
