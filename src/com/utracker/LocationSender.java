package com.utracker;

import java.util.ArrayList;

import com.baidu.location.BDLocation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class LocationSender {
	private final String TAG = "LocationSender";
	
	
	Context context = null;
	LocationApplication application = null;
	SmsManager sms = null;
	PendingIntent sentPI= null;
	PendingIntent deliveredPI= null;
	
	public LocationSender(){
		
		context = LocationApplication.getInstance().getApplicationContext();
		this.application= LocationApplication.getInstance();
		smsManagerInit();
	}
	
	/**
	 * Sends 
	 */
	public  void sendDetailBySms(String phoneNumber, String extra){
		BDLocation location = application.getLastLocation();
		String message;
		if (location !=null)
			message = 
				extra +
				location.getTime()+"\n"
				+application.getLastAddress()+"\n"
				+location.getLongitude()
				+" & "+ location.getLatitude()
				+ "�뾶��"+(location.hasRadius()?(int)location.getRadius()+"":"");
		else
			message = "Sorry , location unavailable.";
		this.sendSMSByDevice(message, phoneNumber);
	}
	
	public void sendSMSByDevice(String message , String phoneNumber){    
        if(message.length()>=70&&!Util.isTrace(message)){
            ArrayList<String> msgs = sms.divideMessage(message);
             for (String msg : msgs) {     
        		 sms.sendTextMessage(phoneNumber, null,msg, sentPI, deliveredPI);
        	 }      
         }
        else
        {       
        	sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);    
        }
	}
	
	
	
	public void sendURLBySms(String phoneNumber){
		BDLocation location = application.getLastLocation();
		String message;
		if (location !=null)
			message = Util.makeURLCenter(location.getLongitude(),
						location.getLatitude());
		else
			message = "Sorry , location unavailable.";
		sendSMSByDevice(message, phoneNumber);
		
	}
	

	public void sendTraceBySms(String phoneNumber) {
		// TODO Auto-generated method stub
		Cursor locations = application.getDBLocations(10);
		String msg;
		if(locations!=null)
			msg= Util.traceToString(locations);
		else 
			msg = "Sorry , trace unavailable.";
		//Log.i(TAG , "Trace msg");
		sendSMSByDevice(msg, phoneNumber);
	}
	private void smsManagerInit()
    {        
		 	String SENT = "SMS_SENT";
	        String DELIVERED = "SMS_DELIVERED";
	 
	        sentPI = PendingIntent.getBroadcast(context, 0,
	            new Intent(SENT), 0);
	 
	        deliveredPI = PendingIntent.getBroadcast(context, 0,
	            new Intent(DELIVERED), 0);
	 
	        //---when the SMS has been sent---
	        context.registerReceiver(new BroadcastReceiver(){
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	                switch (getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Toast.makeText(context, "SMS sent", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                        Toast.makeText(context, "Generic failure", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NO_SERVICE:
	                        Toast.makeText(context, "No service", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_NULL_PDU:
	                        Toast.makeText(context, "Null PDU", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case SmsManager.RESULT_ERROR_RADIO_OFF:
	                        Toast.makeText(context, "Radio off", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                }
	            }
	        }, new IntentFilter(SENT));
	 
	        //---when the SMS has been delivered---
	        context.registerReceiver(new BroadcastReceiver(){
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	                switch (getResultCode())
	                {
	                    case Activity.RESULT_OK:
	                        Toast.makeText(context, "SMS delivered", 
	                                Toast.LENGTH_SHORT).show();
	                        break;
	                    case Activity.RESULT_CANCELED:
	                        Toast.makeText(context, "SMS not delivered", 
	                                Toast.LENGTH_SHORT).show();
	                        break;                        
	                }
	            }
	        }, new IntentFilter(DELIVERED));        
	 
	        sms = SmsManager.getDefault();       
    }

}
