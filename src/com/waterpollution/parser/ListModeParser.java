package com.waterpollution.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.waterpollution.vo.ListModeVo;

public class ListModeParser extends BaseParser<List<ListModeVo>> {

	@Override
	public List<ListModeVo> parseXML(InputStream is) throws Exception {

		List<ListModeVo> listModeList = new ArrayList<ListModeVo>();
		ListModeVo vo = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CHARSET);

		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;	
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("complaintEntity")) {
					vo = new ListModeVo();					
				}
				else if (parser.getName().equals("title")) {
					parser.next();
					vo.setTitle(parser.getText());					
				}else if (parser.getName().equals("count")) {
					parser.next();
					vo.setCount(Integer.parseInt(parser.getText()));		
				}else if (parser.getName().equals("address")) {
					parser.next();
					vo.setAddress(parser.getText());					
				}else if (parser.getName().equals("longitude")) {
					parser.next();
					vo.setLongitude(Float.parseFloat(parser.getText()));					
				}else if (parser.getName().equals("latitude")) {
					parser.next();
					vo.setLatitude(Float.parseFloat(parser.getText()));					
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("complaintEntity")) {
					listModeList.add(vo);
					vo = null;	
				}				
				break;				
			}
			eventType = parser.next();				
		}
		return listModeList;
	}

}
