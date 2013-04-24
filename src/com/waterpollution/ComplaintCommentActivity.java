package com.waterpollution;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.waterpollution.adapter.ComplaintCommentAdapter;
import com.waterpollution.parser.ComplaintParser;
import com.waterpollution.util.Constant;
import com.waterpollution.util.ImageUtil;
import com.waterpollution.vo.ComplaintComment;
import com.waterpollution.vo.ComplaintEntity;
import com.waterpollution.vo.ComplaintInfo;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ComplaintCommentActivity extends BaseActivity {
	private ImageView lvUserHearder;
	private ImageView lvImage;
	private TextView txtNickName;
	private TextView txtcommentCount;
	private TextView txtContent;
	private TextView txtAgree;
	private TextView txtDisagree;
	private TextView txtDatetime;
	private ListView lvComment;
	
	private String complaintId;
	private ComplaintEntity complaintEntity;
	private ComplaintInfo complaintInfo;
	private List<ComplaintComment> commList;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findViewById() {
		lvUserHearder = (ImageView) findViewById(R.id.imageViewCommentUserHead);
		lvImage = (ImageView) findViewById(R.id.imageViewCommentCompliaintImage);
		txtNickName = (TextView) findViewById(R.id.textViewCommentusernick);
		txtcommentCount = (TextView) findViewById(R.id.textViewCommentCount);
		txtContent = (TextView) findViewById(R.id.textViewCommentComplaintContent);
		txtAgree = (TextView) findViewById(R.id.TextViewbadComment);
		txtDisagree = (TextView) findViewById(R.id.TextViewGoodComment);
		txtDatetime = (TextView) findViewById(R.id.textViewCommentDateTime);
		lvComment = (ListView)findViewById(R.id.listViewComment);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_complaint_comment);
		setHeadLeftVisibility(View.VISIBLE);
		setHeadLeftBackgroundResource(R.drawable.backbtn);
		application.setLeftButtonIndex(Constant.LeftButton.BACK);
		setHeadRightVisibility(View.VISIBLE);
		setTitle(R.string.titleRelatedComment);
		selectedBottomTab(Constant.HOME);
	}

	@Override
	protected void processLogic() {
		setHeadRightText(R.string.comment);
		complaintId = getIntent().getExtras().getString("complaintId");
		getcommentsList();
	}
	
	private void getcommentsList(){
		String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=byid&id="+complaintId;
		InputStream is = null;
		try {
			httpClient hc =new httpClient();
			is = hc.GetHttpData(httpcon, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ComplaintParser cr =new ComplaintParser();
		try {
			complaintEntity = cr.parsecomplaintInfoWithComments(is, "utf-8");
			complaintInfo = complaintEntity.complaintInfoList.get(0);
			commList = complaintInfo.commentList;
			if (commList==null){
				commList = new ArrayList<ComplaintComment>();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			lvImage.setImageDrawable(ImageUtil.getDrawableFromUrl(complaintEntity.thumburl));
			lvUserHearder.setImageDrawable(ImageUtil.getDrawableFromUrl(complaintInfo.headurl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtNickName.setText(complaintInfo.name);
		txtcommentCount.setText(String.format(getString(R.string.commentContentCount),commList.size()));
		txtContent.setText(complaintInfo.content);
		txtDatetime.setText(complaintInfo.datetime);
		txtAgree.setText(String.valueOf(complaintInfo.agree));
		txtDisagree.setText(String.valueOf(complaintInfo.disagree));
		lvComment.setAdapter(new ComplaintCommentAdapter(ComplaintCommentActivity.this,commList){});
	}

	@Override
	protected void setListener() {
	}

	@Override
	protected void onHeadRightButton(View v){
		Intent intent = new Intent(ComplaintCommentActivity.this,NewCommentActivity.class);
		intent.putExtra("complaintInfo", complaintInfo);
		startActivity(intent);
	}
}
