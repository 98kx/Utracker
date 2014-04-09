package com.utracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AutoStartService extends Service{
	public static boolean start = false;
	private String TAG = "AutoStartService";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onBinde");
		return null;
	}
	 @Override  
	public void onCreate(){
		 Log.d(TAG, "Start");
	 }
	public void onStart(Intent intent, int startId) {  
		 	Log.d(TAG, "Start");
	        LocationApplication.getInstance().startApp();  
	}

}
