package com.utracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Setting {
	public static final String TAG = "Setting";
	/**
	 * Security settings.
	 */
	public static boolean trackedEnable= true;
	public static boolean requirePassword = false;
	//public static boolean notifyOnEscape = true;		
	public static String password="0000";
	public static String trackerPhoneNumber= "18810666210";
	
	public static boolean isURLIncluded = true;
	public static boolean isDetailIncluded = true;	
	
	public static boolean showCommandSMS = false;
	public static boolean autoAnswerCall = false;
	public static boolean autoCallBack = false;
	
	public static boolean autoStart = false;
	public static boolean notifyOnCommand = true;
	
	
	public static void initSecuritySettings(Context context)throws Exception{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		trackedEnable = pref.getBoolean("trackedEnable", false);
		password = Util.decrypt(pref.getString("password", Util.encrypt("0000")));
		requirePassword = pref.getBoolean("requirePassword", false);
		String tempPhone = pref.getString("trackerPhoneNumber", "18810666210");
		if(Util.isPhoneNumber(tempPhone))
			trackerPhoneNumber = tempPhone;
		else 
			throw new Exception("Invalid phonenumber format");		
		isURLIncluded = pref.getBoolean("isURLIncluded", true);
		isDetailIncluded = pref.getBoolean("isDetailIncluded", true);
		
		showCommandSMS = pref.getBoolean("showCommandSMS", false);
		autoAnswerCall = pref.getBoolean("autoAnswerCall", false);
		autoCallBack = pref.getBoolean("autoCallBack", false);
		autoStart = pref.getBoolean("autoStart", false);
		notifyOnCommand = pref.getBoolean("notifyOnCommand", true);
		Log.i(TAG, "init security settings");
	}
	
	
	/**
	 * Location and track settings.
	 */
	public static double scanSpanSec = 1.5;
	
	public static int distanceLimit = 800;//meters
	public static int trackInterval = 500;
	public static int traceLength =10;
	public static int accuracyLimit = 2000;
	public static double centerLongitude = 116.279009;
	public static double centerLatitude = 40.160213;
	public static void setCenterLocation(double lon, double lat){
		Setting.centerLongitude=lon;
		Setting.centerLatitude=lat;
		
	}
	public static void initLocationSettings(Context context) throws Exception{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		scanSpanSec = Double.parseDouble(pref.getString("scanSpanSec", "30"));
		distanceLimit = Integer.parseInt(pref.getString("distanceLimit", "1000"));
		trackInterval = Integer.parseInt(pref.getString("trackInterval", "500"));
		traceLength = Integer.parseInt(pref.getString("traceLength", "10"));
		accuracyLimit = Integer.parseInt(pref.getString("accuracyLimit", "2000"));
		centerLongitude= 
				Double.parseDouble(pref.getString("centerLongitude",116.279009+"" ));
		centerLatitude= 
				Double.parseDouble(pref.getString("centerLatitude",40.160213+"" ));		
		Log.i(TAG, "init location settings");
	}
	
	
	/**
	 * Refreshes all settings.
	 * @param context
	 * @throws Exception 
	 */
	public static void refreshSettingsFromPref(Context context) throws Exception{
		initSecuritySettings(context);
		initLocationSettings(context);
	}
	
	
	
	

}
