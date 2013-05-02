package com.waterpollution.parser;

//举报信息解析类
//采用Full解析
import org.xmlpull.v1.XmlPullParser;

import com.waterpollution.vo.ComplaintComment;
import com.waterpollution.vo.ComplaintEntity;
import com.waterpollution.vo.ComplaintInfo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Xml;


public class ComplaintParser {
	public List<ComplaintEntity> parsecomplaintEntity(InputStream is,String CodeType) throws Exception{
		List<ComplaintEntity> comlist = new ArrayList<ComplaintEntity>();
		ComplaintEntity ce =null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CodeType);
		
		int eventType = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				
				break;	
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("complaintEntity")) {
					ce = new ComplaintEntity();					
				}
				else if (parser.getName().equals("title")) {					
					ce.title = parser.nextText();					
				}else if (parser.getName().equals("count")) {
					ce.count =  Integer.parseInt(parser.nextText());		
				}else if (parser.getName().equals("image")) {
					ce.image =  parser.nextText();		
				}else if (parser.getName().equals("address")) {
					ce.address =  parser.nextText();					
				}else if (parser.getName().equals("longitude")) {
					ce.longitude =  Float.parseFloat(parser.nextText());					
				}else if (parser.getName().equals("latitude")) {
					ce.latitude =  Float.parseFloat(parser.nextText());					
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("complaintEntity")) {
					comlist.add(ce);
					ce = null;	
				}				
				break;				
			}
			eventType = parser.next();				
		}
		return comlist;
	}
	
	
	public ComplaintEntity parsecomplaintEntityDetail(InputStream is,String CodeType) throws Exception{
		ComplaintEntity ce = null ;	
		ComplaintInfo ci = null;
		boolean EntityFinish=false;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CodeType);
		
		int eventType = parser.getEventType();	
		//int TEXTTAG =0;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;	
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("complaintEntity")) {
					ce = new ComplaintEntity();			
				}
				else if (parser.getName().equals("title")) {
					ce.title = parser.nextText();					
				}else if (parser.getName().equals("count")) {
					String str =parser.nextText();
					if (!EntityFinish){
						ce.count =  Integer.parseInt(str);		
					}else{
						ci.commentsCount = str;
					}	
				}else if (parser.getName().equals("image")) {
					if (!EntityFinish){
						ce.image =  parser.nextText();
					}else{
						ci.image = parser.nextText();
					}	
				}else if (parser.getName().equals("thumb")) {
					if (!EntityFinish){
						ce.thumburl =  parser.nextText();
					}else{
						ci.thumburl = parser.nextText();
					}
				}else if (parser.getName().equals("address")) {
					ce.address =  parser.nextText();					
				}else if (parser.getName().equals("longitude")) {
					ce.longitude =  Float.parseFloat(parser.nextText());					
				}else if (parser.getName().equals("latitude")) {
					ce.latitude =  Float.parseFloat(parser.nextText());					
				
				}else if (parser.getName().equals("headurl")) {
					ci.headurl =  parser.nextText();					
				}else if (parser.getName().equals("complaintInfoList")){
						ce.complaintInfoList = new ArrayList<ComplaintInfo>();
				}else if (parser.getName().equals("complaintInfo")){
					ci = new ComplaintInfo();
				}else if (parser.getName().equals("name")) {
					ci.name = parser.nextText();					
				}else if (parser.getName().equals("datetime")) {				
					ci.datetime = parser.nextText();
				}else if (parser.getName().equals("content")) {
					ci.content = parser.nextText();
				}else if (parser.getName().equals("complaintID")) {
					ci.complaintID = parser.nextText();
				}else if (parser.getName().equals("agree")) {
					ci.agree = Integer.parseInt(parser.nextText());
				}else if (parser.getName().equals("disagree")) {
					ci.disagree = Integer.parseInt(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("complaintEntity")) {
					EntityFinish = true;
				}else if (parser.getName().equals("complaintInfo")) {
					ce.complaintInfoList.add(ci);
					ci = null;	
				}				
				break;				
			}
			//if (eventType !=XmlPullParser.START_TAG)
			eventType = parser.next();				
		}
		return ce;
	}
	public List<ComplaintInfo> parsecomplaintInfoByUid(InputStream is,String CodeType) throws Exception{
		List<ComplaintInfo> comlist = new ArrayList<ComplaintInfo>();
		ComplaintInfo ci =null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CodeType);
		
		int eventType = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;	
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("complaintInfo")) {
					ci = new ComplaintInfo();					
				}else if (parser.getName().equals("name")) {
					parser.next();
					ci.name = parser.getText();					
				}else if (parser.getName().equals("datetime")) {
					parser.next();					
					ci.datetime = parser.getText();
				}else if (parser.getName().equals("content")) {
					parser.next();
					ci.content = parser.getText();
				}else if (parser.getName().equals("compliant_id")) {
					parser.next();
					ci.complaintID = parser.getText();
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("complaintInfo")) {
					comlist.add(ci);
					ci = null;	
				}				
				break;				
			}
			eventType = parser.next();				
		}
		return comlist;
	}
	public ComplaintEntity parsecomplaintInfoWithComments(InputStream is,String CodeType) throws Exception{
		ComplaintEntity ce = new ComplaintEntity();
		ComplaintInfo ci =null;
		ComplaintComment cc = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CodeType);
		int eventType = parser.getEventType();
		boolean InfoFinish=false;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;	
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("compliant_info")) {
					ci = new ComplaintInfo();
					ce.complaintInfoList = new ArrayList<ComplaintInfo>();
					ce.complaintInfoList.add(ci);
				}else if (parser.getName().equals("nickname")) {
					ci.name = parser.nextText();				
				}else if (parser.getName().equals("headurl")) {
					if (!InfoFinish){
						ci.headurl = parser.nextText();
					}else{
						cc.userHearderUrl = parser.nextText();
					}
				}else if (parser.getName().equals("name")) {
					ce.title = parser.nextText();
				}else if (parser.getName().equals("address")) {
					ce.address = parser.nextText();
				}else if (parser.getName().equals("longitude")) {
					ce.longitude = Float.parseFloat(parser.nextText());
				}else if (parser.getName().equals("latitude")) {
					ce.latitude = Float.parseFloat(parser.nextText());
				}else if (parser.getName().equals("info")) {
					if (!InfoFinish){
						ci.content = parser.nextText();
					}else{
						cc.commentInfo = parser.nextText();
					}
				}else if (parser.getName().equals("datetime")) {
					if (!InfoFinish){
						ci.datetime = parser.nextText();
					}else{
						cc.commentDataTime = parser.nextText();
					}
				}else if (parser.getName().equals("thumb")) {
					ci.thumburl = parser.nextText();
				}else if (parser.getName().equals("image")) {
					ci.image = parser.nextText();
				}else if (parser.getName().equals("agreeCount")) {
					ci.agree = Integer.parseInt(parser.nextText());
				}else if (parser.getName().equals("disagreeCount")) {
					ci.disagree =Integer.parseInt(parser.nextText());
				}else if (parser.getName().equals("comments")) {
					if (ci.commentList==null){
						ci.commentList = new ArrayList<ComplaintComment>();
					}
					cc = new ComplaintComment();
				}else if (parser.getName().equals("user")) {
					cc.userNickName =parser.nextText();
				}else if (parser.getName().equals("disagreeCount")) {
					ci.disagree =Integer.parseInt(parser.nextText());
				}else if (parser.getName().equals("disagreeCount")) {
					ci.disagree =Integer.parseInt(parser.nextText());
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("compliant_info")) {
					InfoFinish=true;
				}	
				if (parser.getName().equals("comments")) {
					ci.commentList.add(cc);
					cc = null;	
				}				
				break;				
			}
			eventType = parser.next();				
		}
		return ce;
	}
	
	public String  parsecomplaintPost(InputStream is,String CodeType) throws Exception{
		String result  = "";
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CodeType);
		
		int eventType = parser.getEventType();
		
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;	
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("result")) {
					result =parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:	
				break;				
			}
			eventType = parser.next();				
		}
		return result;
	}	
}
