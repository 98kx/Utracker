package com.utracker.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.utracker.LocationApplication;
import com.utracker.R;
import com.utracker.Setting;
import com.utracker.R.id;
import com.utracker.R.layout;
import com.utracker.R.menu;
import com.utracker.receiver.BootReceiver;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	//LocationApplication application = null;
	private final String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate");
		super.onCreate(savedInstanceState);
		if(Setting.requirePassword)showPasswordDialog();
		setContentView(R.layout.activity_main);	
		LocationApplication.getInstance().main = this;
		

		if(!LocationApplication.showNotification){
			LocationApplication.getInstance().showNotification();
		}
		
		if(!LocationApplication.getInstance().isStarted())
			LocationApplication.getInstance().startApp();
		onGetLocation();
		
		//if(this.getIntent()!=null&&this.getIntent().getAction()!=null&&
		//		this.getIntent().getAction().equals(BootReceiver.action_first_start))
		//	this.finish();
			
			
	}
	
	
	
	private void showPasswordDialog(){
		final EditText password = new EditText(this);
		new AlertDialog.Builder(this).setTitle("Password").setCancelable(false)
		.setView(password)
		.setPositiveButton("OK", new OnClickListener(){			
			public void onClick(DialogInterface dialog, int which) {
				if(password.getText().toString().equals(Setting.password)){
					dialog.dismiss();
				}
				else{
					Toast.makeText(LocationApplication.getInstance(), "Wrong password", Toast.LENGTH_SHORT)
					.show();
					showPasswordDialog();
				}
			}			
		})
		.setNegativeButton("Exit", new OnClickListener(){			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();			
				((LocationApplication)getApplication()).exit();
			}			
		})
		.show();
	}
	/**
	 * Display loading message before location information arrives.
	 */
	private void showLoadingText(){
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		
		item.put("title", "Loading...."); 
		item.put("body", "Make sure network or GPS is available. If no responding for long, try clicking \"Refresh\" Button or restart application."); 
		data.add(item); 
		
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
				new String[] { "title" ,"body"}, new int[] { android.R.id.text1 , android.R.id.text2 });
		this.setListAdapter(adapter); 
	}
	
		
	/**
	 * Call back when gaining location info.
	 * @param application
	 */
	public void onGetLocation(){				
		LocationApplication application = LocationApplication.getInstance();
		BDLocation loc= application.getLastLocation();
		if (loc==null){
			showLoadingText();
			return;
		}
		
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<String, Object>();
		
		item.put("title", "Time"); 
		item.put("body", loc.getTime()+""); 
		data.add(item); 
		
		item = new HashMap<String, Object>(); 
		item.put("title", "Track Enabled"); 
		item.put("body", Setting.trackedEnable?"Yes":"No"); 
		data.add(item); 
		
		item = new HashMap<String, Object>(); 
		item.put("title", "Running Mode");
		item.put("body",(application.runMode==LocationApplication.MONITOR_MODE)?"Monitor mode":
			(application.runMode==LocationApplication.TRACK_MODE)?"Track mode":
				""
			);
		data.add(item);
		
		item = new HashMap<String, Object>(); 
		item.put("title", "Location frequency & Accuracy"); 
		item.put("body", Setting.scanSpanSec+"sec/once & "+ (loc.hasRadius()?((int)loc.getRadius()+" m"):null)+"" ); 
		data.add(item);
		
		
		item = new HashMap<String, Object>(); 
		item.put("title", "Address"); 
		item.put("body",application.getLastAddress()+"");
		data.add(item); 
		
		
	//	if(loc.hasAltitude()){
		//	item = new HashMap<String, Object>();
		//	item.put("title", "Altitude");
		//	item.put("body",loc.hasAltitude()?loc.getAltitude():null+"");
		//	data.add(item);
		//}
		
		item = new HashMap<String, Object>();
		item.put("title", "Longitude & Latitude");
		item.put("body","("+loc.getLongitude()+" , "+loc.getLatitude()+")");
		data.add(item);	
		
		item = new HashMap<String, Object>(); 
		item.put("title", "Speed");
		item.put("body",loc.hasSpeed()?(int)loc.getSpeed():null+" m/s");
		data.add(item);	
		
		
		
		if(application.runMode==LocationApplication.MONITOR_MODE){
			item = new HashMap<String, Object>(); 
			item.put("title", "Distance from center");
			item.put("body",(int)application.getCenterDistance()+" m");
			data.add(item);
		}else if(application.runMode==LocationApplication.TRACK_MODE){
			item = new HashMap<String, Object>(); 
			item.put("title", "Distance from center & last point");
			item.put("body",(int)application.getCenterDistance()+" m & "+ (int)application.getIntervalDistance()+"m");
			data.add(item);
		}
	
	
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,
				new String[] { "title" ,"body"}, new int[] { android.R.id.text1 , android.R.id.text2 });
		this.setListAdapter(adapter); 
		
	}
	
	
	
	 @Override
	    protected void onPause() {
	      	//Log.d(TAG,"Pause");
	        super.onPause();
	    }
	    
	    @Override
	    protected void onResume() {
	    	//Log.d(TAG,"Resume");
	    	((LocationApplication)getApplication()).main = this;
	        super.onResume();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	//Log.d(TAG,"Destroy");
	    	((LocationApplication)getApplication()).main = null;
	        super.onDestroy();
	    }
	    
	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	    	//Log.d(TAG,"Saveinstance");
	    	super.onSaveInstanceState(outState);
	    	
	    }
	    
	    @Override
	    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    	//Log.d(TAG,"RestoreInstance");
	    	super.onRestoreInstanceState(savedInstanceState);
	    }
	    
	   
	
	
	
	
	
	/**
	 * Menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_settings:
			Intent intentSetting = new Intent(MainActivity.this,SettingActivity.class);
			startActivity(intentSetting);
			break;
			
		case R.id.menu_refresh:
			((LocationApplication)this.getApplication()).requestLocation();
			break;
			
	//	case R.id.menu_start:
			
	//		break;
	//	case R.id.menu_stop:
			
	//		break;
		case R.id.menu_exit:
			//((LocationApplication)getApplication()
			
			this.finish();			
			((LocationApplication)getApplication()).exit();
			//((LocationApplication)getApplication()).exit();
			//System.exit(0);
			break;
		case R.id.menu_viewmap:	
			//String s=UserSettingActivity.pref.getString("scanSpanSec", "Not found");	
			//PreferenceManager.getDefaultSharedPreferences(this).getString("scanSpanSec", "Not found");	
	    	//sLocationApplication.getInstance().getLocationSender().sendSMS(null,null);
			Intent intentMap= new Intent(MainActivity.this,MapActivity.class);
			startActivity(intentMap);
			break;
		/*case R.id.menu_debug:
			Intent intentDebug= new Intent(MainActivity.this,DebugActivity.class);
			startActivity(intentDebug);
			break;
		*/
		}
		return super.onOptionsItemSelected(item);
	}

}
