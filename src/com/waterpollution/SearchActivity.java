package com.waterpollution;

import com.waterpollution.util.Constant;


import android.view.View;


public class SearchActivity extends BaseActivity {


	
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
		setContentView(R.layout.activity_search);
		setTitle(R.string.titleSearch);
		setHeadLeftVisibility(View.INVISIBLE);
		setHeadRightVisibility(View.INVISIBLE);
		selectedBottomTab(Constant.SEARCH);
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
