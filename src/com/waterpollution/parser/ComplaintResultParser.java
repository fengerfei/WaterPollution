package com.waterpollution.parser;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.waterpollution.vo.ComplaintResultVo;

public class ComplaintResultParser extends BaseParser<ComplaintResultVo> {

	@Override
	public ComplaintResultVo parseXML(InputStream is) throws Exception {
		ComplaintResultVo vo = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CHARSET);
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("result")) {
					vo = new ComplaintResultVo();
					vo.setResult(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}
		return vo;
	}

}
