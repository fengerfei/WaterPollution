package com.waterpollution.vo;

import java.io.Serializable;
import java.util.List;

public class ComplaintInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3585817843640549434L;
	public String complaintID;
	public String name;  //举报人名称
	public String headurl;
	public String datetime;	//举报时间
	public String content;  //举报内容
	public String image;	//图片URL
	public String thumburl; //
	public String commentsCount; //评论总数	
	public int agree;
	public int disagree;
	public List<ComplaintComment> commentList; //列表
}
