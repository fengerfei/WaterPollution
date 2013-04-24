package com.waterpollution.adapter;

import java.util.List;

import com.waterpollution.ComplaintCommentActivity;
import com.waterpollution.ImageBrowerActivity;
import com.waterpollution.R;
import com.waterpollution.util.ImageUtil;
import com.waterpollution.vo.ComplaintInfo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ComplaintDetailAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context contextActivity;
    private List<ComplaintInfo> mData;
    
    public ComplaintDetailAdapter(Context context,List<ComplaintInfo> mData){
    	 this.mInflater = LayoutInflater.from(context);
    	 this.contextActivity= context;
         this.mData = mData;
    }
    
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		if (convertView == null) {
			layout = (LinearLayout) mInflater.inflate(R.layout.complaintdetail_item,null);
		} else {
			layout = (LinearLayout) convertView;
		}
		final ComplaintInfo ci = mData.get(position);
		TextView nickName = (TextView)layout.findViewById(R.id.textViewCommentusernick);
		TextView datetime = (TextView)layout.findViewById(R.id.textViewCommentDateTime);
		TextView content = (TextView)layout.findViewById(R.id.textViewCommentComplaintContent);
		TextView thumbsDownCount = (TextView)layout.findViewById(R.id.textViewThumbsDownCount);
		TextView thumbsDownUp = (TextView)layout.findViewById(R.id.textViewThumbsUpCount);
		TextView showComments = (TextView)layout.findViewById(R.id.textViewShowComments);
		showComments.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (v.getId()==R.id.textViewShowComments){
					Intent intent = new Intent(contextActivity,ComplaintCommentActivity.class);
					intent.putExtra("complaintId",ci.complaintID);
					contextActivity.startActivity(intent);
				}
			}
		});
		TextView commentSum = (TextView)layout.findViewById(R.id.textViewCommentSum);
		ImageView ivImage = (ImageView)layout.findViewById(R.id.imageViewCommentCompliaintImage);
		ImageView ivHeadImage = (ImageView)layout.findViewById(R.id.imageViewCommentUserHead);
		nickName.setText(ci.name);
		datetime.setText(ci.datetime);
		content.setText(ci.content);
		thumbsDownCount.setText(String.valueOf(ci.disagree));
		thumbsDownUp.setText(String.valueOf(ci.agree));
		commentSum.setText(ci.commentsCount);
		try {
			if (!ci.headurl.equals("")){			
				ivHeadImage.setImageDrawable(ImageUtil.getDrawableFromUrl(ci.headurl));
			}else{
				ivHeadImage.setImageResource(R.drawable.userhead);
			}
			if(!ci.thumburl.equals("")){
				ivImage.setImageDrawable(ImageUtil.getDrawableFromUrl(ci.thumburl));
				ivImage.setOnClickListener(new OnClickListener(){
	
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(contextActivity,ImageBrowerActivity.class);
						intent.putExtra(ImageBrowerActivity.INTENTIMAGEURL, ci.image);	
						contextActivity.startActivity(intent);
					}
					
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return layout;
	}
}
