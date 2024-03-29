package com.utracker.activity;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import com.utracker.LocationApplication;
import com.utracker.MyOverlay;
import com.utracker.R;
import com.utracker.Setting;
import com.utracker.TracePoint;
import com.utracker.Util;


public class MapActivity extends Activity{
		private boolean animate = true;
		private final String TAG = "MapActivity";
		static MapView mMapView = null;
		/*
		public static int STATE = 0;
		public static final int LOCATION_MODE = 0;
		public static final int MY_TRACE_MODE = 1;
		public static final int TRACKEE_TRACE_MODE = 2;
		*/
		private boolean traceMode = false;
		
		public boolean settingCenter = false;
				
		
		@Override
        public void onCreate(Bundle savedInstanceState){
        	super.onCreate(savedInstanceState);       	
        	setContentView(R.layout.activity_map);
        	LocationApplication.getInstance().map = this;
        //	LocationApplication.getInstance().openMapManager();
        	
        	mMapView = (MapView) findViewById(R.id.bmapView);
        	//mMapView.setLongClickable(true);
        	//mMapView.setOnLongClickListener(null);        	
        	mMapView.setBuiltInZoomControls(true);
        	mMapView.setAnimationCacheEnabled(true);  
        	mMapView.getController().setCenter(new GeoPoint((int)(Setting.centerLatitude*1e6) ,(int) (Setting.centerLongitude*1e6)));
            mMapView.getController().setZoom(16);
            mMapView.getController().enableClick(true);
            
      //     graphicsOverlay = new GraphicsOverlay(mMapView);
      //     mMapView.getOverlays().add(graphicsOverlay);
            
        	this.traceMode = false;   
        	this.animate = true;
        	((LocationApplication)this.getApplication()).requestLocation();
        	Log.d(TAG, "Init map");        	
        	//mMapView.getController().setCenter(new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6)));
        	
        }
	
        public class MyLocationData extends LocationData{
        	public MyLocationData(){
        		super();
        	}
        	public MyLocationData(BDLocation loc){
        		super();
        		this.latitude = loc.getLatitude();
        		this.longitude = loc.getLongitude();
        		this.direction = loc.getDerect();
        		this.accuracy = loc.getRadius();
        	}
        	
        	public void setLocationData(BDLocation loc){
        		this.latitude = loc.getLatitude();
        		this.longitude = loc.getLongitude();
        		this.direction = loc.getDerect();
        		this.accuracy = loc.getRadius();
        	}
        	
        	public GeoPoint getGeoPoint(){
        			return new GeoPoint((int)(this.latitude*1e6),
        					(int)(this.longitude*1e6));
           	}
        }
        /*
        @Override
        public boolean onTouchEvent(MotionEvent event){
        	//mMapView.getProjection().fromPixels(event.getX(), event.getY())
			Toast.makeText(this, "Touched",Toast.LENGTH_SHORT)
			.show();
			return super.onTouchEvent(event);
        	        	
        }*/
      
        
        @Override
        protected void onDestroy(){
                mMapView.destroy();
                LocationApplication.getInstance().map = null;
                super.onDestroy();
        }
        @Override
        protected void onPause(){
                mMapView.onPause();
                super.onPause();
        }
        @Override
        protected void onResume(){
                mMapView.onResume();
                LocationApplication.getInstance().map = this;
                this.animate = true;
                super.onResume();
        }
        
       
        
        
        
        @Override
    	public boolean onCreateOptionsMenu(Menu menu) {
    		// Inflate the menu; this adds items to the action bar if it is present.
    		getMenuInflater().inflate(R.menu.map, menu);
    		return true;
    	}
    	
    	public boolean onOptionsItemSelected(MenuItem item){
    		switch(item.getItemId()){
    		case R.id.map_menu_refresh: 
    			this.traceMode= false;
    			//mMapView.getOverlays().clear();
    			//this.addBorderOverlay();
    			//this.testLineClick();
    			//getBorderOverlay();
    			//mMapView.refresh();   			
    			((LocationApplication)this.getApplication()).requestLocation();
    			    
    		break;
    		
    		case R.id.map_menu_setScreenCenter:
    			//GeoPoint center = mMapView.getMapCenter();
    			//setTrackCenter((double)center.getLatitudeE6()/1000000.0, 
    			//		(double)center.getLongitudeE6()/1000000.0);
    			this.settingCenter = true;
    			Toast.makeText(this,"Tap the screen to set center",Toast.LENGTH_SHORT)
    			.show();
    			break;
    			
    		case R.id.map_menu_viewMyTrace:
    			this.traceMode = true;
    			viewMyTrace();
    			//this.testViewmytrace();
    			break;
    			
    		case R.id.map_menu_viewTrackeeTrace:
    			this.traceMode = true;
    			viewTrackeeTrace();
    			break;
    		}
    		return super.onOptionsItemSelected(item);
    	}
    	
    	
    	 public void OnGetLocation(){
    		if(this.traceMode) return;
    		    		
         	BDLocation location = LocationApplication.getInstance().getLastLocation();
         	if(location ==null) return;
         	mMapView.getOverlays().clear();
         	
         	MyLocationData locData = new MyLocationData(location);
          //     Log.d("loctest",String.format("before: lat: %f lon: %f", location.getLatitude(),location.getLongitude()));
         	
         	
    		MyLocationOverlay myLocationOverlay = null;
         	myLocationOverlay = new MyLocationOverlay(mMapView);
        	myLocationOverlay.enableCompass(); 
         	myLocationOverlay.setData(locData);
         	MapController mMapController=mMapView.getController();
         	
         	
         	Drawable marker = getResources().getDrawable(R.drawable.main_map_icon_streetscape_selected);
     	    MyOverlay centerOverlay = new MyOverlay(marker, this);
     	    centerOverlay.addItem(getCenterItem());
     	    /*
     	    int lat = (int) (Setting.centerLatitude*1e6);
		   	int lon = (int) (Setting.centerLongitude*1e6);   	
		   	GeoPoint pt1 = new GeoPoint(lat, lon);	   	
		    
		 	Geometry circleGeometry = new Geometry();
	  	
	  		circleGeometry.setCircle(pt1, Setting.distanceLimit);
	  		
	  		Symbol circleSymbol = new Symbol();
	 		Symbol.Color circleColor = circleSymbol.new Color();
	 		circleColor.red = 0;
	 		circleColor.green = 255;
	 		circleColor.blue = 0;
	 		circleColor.alpha = 126;
	  		circleSymbol.setSurface(circleColor,1,3);
	  		
	  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
	  		
	  		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
	  		graphicsOverlay.setData(circleGraphic);
     	    
     	    
     	    
     	    
     	    
     	    
     	    
     	    
     	    */
     	    
     	    
	  	//	mMapView.getOverlays().add(graphicsOverlay);
            this.addBorderOverlay();
            mMapView.getOverlays().add(myLocationOverlay);
            mMapView.getOverlays().add(centerOverlay);
         	mMapView.refresh();
         	
         	if(this.animate){
     			mMapController.animateTo(locData.getGeoPoint());
     			animate = false;
     	}
         	 
         }
    	private void viewMyTrace(){
    		Log.d(TAG,"View Trace");
    		mMapView.getOverlays().clear();
    		//TraceOverlay routeOverlay = new TraceOverlay(this, mMapView);
    		
    		//OverlayItem item= new OverlayItem(new GeoPoint(lat,lon),"item"+i,"item"+i);
    		//getResources().getDrawable(R.drawable.icon_markc);
    	    
    	    Drawable marker = getResources().getDrawable(R.drawable.user_center_address_icon);
    	    MyOverlay pointOverlay = new MyOverlay(marker, this);
    	    
    	    Cursor cursor = LocationApplication.getInstance().getDBLocations(Setting.traceLength);
    	    cursor.moveToFirst();
    		int length = cursor.getCount();
    		
    		if(length<1){
    			Toast.makeText(this, "No trace recorded",Toast.LENGTH_SHORT)
    			.show();
    			return;
    		}
    		
    		TracePoint[] route = new TracePoint[length];
    		for(int i = length-1 ;i>=0 ;i--){
    			route[i]= new TracePoint(cursor);
    			OverlayItem item= new OverlayItem(route[i],route[i].getInfo(),"item"+i);
    			pointOverlay.addItem(item);
    			if(!cursor.moveToNext())break;
    			}
    		cursor.close();
    		pointOverlay.addItem(getCenterItem());
    		
    		MKRoute mr = new MKRoute();
    		mr.customizeRoute(route[0],route[length-1], route);
    		
    		RouteOverlay routeOverlay = new RouteOverlay(this ,mMapView);
		    routeOverlay.setData(mr);
    		
    		//TraceRoute tr = new TraceRoute(LocationApplication.getInstance().getDBLocations());
		    this.addBorderOverlay();
		    mMapView.getOverlays().add(routeOverlay);
		    mMapView.getOverlays().add(pointOverlay);
		    	  		
		 //   mMapView.getOverlays().add(getBorderOverlay());
		    mMapView.refresh();
		    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		    mMapView.getController().animateTo(mr.getStart());
    	}
    	
    	
    	
    	private void showTrackeeTrace(Cursor cursor , int position){
    		
    		if(cursor ==null){
    			Toast.makeText(this, "No trace recorded",Toast.LENGTH_SHORT)
    			.show();
    			return;
    		}

		    mMapView.getOverlays().clear();
    		 cursor.moveToPosition(position);
    		 
    		 String msg = cursor.getString(3);
    		 
    		ArrayList<GeoPoint> geoList = Util.strlistToTrace(msg);
    		int len = geoList.size();
    		Drawable marker = getResources().getDrawable(R.drawable.user_center_address_icon);
    	    MyOverlay pointOverlay = new MyOverlay(marker, this);
    	        	    
    		GeoPoint[] geoPoint = new GeoPoint[len];
    		for(int i = len-1;i>=0; i--){
    			geoPoint[i]= new GeoPoint(geoList.get(len-1-i).getLatitudeE6(),geoList.get(len-1-i).getLongitudeE6());
    			OverlayItem item= new OverlayItem(geoPoint[i],"Longitude: "+geoPoint[i].getLongitudeE6()/1000000.0+"\nLatitude: "+geoPoint[i].getLatitudeE6()/1000000.0,"item"+i);
    			pointOverlay.addItem(item);	
    			Log.d(TAG, geoPoint[i].getLatitudeE6()+" "+geoPoint[i].getLongitudeE6()+"");
    		}
    		
    		//pointOverlay.addItem(getCenterItem());
    		
    		MKRoute mr = new MKRoute();
    		mr.customizeRoute(geoPoint[0],geoPoint[len-1], geoPoint);
    		
    		RouteOverlay routeOverlay = new RouteOverlay(this ,mMapView);
		    routeOverlay.setData(mr);
    
    		//TraceRoute tr = new TraceRoute(LocationApplication.getInstance().getDBLocations());
		    
		    
		    Log.d(TAG, "ClearOverlay");
		    mMapView.getOverlays().add(routeOverlay);
		    mMapView.getOverlays().add(pointOverlay);
		    mMapView.refresh();
		    mMapView.getController().zoomToSpan(routeOverlay.getLatSpanE6(), routeOverlay.getLonSpanE6());
		    mMapView.getController().animateTo(mr.getStart());
    	}
    	private void viewTrackeeTrace(){
    		/*
    		AlertDialog.Builder bld = new AlertDialog.Builder(this);
    		
    		final EditText phoneNumber = new EditText(this);
    		phoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
    		
    		bld.setTitle("Enter trackee phone number")
    		.setView(phoneNumber)
    		.setPositiveButton("OK", new DialogInterface.OnClickListener(){			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				
    					Toast.makeText(MapActivity.this, "Location scan span is set "+Setting.scanSpanSec+" second", Toast.LENGTH_SHORT)
    					.show();
    							
    			}
    			
    		})
    		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				dialog.cancel();
    			}
    		})
    		.show();
    		*/
    		//Intent i = new Intent(this, TraceActivity.class);
    		//startActivity(i);
    		final Cursor cursor = LocationApplication.getInstance().getDBTraces();
    		startManagingCursor(cursor);
    	    cursor.moveToFirst();
    	       	    
    	    int length = cursor.getCount();
    	    String[] itemList = new String[length];    	    
    	    
    	    for(int i =0 ;i<length;i++){
    	    	itemList[i] = cursor.getString(2)+" "+ cursor.getString(1);   			
    	    	if(!cursor.moveToNext())break;
   			}
    	    
    	    
    	    new AlertDialog.Builder(this).setTitle("Choose a trace")
    	    .setIcon(
    	    	     android.R.drawable.ic_dialog_info)
    	    .setSingleChoiceItems(
    	    	     itemList, 0,
    	    	     new DialogInterface.OnClickListener() {
    	    	      public void onClick(DialogInterface dialog, int which) {
    	    	       //dialog.dismiss();
    	    	    	  showTrackeeTrace(cursor, which);    	    	    	  
    	    	      }
    	    	     })
    	    .setNegativeButton("OK",new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
    	    	
    	    })
    	   .show();
    	    
    	    
    	   // this.showTrackeeTrace(msg);
    	}
    	
    	
    	public void setTrackCenter(double lat, double lon){
    		if(!this.settingCenter) return;
    		boolean succ = true;
    		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LocationApplication.getInstance());
    		pref.edit()
    		.putString("centerLongitude", lon+"")
    		.putString("centerLatitude", lat+"")
    		.commit();
    		try {
				Setting.initLocationSettings(getApplication());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				succ = false;
				e.printStackTrace();
			}
    		if(succ){
    			Toast.makeText(this,"Track center is set ("+lon+","+lat+") successfully.",Toast.LENGTH_SHORT)
    			.show();
    			this.settingCenter= false;
    		}
		
    	}
    	
    	private OverlayItem getCenterItem(){
    		Drawable marker = getResources().getDrawable(R.drawable.nav_turn_via_1);
    	    OverlayItem item= new OverlayItem(new GeoPoint((int)(Setting.centerLatitude*1e6) ,(int) (Setting.centerLongitude*1e6)),
    	    						"Center: ("+ Setting.centerLongitude+" , "+Setting.centerLatitude+")"
    	    						+"\n"+ "Distance from your position: " + LocationApplication.getInstance().getCenterDistance()+"m",
    	    						"centeritem");
			item.setMarker(marker);
			return item;
    	}
    	
    	 private void addBorderOverlay() {
    	    	int lat = (int) (Setting.centerLatitude*1e6);
    		   	int lon = (int) (Setting.centerLongitude*1e6);   	
    		   	GeoPoint pt1 = new GeoPoint(lat, lon);
    		   	
    		    
    		  //构建点并显示
    	  		Geometry circleGeometry = new Geometry();
    	  	
    	  		circleGeometry.setCircle(pt1, (int)(Setting.distanceLimit*1.35));
    	  		
    	  		Symbol circleSymbol = new Symbol();
    	 		Symbol.Color circleColor = circleSymbol.new Color();
    	 		circleColor.red = 0;
    	 		circleColor.green = 255;
    	 		circleColor.blue = 0;
    	 		circleColor.alpha = 70;
    	  		circleSymbol.setSurface(circleColor,1,3);
    	  		
    	  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
    	  		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
    	  		graphicsOverlay.removeAll();
    	  		graphicsOverlay.setData(circleGraphic);
    	  		mMapView.getOverlays().add(graphicsOverlay);
    	  		

    	   }    	 
    
}
