package com.utracker.activity;

import com.utracker.LocationApplication;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class DebugActivity extends Activity{
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        /*  ListView lv = new ListView(this);
	        Cursor cursor = LocationApplication.getInstance().getDBLocations();
	         SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter   (this,android.R.layout.simple_expandable_list_item_2,cursor,new String[]{"_id", "time"},new int[]{android.R.id.text1, android.R.id.text2});
	         lv.setAdapter(simpleCursorAdapter);
	         this.setContentView(lv);
	        
	        
	        Cursor cursor = LocationApplication.getInstance().getDBLocations();
	        TextView tv = new TextView(this);
	        
	        cursor.moveToFirst();
			ArrayList<String> strList = Util.traceToStrlist(cursor);
			cursor.moveToFirst();
			String msg = Util.traceToString(cursor);
			
			ArrayList<String> strList2 = Util.stringToStrlist(msg);
			ArrayList<GeoPoint> geoList = Util.strlistToTrace(msg);
			
	        
	        int length = cursor.getCount();
	        tv.append("Cursor size:"+ cursor.getCount()+
	        		" \nStrListSize:"+ strList.size()+
	        		"\nGeoPoint size: "+ strList2.size()+"\n");
	        tv.append("MSG:"+ msg+"\n");
	        tv.append("_______\n");
	        cursor.moveToFirst();
			for(int i= 0 ;i<length;i++){
				if(i>strList2.size()) break;
				TracePoint tr = new TracePoint(cursor);				
				tv.append(tr.getLongitudeE6()+" "+tr.getLatitudeE6()+" \n");
				String s = strList.get(i);
				tv.append(s+"\n");
				tv.append(strList2.get(i*2)+" "+strList2.get(i*2+1)+"\n");
				GeoPoint g = geoList.get(i);
				tv.append(g.getLongitudeE6()+" "+g.getLatitudeE6()+" \n");
				
				tv.append("______\n");
				if(!cursor.moveToNext())break;
			}
			*/
	        
	        Cursor cursor = LocationApplication.getInstance().getDBTraces();
	        TextView tv = new TextView(this);
	        cursor.moveToFirst();
			do{
				tv.append("ID:"+ cursor.getInt(0)+"\n"+
							"Phone:"+ cursor.getString(1)+"\n"
							+"Time:"+ cursor.getString(2)+"\n"
							+"MSG:"+ cursor.getString(3)+"\n"
							+"_________\n");
							
			}while(cursor.moveToNext());
			ScrollView sv = new ScrollView(this);
			sv.addView(tv);
	        this.setContentView(sv);
	     
	    }
	 
}
