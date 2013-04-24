package com.waterpollution;

import com.waterpollution.util.Constant;
import com.waterpollution.util.ImageUtil;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class ImageBrowerActivity extends BaseActivity {
	private ImageView ivBrower;
	private String imageURL;
	private Bitmap bitimage;
	public static final String INTENTBITMAP="bitmapImage";
	public static final String INTENTIMAGEURL="imageurl";

	@Override
	public void onClick(View v) {
	}

	@Override
	protected void findViewById() {
		ivBrower = (ImageView) findViewById(R.id.imageViewBrower);
	}

	@Override
	protected void loadViewLayout() {
		setContentView(R.layout.activity_imageview);
		setHeadLeftVisibility(View.VISIBLE);
		setHeadLeftBackgroundResource(R.drawable.backbtn);
		application.setLeftButtonIndex(Constant.LeftButton.BACK);
		setHeadRightVisibility(View.INVISIBLE);
		setTitle(R.string.titleImageView);
		selectedBottomTab(Constant.HOME);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void processLogic() {
		imageURL = getIntent().getExtras().getString(ImageBrowerActivity.INTENTIMAGEURL);
		bitimage = (Bitmap) getIntent().getParcelableExtra(ImageBrowerActivity.INTENTBITMAP);
		if (imageURL!=null){
		try {
			ivBrower.setBackgroundDrawable(ImageUtil.getDrawableFromUrl(imageURL));
		} catch (Exception e) {
			e.printStackTrace();
		}}
		if (bitimage!=null){
			ivBrower.setImageBitmap(bitimage);
		}
	}

	@Override
	protected void setListener() {
	}

}
