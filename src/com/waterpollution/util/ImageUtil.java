package com.waterpollution.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextPaint;

public class ImageUtil {

	public static InputStream getRequest(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		if (conn.getResponseCode() == 200){
			return conn.getInputStream();
		}
		return null;
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}
	
	public static Drawable loadImageFromUrl(String url){
        URL m;
        InputStream i = null;
        try {
            m = new URL(url);
            i = (InputStream) m.getContent();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }
	
	public static Drawable getDrawableFromUrl(String url) throws Exception{
		 return Drawable.createFromStream(getRequest(url),null);
	}
	
	public static Bitmap getBitmapFromUrl(String url) throws Exception{
		byte[] bytes = getBytesFromUrl(url);
		return byteToBitmap(bytes);
	}
	
	public static Bitmap getRoundBitmapFromUrl(String url,int pixels) throws Exception{
		byte[] bytes = getBytesFromUrl(url);
		Bitmap bitmap = byteToBitmap(bytes);
		return toRoundCorner(bitmap, pixels);
	} 
	
	public static Drawable geRoundDrawableFromUrl(String url,int pixels) throws Exception{
		byte[] bytes = getBytesFromUrl(url);
		BitmapDrawable bitmapDrawable = (BitmapDrawable)byteToDrawable(bytes);
		return toRoundCorner(bitmapDrawable, pixels);
	} 
	
	public static byte[] getBytesFromUrl(String url) throws Exception{
		 return readInputStream(getRequest(url));
	}
	
	public static Bitmap byteToBitmap(byte[] byteArray){
		if(byteArray.length!=0){ 
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length); 
        } 
        else { 
            return null; 
        }  
	}
	
	public static Drawable byteToDrawable(byte[] byteArray){
		ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
		return Drawable.createFromStream(ins, null);
	}
	
	public static byte[] Bitmap2Bytes(Bitmap bm){ 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	 	/**
	      * ͼƬȥɫ,���ػҶ�ͼƬ
	      * @param bmpOriginal �����ͼƬ
	     * @return ȥɫ���ͼƬ
	     */
	    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
	        int width, height;
	        height = bmpOriginal.getHeight();
	        width = bmpOriginal.getWidth();    
	
	        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	        Canvas c = new Canvas(bmpGrayscale);
	        Paint paint = new Paint();
	        ColorMatrix cm = new ColorMatrix();
	        cm.setSaturation(0);
	        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	        paint.setColorFilter(f);
	        c.drawBitmap(bmpOriginal, 0, 0, paint);
	        return bmpGrayscale;
	    }
	    
	    
	    /**
	     * ȥɫͬʱ��Բ��
	     * @param bmpOriginal ԭͼ
	     * @param pixels Բ�ǻ���
	     * @return �޸ĺ��ͼƬ
	     */
	    public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
	        return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	    }
	    
	    /**
	     * ��ͼƬ���Բ��
	     * @param bitmap ��Ҫ�޸ĵ�ͼƬ
	     * @param pixels Բ�ǵĻ���
	     * @return Բ��ͼƬ
	     */
	    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
	
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);
	
	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);
	        final float roundPx = pixels;
	
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	
	        return output;
	    }
	
	    
	   /**
	     * ʹԲ�ǹ���֧��BitampDrawable
	     * @param bitmapDrawable 
	     * @param pixels 
	     * @return
	     */
	    @SuppressWarnings("deprecation")
		public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
	        Bitmap bitmap = bitmapDrawable.getBitmap();
	        bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
	        return bitmapDrawable;
	    }
	    //Text To Bmp
	    public synchronized static Bitmap TextToBmp(Bitmap map,String Head,String Detail){
	    		//拆分字符串
	    		String a[] = Detail.split(";"); 
	    		String snum ="";
	    		String address;
	    		String shead =Head;
	    		if (a.length>1){
	    			snum = a[1]+"次";
	    		}
	    		
	    		if ((Head+snum).length() >16){
	    			shead = Head.substring(0, 13)+".. "+snum;   
	    		}
	    		else{
	    			shead = Head+" "+snum;
	    		}
	    		
	    		if (a[0].length() >16){
	    			address = a[0].substring(0, 15)+".. ";   
	    		}
	    		else{
	    			address = a[0];
	    		}
	    		
	    		
	    		
	    		
	    		Paint paint = new TextPaint(); 
	    		Paint paint2 = new TextPaint(); 
            	paint.setTextSize(24f);
            	paint.setTypeface(Typeface.DEFAULT_BOLD);
            	paint.setARGB(255,255,255,255);
            	
            	paint2.setTextSize(20f);
            	paint2.setARGB(255,255,255,255);
            	
            	
            	FontMetrics hfm = paint.getFontMetrics();
            	int headh = (int)Math.ceil(hfm.descent - hfm.ascent);  
            	FontMetrics dfm = paint2.getFontMetrics();
            	int detailh = (int)Math.ceil(dfm.descent - dfm.ascent);  
	    		
	    	 int x = 5,y=headh,y1=headh+detailh;  
	    	 int w = map.getWidth();
	    	 int h = map.getHeight();
	    	 Rect sRect = new Rect(0, 0, w, h);	    	 
	    	 Bitmap bitmap =null;

	    	w = (int)paint.measureText(shead);

	    	 if (paint2.measureText(address)>w){
	    		 w = (int)paint2.measureText(address);
	    	 }
	    	 w= w+5;
             try {  
            	 bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);  
                 Canvas canvas = new Canvas(bitmap);  
    	    	 @SuppressWarnings("unused")
				Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h);                 
            	//Canvas canvas = new Canvas();  

    	    	 Rect dRect = new Rect(0, 0, w, h);

            	canvas.drawBitmap(map,sRect,dRect,null);
            	canvas.drawText(shead ,x, y, paint);  
            	canvas.drawText(address ,x, y1, paint2);  
            
             canvas.save(Canvas.ALL_SAVE_FLAG);  
             //canvas.restore();  
             String path = Environment.getExternalStorageDirectory() + "/image1.png"; 
             
             System.out.println(path);  
                 FileOutputStream os = new FileOutputStream(new File(path));  
                 bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);  
                 os.flush();  
                 os.close(); 
             }  
                catch (Exception e) {  
                 // TODO Auto-generated catch block  
                 e.printStackTrace();  
             }  	    	
	    	
	    	return bitmap;
	    }
	    
	
}
