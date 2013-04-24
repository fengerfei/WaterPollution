package com.waterpollution.parser;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.waterpollution.vo.ListModeVo;

public class FirstCommentParser extends BaseParser<ListModeVo> {

	//TODO ??
	@Override
	public ListModeVo parseXML(InputStream is) throws Exception{
		ListModeVo vo = new ListModeVo();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CHARSET);
		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("content")) {
					parser.next();
					vo.setContent(parser.getText());
				} else if (parser.getName().equals("thumb")) {
					parser.next();
					vo.setImage(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("complaintInfo")) {
					return vo;
				}
				break;
			}
			eventType = parser.next();
		}
		return vo;
	}
}
