package com.waterpollution.parser;

import java.io.InputStream;

public abstract class BaseParser<T> {

	public static final String CHARSET = "utf-8";

	public abstract T parseXML(InputStream is) throws Exception;

}
