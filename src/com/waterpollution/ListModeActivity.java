package com.waterpollution;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.waterpollution.adapter.ListModeAdapter;
import com.waterpollution.parser.FirstCommentParser;
import com.waterpollution.parser.ListModeParser;
import com.waterpollution.util.Constant;
import com.waterpollution.util.NetUtil;
import com.waterpollution.vo.ComplaintEntity;
import com.waterpollution.vo.ListModeVo;
import com.waterpollution.vo.RequestVo;







public class ListModeActivity extends BaseActivity {

	private Spinner spnRange;
	private ListView listView;
	private ListModeAdapter listModeAdapter;
	
	private double latitude, longitude;
	@Override
	protected void findViewById() {
		listView = (ListView)findViewById(R.id.listMode);
		spnRange = (Spinner)findViewById(R.id.spnRange);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_listmode);
		setTitle(R.string.titleListMode);
		setHeadLeftBackgroundResource(R.drawable.range_btn);
		application.setLeftButtonIndex(Constant.LeftButton.MAP_MODE);
		setHeadLeftVisibility(View.VISIBLE);
		
		setHeadRightBackgroundResource(R.drawable.fresh1);
		setHeadRightText("");
		setHeadRightVisibility(View.INVISIBLE);
		selectedBottomTab(Constant.HOME);
	}

	@Override
	protected void processLogic() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
				R.array.listModeData, android.R.layout.simple_spinner_item); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spnRange.setAdapter(adapter);
		List<ComplaintEntity> complaintEntitylist = application.complaintEntitylist;
		if(complaintEntitylist !=null && complaintEntitylist.size()!=0){
			latitude = (double)complaintEntitylist.get(0).latitude;
			longitude = (double)complaintEntitylist.get(0).longitude;
		}
	}

	private List<ListModeVo> getData(int km) {
		RequestVo vo = new RequestVo();
		vo.requestUrl = R.string.url;
		vo.method = RequestVo.METHOD_GET;
		vo.xmlParser = new ListModeParser();
		vo.context = this;
		Map<String,String> map = new HashMap<String,String>();
		map.put("m", "compliant");
		map.put("a", "bylatlong");
		map.put("longitude", String.valueOf(longitude));
		map.put("latitude", String.valueOf(latitude));
		map.put("range", String.valueOf(km));
		vo.requestDataMap = map;
		List<ListModeVo> newData = new ArrayList<ListModeVo>();
		List<ListModeVo> data = (List<ListModeVo>)NetUtil.get(vo);
		if(data != null){
			for (ListModeVo entity : data) {
				newData.add(requestComments(entity));
			}
		}
		return newData;
	}

	private ListModeVo requestComments(final ListModeVo listModeVo){
		RequestVo vo = new RequestVo();
		vo.requestUrl = R.string.url;
		vo.method = RequestVo.METHOD_GET;
		vo.xmlParser = new FirstCommentParser();
		vo.context = this;
		Map<String,String> map = new HashMap<String,String>();
		map.put("m", "compliant");
		map.put("a", "byread");
		try {
			map.put("object_name", URLEncoder.encode(listModeVo.getTitle(),"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		map.put("pi", "0");
		map.put("psize", "1");
		vo.requestDataMap = map;
		ListModeVo retval = (ListModeVo)NetUtil.get(vo);
		listModeVo.setImage(retval.getImage());
		listModeVo.setContent(retval.getContent());
		return listModeVo;
	}
	@Override
	protected void setListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ListModeActivity.this,ComplaintDetailActivity.class);
				intent.putExtra("listModeVo", listModeAdapter.getListModeVo(arg2));
				startActivity(intent);
			}
		});
		spnRange.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				int kilometre = 1000;
				switch (arg2) {
				case 1:
					kilometre = 1000;
					break;
				case 2:
					kilometre = 2000;
					break;
				case 3:
					kilometre = 5000;
					break;
				case 4:
					kilometre = 10000;
					break;
				}
				final Integer km = Integer.valueOf(kilometre);
				showProgressDialog();
				new Thread() {
					public void run() {
						Message message = new Message();
						message.what = SUCCESS;
						message.obj = getData(km);
						myHandler.sendMessage(message);
					}
				}.start();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});
	}

	@Override
	public void onClick(View v) {
		
	}
	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;
	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				List<ListModeVo> data = (List<ListModeVo>)msg.obj;
				listModeAdapter = new ListModeAdapter(ListModeActivity.this,data);
				listView.setAdapter(listModeAdapter);
				break;
			}
			closeProgressDialog();
			super.handleMessage(msg);
		}
	};
   
}
