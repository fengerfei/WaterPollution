package com.waterpollution.adapter;

import java.util.List;

import com.waterpollution.R;
import com.waterpollution.util.ImageUtil;
import com.waterpollution.vo.ComplaintComment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ComplaintCommentAdapter extends BaseAdapter {
	private List<ComplaintComment> commList;
	private LayoutInflater mInflater;
    public ComplaintCommentAdapter(Context context,List<ComplaintComment> mData){
    	this.commList = mData;
    	this.mInflater = LayoutInflater.from(context);
    }
	@Override
	public int getCount() {
		return commList.size();
	}

	@Override
	public Object getItem(int position) {
		return commList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		if (convertView == null) {
			layout = (LinearLayout) mInflater.inflate(R.layout.commnent_item,null);
		} else {
			layout = (LinearLayout) convertView;
		}
		TextView nickName = (TextView)layout.findViewById(R.id.textViewCommentusernick);
		TextView datetime = (TextView)layout.findViewById(R.id.textViewCommentDateTime);
		TextView content = (TextView)layout.findViewById(R.id.textViewCommentComplaintContent);
		ImageView ivHeadImage = (ImageView)layout.findViewById(R.id.imageViewCommentUserHead);
		ComplaintComment cc = commList.get(position);
		nickName.setText(cc.userNickName);
		datetime.setText(cc.commentDataTime);
		content.setText(cc.commentInfo);
		try {
			ivHeadImage.setImageDrawable(ImageUtil.getDrawableFromUrl(cc.userHearderUrl));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return layout;
	}
}
