package com.waterpollution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.waterpollution.adapter.RankingListAdapter;
import com.waterpollution.parser.RankingParser;
import com.waterpollution.util.Constant;
import com.waterpollution.vo.RankingVo;
import com.waterpollution.vo.RequestVo;

public class RankingActivity extends BaseActivity {

	private ListView rankingListView;
	private RankingListAdapter adapter;
	@Override
	protected void findViewById() {
		rankingListView = (ListView)findViewById(R.id.lstRanking);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_ranking);
		setTitle(R.string.titleRanking);
		setHeadLeftVisibility(View.INVISIBLE);
		setHeadRightBackgroundResource(R.drawable.fresh1);
		setHeadRightVisibility(View.VISIBLE);
		selectedBottomTab(Constant.RANKING);
		if(!application.mbProxy.isOauth()){
			setContentView(R.layout.activity_nouser_ranking);
			return;
		}
	}

	@Override
	protected void processLogic() {
		showProgressDialog();
		RequestVo vo = new RequestVo();
		vo.requestUrl = R.string.url;
		vo.method = RequestVo.METHOD_GET;
		vo.xmlParser = new RankingParser();
		vo.context = this;
		Map<String,String> map = new HashMap<String,String>();
		map.put("m", "compliant");
		map.put("a", "bycity");
		vo.requestDataMap = map;
		getDataFromServer(vo, new DataCallback<List<RankingVo>>() {

			@Override
			public void processData(List<RankingVo> paramObject, boolean paramBoolean) {
				if (paramObject != null) {
					//生成适配器的Item和动态数组对应的元素  
			        adapter = new RankingListAdapter(RankingActivity.this,paramObject);  
			        //添加并且显示  
			        rankingListView.setAdapter(adapter);
				} else {
					Toast.makeText(RankingActivity.this, "网络错误！", Toast.LENGTH_LONG).show();
				}
			}
		});
		setHeadRightText("");
	}

	@Override
	protected void setListener() {
		rankingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(RankingActivity.this,CitymapActivity.class);
				intent.putExtra("city_name", adapter.getCity(arg2).getCityName());
				startActivity(intent);
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
