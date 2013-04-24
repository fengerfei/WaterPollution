package com.waterpollution;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.waterpollution.util.Constant;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.net.RequestListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReBroadCastActivity extends BaseActivity {
	private EditText txtContent;
	
	private String contentBefore;
	private String content;
	private String latitude;
	private String longitude;
	
	ProgressDialog mpDialog;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findViewById() {
		txtContent = (EditText) findViewById(R.id.editTextBroadCast);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_reboradcast);
		setHeadLeftVisibility(View.VISIBLE);
		setHeadRightVisibility(View.VISIBLE);
		setTitle(R.string.titleReBroadCast);
		selectedBottomTab(Constant.HOME);
	}

	@Override
	protected void processLogic() {
		setHeadRightText(R.string.complaintCommit);
		contentBefore =  getIntent().getExtras().getString("weiboConent");
		latitude = getIntent().getExtras().getString("latitude");
		longitude = getIntent().getExtras().getString("longitude");
		txtContent.setText(contentBefore);
		setHeadRightText(R.string.reBroadCast);
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}
	
	
	private boolean checkInputValid(){
		content = txtContent.getText().toString();
		if (content.equals("")){
			new AlertDialog.Builder(this)
				.setTitle("输入提示")
				.setMessage("转发内容不能为空！")
				.setPositiveButton("确定", null)
				.show(); 
			txtContent.requestFocus();
			return false;
		}
		return true;
	}
	
	
	
	@Override
	protected void onHeadRightButton(View v){
		if (checkInputValid()){
			mpDialog = new ProgressDialog(ReBroadCastActivity.this);
			mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
			mpDialog.setTitle(getString(R.string.loadTitle));//设置标题  
			mpDialog.setMessage(getString(R.string.LoadContent));  
			mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
			mpDialog.setCancelable(false);//设置进度条是否可以按退回键取消 
			mpDialog.show();  
			new Thread(){  
                public void run(){  
                    try{  
            			sendtoServer();
                    }catch(Exception ex){  
                    	ex.printStackTrace();
                    }  
                }  
            }.start();  
		}
	}
	
	private void sendtoServer(){
		application.mbProxy.getUserInfo();
		application.mbProxy.sendWeibo(content,"", latitude, longitude, new OauthDialogListener());
		if (application.mbProxy.isSendWeiboSuccess()){
			mpDialog.dismiss();
			Intent nt = new Intent();
			ReBroadCastActivity.this.setResult(RESULT_OK, nt);
			ReBroadCastActivity.this.finish();
		}
	}
	
	private  class OauthDialogListener implements RequestListener{

		@Override
		public void onComplete(String arg0) {
			try {
				JSONObject res= new JSONObject(arg0);
				if (res.getString("error_code").equals("0")){
					mpDialog.dismiss();
					Toast.makeText(ReBroadCastActivity.this,"转发微博成功",Toast.LENGTH_LONG).show();
					Intent nt = new Intent();
					ReBroadCastActivity.this.setResult(RESULT_OK, nt);
					ReBroadCastActivity.this.finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onError(WeiboException arg0) {
			mpDialog.dismiss();
			Toast.makeText(ReBroadCastActivity.this,"发送微博错误："+arg0,Toast.LENGTH_LONG).show();
		}

		@Override
		public void onIOException(IOException arg0) {
			mpDialog.dismiss();
			Toast.makeText(ReBroadCastActivity.this,"发送微博异常："+arg0,Toast.LENGTH_LONG).show();
		}
	}

}
