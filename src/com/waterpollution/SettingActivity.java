package com.waterpollution;

import android.view.View;

import com.waterpollution.util.Constant;

public class SettingActivity extends BaseActivity {

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_setting);
		setTitle(R.string.titleSetting);
		setHeadLeftVisibility(View.INVISIBLE);
		setHeadRightVisibility(View.INVISIBLE);
		selectedBottomTab(Constant.SETTING);
	}

	@Override
	protected void processLogic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		
	}

}
