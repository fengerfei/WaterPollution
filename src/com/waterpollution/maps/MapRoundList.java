package com.waterpollution.maps;

import java.io.InputStream;

import com.waterpollution.httpClient;
import com.waterpollution.application.WPApplication;
import com.waterpollution.parser.ComplaintParser;

public class MapRoundList{
	WPApplication wpapp;
	public int range = 2000;
public MapRoundList(WPApplication app){
	wpapp =app;
}
public void GetRoundList(double latitude ,double longitude){

//	GeoPoint p = new GeoPoint((int)(latitude* 1e6), (int)(longitude *  1e6));
	//取数�?
	String  httpcon ="http://42.120.23.245/Iphone1.2/index.php?m=compliant&a=bylatlong&longitude="+longitude+"&latitude="+latitude+"&range="+range;
		
	InputStream is = null;
	try {
		httpClient hc =new httpClient();
		
		is = hc.GetHttpData(httpcon, "GET");
	} catch (Exception e) {
		e.printStackTrace();
	}

	ComplaintParser cr =new ComplaintParser();

	try {
		wpapp.complaintEntitylist = cr.parsecomplaintEntity(is, "utf-8");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		
		e.printStackTrace();
	}
		
}

}
