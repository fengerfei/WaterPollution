package com.waterpollution.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.waterpollution.R;
import com.waterpollution.vo.RankingVo;

public class RankingListAdapter extends BaseAdapter {
	 private LayoutInflater mInflater;
     private List<RankingVo> mData;
     
     public RankingListAdapter(Context context,List<RankingVo> mData){
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
			layout = (LinearLayout) mInflater.inflate(R.layout.ranking_item,null);
		} else {
			layout = (LinearLayout) convertView;
		}
		TextView txtCityName = (TextView) layout.findViewById(R.id.txtCityName);
		TextView txtPlace = (TextView) layout.findViewById(R.id.txtPlace);
		TextView txtCount = (TextView) layout.findViewById(R.id.txtCount);

		RankingVo vo = mData.get(position);
		txtCityName.setText(vo.getCityName());
		txtPlace.setText("共有" + vo.getObjectNums() + "地点被举报");
		txtCount.setText("共举报" + vo.getCompliantNums() + "次");
		return layout;
	}
     
     public RankingVo getCity(int position){
    	 return mData.get(position);
    		
     }
}
