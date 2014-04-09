package com.utracker;

import android.database.Cursor;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class TracePoint extends GeoPoint{
	
	private String time;
	private String address;
	private double radius;
	private double speed;
	private int id;
	
	public TracePoint(Cursor c){
		super(0,0);
		this.id = c.getInt(0);
		this.setLongitudeE6((int)(c.getDouble(1)*1e6));
		this.setLatitudeE6((int)(c.getDouble(2)*1e6));
		this.time= c.getString(3);
		this.address= c.getString(4);
		this.radius= c.getDouble(5);
		this.speed= c.getDouble(6);
		
		
	}
	public String getInfo(){
		String str = "ID: " +id +"\n"+
				"Longitude: "+ this.getLongitudeE6()/1000000.0+"\n"+
				"Latiude: "+this.getLatitudeE6()/1000000.0+"\n"+
				"Time: "+ time +"\n" +
				"Address: " + address +"\n" +
						"Radius: "+ (int)radius +" m\n" +
								"Speed: "+(int)speed +"m/s \n";
		return str;
	}
}
