package com.waterpollution;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;

import org.apache.http.entity.FileEntity;

//httpClient客户端处理类
//GetHttpData得到数据
//PostHttpData提交数据


public class httpClient {

	private String methodType ="GET";
	private String CodeType ="UTF-8";
	public httpClient(){
		
		
	}
	public String PostHttpData(String HttpAdd,InputStream is) throws Exception{
		String Ret="";
		InputStream outis = PostHttpData(HttpAdd,is,"POST");
		Ret = ReadData(outis,CodeType);
	
		return Ret;		
		
	}
	public InputStream PostHttpData(String HttpAdd,InputStream is,String Type) throws Exception{
		String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
//        http://193...../index.php?sdfsdsfsjaj-aassl
        	
		URL url =  new URL(HttpAdd);		
		HttpURLConnection conn =  (HttpURLConnection)url.openConnection();
		String AType =Type;
		if (Type ==""){
			AType = "POST";
		}				
		conn.setConnectTimeout(6*1000);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setRequestMethod(AType);
		conn.setRequestProperty("Connection","Keep-Alive");
		conn.setRequestProperty("Charset","utf-8");
		
		//conn.setRequestProperty("Conntent-Length",String.valueOf(is.l)
		conn.setRequestProperty("Conntent-Type","multipart/form-data;boundary="+boundary);

		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
		/* 设置每次写入1024bytes */
        int bufferSize =1024;
        byte[] buffer =new byte[bufferSize];
        int length =-1;
        /* 从文件读取数据至缓冲区 */
        if (is !=null){
        while((length = is.read(buffer)) !=-1)
        {
          /* 将资料写入DataOutputStream中 */
        	outStream.write(buffer, 0, length);
        }		
        }
		
        outStream.flush();
		try {
			if (conn.getResponseCode()!=200) {		
				throw new RuntimeException("URL取得失败!");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}        
        
        InputStream isret = conn.getInputStream();
        
        outStream.close();
		
		return isret;
	}
	
	public String GetHttpData(String HttpAdd) throws Exception{
		String Ret="";
		InputStream is = GetHttpData(HttpAdd,methodType);
		Ret = ReadData(is,CodeType);
	
		return Ret;
		
	}
	public InputStream GetHttpData(String HttpAdd,String Type) throws Exception{
		URL url =  new URL(HttpAdd);

		HttpURLConnection conn =  (HttpURLConnection)url.openConnection();
		String AType =Type;
		if (Type ==""){
			AType = methodType;
		}		
		conn.setConnectTimeout(6*1000);
		conn.setReadTimeout(6*1000);

		conn.setRequestMethod(AType);
//		conn.connect();
		
//		try {
//			conn.connect();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			conn.disconnect();
//		}
		
		try {
			if (conn.getResponseCode()!=200) {		
				throw new RuntimeException("URL取得失败!");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream is =conn.getInputStream();
		//conn.disconnect();

		return is;
		
		
	}
	public String ReadData(InputStream is,String CodeT) throws Exception{
		ByteArrayOutputStream outS = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while((len = is.read(buffer))!=-1){
			outS.write(buffer, 0, len);
		}
		byte[] data = outS.toByteArray();
		outS.close();
		is.close();
		return new String(data,CodeT);
		

	}
}
