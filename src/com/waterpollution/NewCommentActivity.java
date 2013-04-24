package com.waterpollution;

import java.io.InputStream;

import com.waterpollution.parser.ComplaintParser;
import com.waterpollution.util.Constant;
import com.waterpollution.util.NetUtil;
import com.waterpollution.vo.ComplaintInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewCommentActivity extends BaseActivity {
	private EditText txtCommentContent;
	
	private ComplaintInfo complaintInfo;
	private String commentContent;
	ProgressDialog mpDialog;

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findViewById() {
		txtCommentContent = (EditText) findViewById(R.id.editTextCommentNew);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_complaint_comment_new);
		setHeadLeftBackgroundResource(R.drawable.backbtn);
		application.setLeftButtonIndex(Constant.LeftButton.BACK);
		setHeadLeftVisibility(View.VISIBLE);
		setHeadRightVisibility(View.VISIBLE);
		setTitle(R.string.titleNewComment);
		selectedBottomTab(Constant.HOME);
	}

	@Override
	protected void processLogic() {
		setHeadRightText(R.string.complaintCommit);
		complaintInfo = (ComplaintInfo) getIntent().getExtras().getSerializable("complaintInfo");
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}
	
	
	private boolean checkInputValid(){
		commentContent = txtCommentContent.getText().toString();
		if (commentContent.equals("")){
			new AlertDialog.Builder(this)
				.setTitle("输入提示")
				.setMessage("评论内容不能为空！")
				.setPositiveButton("确定", null)
				.show(); 
			txtCommentContent.requestFocus();
			return false;
		}
		return true;
	}
	
	
	
	@Override
	protected void onHeadRightButton(View v){
		if (checkInputValid()){
			mpDialog = new ProgressDialog(NewCommentActivity.this);
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
		String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=bycomment"+
						 "&id="+NetUtil.StringToUnicode(complaintInfo.complaintID)+
						 "&user_id="+NetUtil.StringToUnicode(application.mbProxy.getUserId())+
						 "&nick_name="+NetUtil.StringToUnicode(application.mbProxy.getNickName())+
						 "&content="+NetUtil.StringToUnicode(commentContent)+
						 "&argee="+NetUtil.StringToUnicode((String.valueOf(complaintInfo.agree))+
						 "&disargee=" +NetUtil.StringToUnicode(String.valueOf(complaintInfo.disagree)));
		
		String s ="";
		InputStream ois =null;	
		httpClient hc = new httpClient();
		try{
			ois = hc.PostHttpData(httpcon, null,"POST");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ComplaintParser cr =new ComplaintParser();

		try {
			s = cr.parsecomplaintPost(ois, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		mpDialog.dismiss();
		if (s.equals("0")){
			//提交成功
			 Intent nt = new Intent();
			 NewCommentActivity.this.setResult(RESULT_OK, nt);
			 NewCommentActivity.this.finish();
		}
		else{
			Toast.makeText(NewCommentActivity.this,"保存失败，请检查网络",Toast.LENGTH_LONG).show();
		}
	}
}
