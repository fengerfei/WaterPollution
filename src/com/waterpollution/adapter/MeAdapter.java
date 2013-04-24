package com.waterpollution.adapter;

import java.util.List;

import com.waterpollution.R;
import com.waterpollution.vo.ComplaintInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MeAdapter extends BaseAdapter {

	private List<ComplaintInfo> iData;
	private LayoutInflater mInflater;
	public MeAdapter(Context context,List<ComplaintInfo> Data){
		this.iData = Data;
		this.mInflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return iData.size();
	}

	@Override
	public Object getItem(int position) {
		return iData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		if (convertView == null) {
			layout = (LinearLayout) mInflater.inflate(R.layout.me_item,null);					
		} else {
			layout = (LinearLayout) convertView;
		}
		TextView txtComplaintName = (TextView) layout.findViewById(R.id.textViewComplaintDetailName);
		TextView txtDataTime = (TextView) layout.findViewById(R.id.textViewCommentDateTime);
		TextView txtContent = (TextView) layout.findViewById(R.id.textViewCommentComplaintContent);
		ComplaintInfo info = iData.get(position);
		txtComplaintName.setText(info.name);
		txtDataTime.setText(info.datetime);
		txtContent.setText(info.content);
		return layout;
	}

}
