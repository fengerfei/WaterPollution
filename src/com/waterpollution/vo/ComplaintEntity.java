package com.waterpollution.vo;

import java.util.List;

public class ComplaintEntity {
	public String title;  //举报实体名称
	public int count;  //被举报次数
	public String address;  //举报地址
	public float longitude;  //经度
	public float latitude;   //纬度
	public String image;	//图片URL
	public String thumburl;  //缩略图
	public String city_name;  //城市名称
	public List<ComplaintInfo> complaintInfoList; //complaintInfo列表
}


