package com.waterpollution.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.waterpollution.vo.RankingVo;

/**
 * 城市排行数据解析器
 * @author liu
 *
 */
public class RankingParser extends BaseParser<List<RankingVo>>{

	@Override
	public List<RankingVo> parseXML(InputStream is) throws Exception  {

		List<RankingVo> rankList = new ArrayList<RankingVo>();
		RankingVo vo = null;
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, CHARSET);

		int eventType = parser.getEventType();

		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("cityInfo")) {
					vo = new RankingVo();
				} else if (parser.getName().equals("cityname")) {
					parser.next();
					vo.setCityName(parser.getText());
				} else if (parser.getName().equals("objectnums")) {
					parser.next();
					vo.setObjectNums(Integer.parseInt(parser.getText()));
				} else if (parser.getName().equals("compliantnums")) {
					parser.next();
					vo.setCompliantNums(Integer.parseInt(parser.getText()));
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("cityInfo")) {
					rankList.add(vo);
					vo = null;
				}
				break;
			}
			eventType = parser.next();
		}
		return rankList;
	}

}
