package com.utracker;


import java.util.ArrayList;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.mapapi.utils.DistanceUtil;
import android.database.Cursor;

public class Util {
	/**
	 * Gets distance using latitude and longitude.
	 */
	private final static double EARTH_RADIUS = 6378137.0;//meters
	private static double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
	{
		/*
	   double radLat1 = rad(lat1);
	   double radLat2 = rad(lat2);
	   double a = radLat1 - radLat2;
	   double b = rad(lng1) - rad(lng2);
	   double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
	    Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	   s = s * EARTH_RADIUS;
	   s = Math.round(s * 10000) / 10000.0;
	   return s;
	   */
		GeoPoint g1 = new GeoPoint((int)(lat1*1e6),(int)(lng1*1e6));
		GeoPoint g2 = new GeoPoint((int)(lat2*1e6),(int)(lng2*1e6));
		return DistanceUtil.getDistance(g1,g2);
	}
	
	/**
	 * Encrypts password
	 * @param inStr
	 * @return
	 */
	 public static String encrypt(String inStr) {  
		  // String s = new String(inStr);  
		  char[] a = inStr.toCharArray();  
		  for (int i = 0; i < a.length; i++) {  
		   a[i] = (char) (a[i] ^ 't');  
		  }   
		  return new String(a);  
		 } 
	public static String decrypt(String inStr) {  
		char[] a = inStr.toCharArray();  
		for (int i = 0; i < a.length; i++) {  
			a[i] = (char) (a[i] ^ 't');  
		}  			  
		return new String(a);  
	}
	
	public static String getPasswordFromMessage(String msg){
		int separator = msg.indexOf("#");
		if(separator==-1)	return "";
		else return msg.substring(0, separator);
	}
	
	public static String getCommandFromMessage(String msg){
		int separator = msg.indexOf("#");
		if(separator == -1) return "";
		else return msg.substring(separator+1);
	}
	public static boolean isPhoneNumber(String str){
		if(str.length()<11||str.length()>16) return false;
		for(int i= 0 ;i<str.length();i++ ){
			if(str.charAt(i)!='+'&& (str.charAt(i)>'9'||str.charAt(i)<'0'))
				return false;
		}
		return true;
	}
	
	public static String makeSMS(String str1,String str2,String str3,
			String str4){
		return str1+"\n"
				+str2+"\n"
				+str3+"\n"
				+str4+"\n";
	}
	
	//public static final int URL_CENTER = 0 ;
	public static String makeURLCenter(double lon, double lat){
		return "http://api.map.baidu.com/staticimage?"
				+"width=400&height=300" +
				"&center="+lon+","+lat+
				"&zoom=15" +
				"&markers="+lon+","+lat+
				"&markerStyles=s,";
	
	}
	
	public static ArrayList<String> traceToStrlist(Cursor cursor){
		int length = 0;
		String str = null;
		
		ArrayList<String> strList = new ArrayList<String>();
		
	    cursor.moveToFirst();

	    int lon = (int)(cursor.getDouble(1)*1e6);
	    int lat = (int)(cursor.getDouble(2)*1e6);
	    str = lon+" "+lat+" ";
		length = length +str.length();
		strList.add(str);
		
		while(cursor.moveToNext()){
			 str = (int)(cursor.getDouble(1)*1e6-lon)+" "
						+(int)(cursor.getDouble(2)*1e6-lat)+" ";
			if(length+str.length()>140) break;
			else strList.add(str);
			}
		return strList;
	}
	
	public static String traceToString(Cursor cursor){
		ArrayList<String> strList = traceToStrlist(cursor);
		StringBuffer buff = new StringBuffer();
		for(String s :strList){
			buff.append(s);
		}
		return buff.toString();
	}
	
	public static ArrayList<GeoPoint> strlistToTrace(String str){
		ArrayList<String> strList = stringToStrlist(str);
		ArrayList<GeoPoint> geoList = new ArrayList<GeoPoint>();
		

		int baseLon = Integer.parseInt(strList.get(0));
		int baseLat = Integer.parseInt(strList.get(1));
		geoList.add(new GeoPoint(baseLat,baseLon));
		
		int len = strList.size()/2;
		for(int i = 1 ;i<len;i++){
			int lon = Integer.parseInt(strList.get(i*2))+baseLon;
			int lat = Integer.parseInt(strList.get(i*2+1))+baseLat;
			geoList.add(new GeoPoint(lat,lon));
		}		
		return geoList;
	}
	public static ArrayList<String> stringToStrlist(String msg){
				
		ArrayList<String> strList = new ArrayList<String>();
		int len = msg.length();
		int left = -1;
		int right =0;
		boolean flag = false;
		
		for(int i = 0 ;i<len;i++){
			char c = msg.charAt(i);
			if(c==' '){
				flag = true;
				right = i;
			}
			if(flag){
				strList.add(msg.substring(left+1,right));
				left = right;
				flag = false;
			}
		}
		return strList;
		
	}
	public static boolean isTrace(String msg){
		int len = msg.length();
		
		for(int i = 0 ;i<len;i++){
			char c= msg.charAt(i);
			if((c>'9'||c<'0')&&c!='-'&&c!=' ') return false;
		}
		//if (len< 50) return false;
		return true;
	}
}
