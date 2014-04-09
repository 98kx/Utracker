package com.utracker.receiver;

import com.utracker.LocationApplication;
import com.utracker.Setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class PhoneReceiver extends BroadcastReceiver{
	public static boolean answerMe = false;
	public static String answerPhoneNumber = null;
	
	public final static String B_PHONE_STATE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;  
	private final String TAG = "PhoneReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		 if(!Setting.autoAnswerCall) return;
		 if(!answerMe) return;
		 String action = intent.getAction();  
	        Log.i(TAG, "[Broadcast]"+action);  
	          
	        
	     if(action.equals(B_PHONE_STATE)){		
	    	 String phoneNumber = intent.getStringExtra(  
		        		TelephonyManager.EXTRA_INCOMING_NUMBER);
	    	 Log.i(TAG, "Call from "+phoneNumber);
	    	 if(!phoneNumber.equals(answerPhoneNumber)) return;
	    	 autoAnswerCall();
	    	 answerMe = false;
	     }
		
	}
	
	synchronized void autoAnswerCall(){
		 
		Context context = LocationApplication.getInstance();
		 
		   try
		 
		         {
			   		//Set silent ringer mode.
			   		int mode ;
			   		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			   		mode= mAudioManager.getRingerMode();
			   		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		         
		 
		             Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
		 
		             localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		 
		             localIntent1.putExtra("state", 1);
		 
		             localIntent1.putExtra("microphone", 1);
		 
		             localIntent1.putExtra("name", "Headset");
		 
		             context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
		 
		             
		 
		             Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		 
		             KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
		 
		             localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
		 
		             context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
		 
		           
		             Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		 
		             KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
		 
		             localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
		 
		             context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
		 
		             
		             Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
		 
		             localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		 
		             localIntent4.putExtra("state", 0);
		 
		             localIntent4.putExtra("microphone", 1);
		 
		             localIntent4.putExtra("name", "Headset");
		 
		             context.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");
		            
		             mAudioManager.setSpeakerphoneOn(true);		 
		             mAudioManager.setRingerMode(mode);
		             
		         }catch (Exception e){
		 
		             e.printStackTrace();
		 
		         }
		 
		}


}
