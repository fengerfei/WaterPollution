package com.waterpollution.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waterpollution.R;
import com.waterpollution.util.ImageUtil;
import com.waterpollution.vo.ListModeVo;

public class ListModeAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ListModeVo> mData;

	public ListModeAdapter(Context context, List<ListModeVo> mData) {
		this.mInflater = LayoutInflater.from(context);
		this.mData = mData;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		if (convertView == null) {
			layout = (LinearLayout) mInflater.inflate(R.layout.listmode_item,
					null);
		} else {
			layout = (LinearLayout) convertView;
		}
		ImageView imageView = (ImageView) layout.findViewById(R.id.imgPicture);
		TextView txtName = (TextView) layout.findViewById(R.id.txtName);
		TextView txtCount = (TextView) layout.findViewById(R.id.txtCount);
		TextView txtAddress = (TextView) layout.findViewById(R.id.txtAddress);
		TextView txtDescription = (TextView) layout
				.findViewById(R.id.txtDescription);

		ListModeVo vo = mData.get(position);
		try {
			imageView.setBackgroundDrawable(ImageUtil.getDrawableFromUrl(vo.getImage()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		txtName.setText(vo.getTitle());
		int count = vo.getCount();
		txtCount.setText("被举报"+count+"次");
		txtAddress.setText(vo.getAddress());
		txtDescription.setText(vo.getContent());
		return layout;
	}

	public ListModeVo getListModeVo(int position){
		return mData.get(position);
	}
}
