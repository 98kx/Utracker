package com.utracker;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.utracker.activity.MapActivity;


public class MyOverlay extends ItemizedOverlay<OverlayItem> {
    public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	protected Context mContext = null;
    Toast mToast = null;
    
	public MyOverlay(Drawable marker,Context context){
		super(marker);
		this.mContext = context;
	    populate();
	    
		
	}
	protected boolean onTap(int index) {
       
		if (null == mToast)
            mToast = Toast.makeText(mContext, mGeoList.get(index).getTitle(), Toast.LENGTH_LONG);
        else mToast.setText(mGeoList.get(index).getTitle());
		mToast.show();
		
		return true;
	}
	public boolean onTap(GeoPoint pt, MapView mapView){
		super.onTap(pt,mapView);
		if(!((MapActivity)mContext).settingCenter) return false;
		final GeoPoint p = pt;
		new AlertDialog.Builder(mContext).setTitle("Set this point as track center ?").setCancelable(false)
		.setPositiveButton("Yes", new OnClickListener(){			
			public void onClick(DialogInterface dialog, int which) {
				((MapActivity)mContext).setTrackCenter(p.getLatitudeE6()/1000000.0, p.getLongitudeE6()/1000000.0);
			}			
		})
		.setNegativeButton("No", new OnClickListener(){			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}			
		})
		.show();
		return true;
	}
	
	
	@Override
	protected OverlayItem createItem(int i) {
		return mGeoList.get(i);
	}
	
	@Override
	public int size() {
		return mGeoList.size();
	}
	public void addItem(OverlayItem item){
		mGeoList.add(item);
		populate();
	}
	public void removeItem(int index){
		mGeoList.remove(index);
		populate();
	}

	
}
