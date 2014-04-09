package com.utracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.utracker.activity.MainActivity;
import com.utracker.activity.MapActivity;
import com.utracker.receiver.PhoneReceiver;
import com.utracker.receiver.SmsReceiver;

public class LocationApplication extends Application{
	public static boolean showNotification = false;
	public static boolean isAppRunning = false;
	
	private static LocationApplication mInstance = null;
	private String TAG = "LocationApplication";
	
	private BMapManager bMapManager = null;
	public static final String strKey = "05CA829B3973E806E8F15FB6023963AB0A20B076";
		
	private LocationClient bLocationClient = null;	
	private BDLocation lastLocation = null;
	private String lastAddress = "";	
	
	private LocationSender locationSender= null;
	public MainActivity main = null;
	public MapActivity map = null;
	
	private DatabaseHelper dbHelper = null;
	
	public static int counter = 1;
	
	//AlarmManager alarm = null;
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		//alarm = (AlarmManager) this.getSystemService(ALARM_SERVICE);		
		//long firstime=SystemClock.elapsedRealtime();
		//alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
         //   , firstime, 5*1000, null);		
		//if(!LocationApplication.isAppRunning){
		//	this.isAppRunning = true;
		//	startClient();
		//	openDatabase();
		//	openMapManager();			
		//}
		//if(!this.isStarted()) startApp();
		Log.d(TAG,"APP onCreate");
		}
	private LocationClientOption getDefaultClientOption(){
		LocationClientOption option = new LocationClientOption();		
		option.setAddrType("all");
		option.setPriority(LocationClientOption.GpsFirst);
		option.disableCache(true);
		option.setOpenGps(true);
		option.setScanSpan((int) (Setting.scanSpanSec*1000));
		option.setCoorType("bd09ll");
		return option;
	}
	
	public void requestLocation(){
		if(bLocationClient!=null)
			bLocationClient.requestLocation();
	}
	
	public void startClient(){
		if(bLocationClient !=null&& !bLocationClient.isStarted())
			bLocationClient.start();
	}
	
	public boolean isStarted(){
		return bLocationClient!=null&&bLocationClient.isStarted();
	}
	
	
	public void openDatabase(){
		if(dbHelper!=null&&!dbHelper.isOpened())
			dbHelper.open();
	}
	
	public void openMapManager(){
		if(bMapManager!=null)bMapManager.init(strKey, new MyGeneralListener());
	}
	
	public void startApp(){
		if (this.isStarted()) return;
	//	if(this.isAppRunning) return;
	//	this.isAppRunning = true;
		Log.d(TAG,"START APP");
		if (dbHelper == null){
			Log.d(TAG,"New Helper");
			dbHelper = new DatabaseHelper(this);
		}
		dbHelper.open();
		
		/**
		 * Initial MapManager
		 */
		if(bMapManager==null)
			bMapManager = new BMapManager(this);
		
		/**
		 * Settings are loaded from preference files.
		 */
		try {
			Setting.refreshSettingsFromPref(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**
		 * Register BroadcastReceiver. 
		 */
		IntentFilter filter = new IntentFilter(SmsReceiver.SMS_RECEIVED_ACTION);  
	    filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
	    
		SmsReceiver smsReceiver = new SmsReceiver();
		registerReceiver(smsReceiver, filter); 
		
		PhoneReceiver phoneReceiver = new PhoneReceiver();  
	    IntentFilter intentFilter = new IntentFilter();  
	    intentFilter.addAction(PhoneReceiver.B_PHONE_STATE);  
	    intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);  
	    registerReceiver(phoneReceiver, intentFilter);
		
	    /**
	     * Initializes location sender.
	     */
	    
	    locationSender = new LocationSender();
	    
	    
		/**
		 * Initialize location client.
		 */
	    if(bLocationClient==null)
	    	bLocationClient = new LocationClient(this);		
		bLocationClient.setLocOption(getDefaultClientOption());		
		bLocationClient.registerLocationListener(new MyLocationListenner());
		//bLocationClient.start();


		if(!LocationApplication.showNotification){
					LocationApplication.getInstance().showNotification();
		}
				
		startClient();
		bLocationClient.requestLocation();
		openDatabase();
		openMapManager();
	}

	/**
	 * Exit application.
	 */
	public void exit(){
		this.hideNotification();
		
		Setting.trackedEnable = false;
		//this.started = false;
		if(bLocationClient !=null&& bLocationClient.isStarted()) bLocationClient.stop();
		if(dbHelper!=null &&dbHelper.isOpened()) dbHelper.close();
		
		//if (bMapManager != null) {
        //    bMapManager.destroy();
        //    bMapManager = null;
        //}
		
		
		//this.stopService(name);		
		//try {
		//	Thread.sleep(200);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		//System.exit(0);
	}
	public void onTerminate() {
	//	this.isAppRunning = false;
		// TODO Auto-generated method stub
	    if (bMapManager != null) {
            bMapManager.destroy();
            bMapManager = null;
        }
	    
		super.onTerminate();
		Log.i(TAG,"Utracker terminated");
	}
	
	 static class MyGeneralListener implements MKGeneralListener {
	        
	        @Override
	        public void onGetNetworkState(int iError) {
	            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
	                Toast.makeText(LocationApplication.getInstance().getApplicationContext(), "Network connection error",
	                    Toast.LENGTH_LONG).show();
	            }
	            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
	                Toast.makeText(LocationApplication.getInstance().getApplicationContext(), "Network data error",
	                        Toast.LENGTH_LONG).show();
	            }
	            // ...
	        }

	        @Override
	        public void onGetPermissionState(int iError) {
	            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
	                Toast.makeText(LocationApplication.getInstance().getApplicationContext(), 
	                        "Invalid Key！", Toast.LENGTH_LONG).show();
	                
	            }
	        }
	    }
	/**
	 * Listener for Location client.
	 */
	public class MyLocationListenner implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation location) {
			
			//Log.i(TAG,"Counter "+LocationApplication.counter++);			
		
			
			//Log.i(TAG,"########");
			//if(Setting.isScanSpanChanged){
			//	setScanSpan(Setting.scanSpanSec);
			//}
						
			if(location==null){
				Log.d("getLocation","try again");
				return;
			}
			if(location.hasRadius())
				if(location.getRadius()>Setting.accuracyLimit) return;
			if(location.getLatitude()>180||location.getLatitude()<-180||
					location.getLongitude()>180||location.getLongitude()<-180)
				return;
			/**
			 * Updates location info and refresh Main activity.
			 */
			lastLocation = location;
			
			if (location.hasAddr())	lastAddress = location.getAddrStr();
			Log.i(TAG,lastLocation.getTime()+lastAddress);
			if(main!=null) main.onGetLocation();
			if(map!=null) map.OnGetLocation();
			
			/**
			 * Sends warning and switches running mode.
			 */
			if(isLocationChanged()&&Setting.trackedEnable){
				// Send SMS warning to tracker
				if(BuildConfig.DEBUG)Log.i(TAG, "LocationChanged");
				if(Setting.isDetailIncluded){
					locationSender.sendDetailBySms(Setting.trackerPhoneNumber,"警告:\n");
					notifyMessage("Detail sent","Auto report to "+ Setting.trackerPhoneNumber);
				}
				if(Setting.isURLIncluded){
					locationSender.sendURLBySms(Setting.trackerPhoneNumber);
					notifyMessage("URL sent","Auto report to "+ Setting.trackerPhoneNumber);
				}
				if(dbHelper!=null && dbHelper.isOpened())
					try{
						dbHelper.insertLocation(location);
					}catch(SQLiteException se){
						se.printStackTrace();
					}
			}
			
			
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	/**
	 * Gets instance of Application
	 * @return
	 */
	public static LocationApplication getInstance(){
		return mInstance;
	}
	
	public Cursor getDBLocations(int length){
		if (dbHelper!=null &&dbHelper.isOpened())
			return dbHelper.getLocation(length);
		else return null;
	}
	
	public void insertTrace(String phoneNumber ,String time, String msg){
		if (dbHelper!=null &&dbHelper.isOpened())
			dbHelper.insertTrace(phoneNumber, time, msg);
	}
	public Cursor getDBTraces(){
		if (dbHelper!=null &&dbHelper.isOpened())
			return dbHelper.getTrace();
		else return null;
	}
	
	public LocationSender getLocationSender(){
		return this.locationSender;
	}
	
	
	public BDLocation getLastLocation(){
		return this.lastLocation;
	}
	public String getLastAddress(){
		return this.lastAddress;
	}
	
	public void setScanSpan(double span){
		bLocationClient.getLocOption().setScanSpan((int) (span*1000));
		//Setting.isScanSpanChanged= false;
	}
	
	
	
	public  static final int MONITOR_MODE = 0 ;
	public static final int TRACK_MODE = 1;
	public  int runMode = 0;
	private double lastCenterLongitude=0;
	private double lastCenterLatitude=0;
	
	private void updateLastCenter(){
		lastCenterLongitude = lastLocation.getLongitude();
		lastCenterLatitude = lastLocation.getLatitude();
	}
	public double getCenterDistance(){
		return Util.GetDistance(lastLocation.getLatitude(), 
				lastLocation.getLongitude(),
				Setting.centerLatitude,
				Setting.centerLongitude);
	}
	public double getIntervalDistance(){
		return  Util.GetDistance(lastLocation.getLatitude(), 
				lastLocation.getLongitude(),
				lastCenterLatitude,
				lastCenterLongitude);
	}
	private boolean isLocationChanged(){
		if(runMode==MONITOR_MODE){			
	//	double r = lastLocation.hasRadius()? lastLocation.getRadius()/2.0:0;
			if(getCenterDistance()>Setting.distanceLimit) {
				runMode = TRACK_MODE;
				updateLastCenter();
				return true;
			}
			else return false;
			
		}else if(runMode==TRACK_MODE){			
			if(getCenterDistance()<Setting.distanceLimit)
				runMode = MONITOR_MODE;			
			if(getIntervalDistance()>Setting.trackInterval){
				updateLastCenter();
				return true;
			}
			else return false;
			
		}else return false;
	}
	
	public void showNotification(){
		this.showNotification = true;
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification notification = new Notification(R.drawable.ic_launcher,"Utracker started",System.currentTimeMillis()); //此处定义了一个Notification ，其中第一个参数代表图标


    	  PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);//该语句的作用是定义了一个不是当即显示的activity，只有当用户拉下notify显示列表，并且单击对应的项的时候，才会触发系统跳转到该activity.

    	  notification.setLatestEventInfo(this, "Utracker", "Application running", contentIntent);//在此处设置在nority列表里的该norifycation得显示情况。
    	  notification.flags = Notification.FLAG_ONGOING_EVENT;
    	  nm.notify(R.string.app_name, notification);
	}
	public void hideNotification(){
		this.showNotification = false;
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.string.app_name);
	}
	public void notifyMessage(String title, String message){
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification notification = new Notification(R.drawable.user_center_address_icon,message,System.currentTimeMillis()); //此处定义了一个Notification ，其中第一个参数代表图标
    	//  PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);//该语句的作用是定义了一个不是当即显示的activity，只有当用户拉下notify显示列表，并且单击对应的项的时候，才会触发系统跳转到该activity.
    	notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE;  
    	notification.flags = Notification.FLAG_AUTO_CANCEL;
    	notification.setLatestEventInfo(this, title , message, null);//在此处设置在nority列表里的该norifycation得显示情况。
    	nm.notify(0, notification);
	}


}
