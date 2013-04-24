package com.waterpollution.vo;

import java.util.Map;

import android.content.Context;

import com.waterpollution.parser.BaseParser;

public class RequestVo {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public int requestUrl;
	public Context context;
	public Map<String, String> requestDataMap;
	public BaseParser<?> xmlParser;
	public String method = METHOD_GET;

	public RequestVo() {
	}

	public RequestVo(int requestUrl, Context context,
			Map<String, String> requestDataMap, BaseParser<?> xmlParser,
			String method) {
		super();
		this.requestUrl = requestUrl;
		this.context = context;
		this.requestDataMap = requestDataMap;
		this.xmlParser = xmlParser;
		this.method = method;
	}
}
