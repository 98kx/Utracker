package com.utracker.activity;


import com.utracker.LocationApplication;
import com.utracker.R;
import com.utracker.Setting;
import com.utracker.Util;
import com.utracker.R.id;
import com.utracker.R.layout;
import com.utracker.R.xml;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
public class SettingActivity extends PreferenceActivity {

	Dialog changeKeyDlg = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        addPreferencesFromResource(R.xml.preferences);
       
    }
    
   
   /* public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
    		Preference preference) {
    	//if(preference.getKey().equals("scanSpan"))
    	//	Setting.isScanSpanChanged= true;
    	if(preference.getTitle().equals("Password setting"))
    		changeKey();
    //	String str=PreferenceManager.getDefaultSharedPreferences(this).getString("scanSpanSec", "Not found");	
    //	Log.d("UserSetting",str);
    	return false;
    }
    private void changeKey(){
		changeKeyDlg = new Dialog(this);
		changeKeyDlg.setTitle("Change password:");
		changeKeyDlg.setContentView(R.layout.dialog_changekey);
		Button ok = null;
		Button cancel = null;
		
		ok = (Button) changeKeyDlg.findViewById(R.id.dialog_button_ok);
		cancel= (Button) changeKeyDlg.findViewById(R.id.dialog_button_cancel);
		//Log.d("NULL", (ok==null)+"");
		ok.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText oldKey = (EditText) changeKeyDlg.findViewById(R.id.oldKey);
				if(oldKey.getText().toString().equals(Setting.password)){
					EditText newKey = (EditText) changeKeyDlg.findViewById(R.id.newKey);
					String newPassword = newKey.getText().toString();
					if(newPassword==null||newPassword.equals("")){
						Toast.makeText(SettingActivity.this, "Can't be blank.", Toast.LENGTH_SHORT)
						.show();
					}
					else{
						Setting.password= newPassword;
						PreferenceManager.getDefaultSharedPreferences(SettingActivity.this).edit()
						.putString("password",Util.encrypt(newPassword))
						.commit();
						Toast.makeText(SettingActivity.this, "Password changed successfully. ", Toast.LENGTH_SHORT)
						.show();
						changeKeyDlg.dismiss();
					}
				}
				else{
					Toast.makeText(SettingActivity.this, "Wrong old Password.", Toast.LENGTH_SHORT)
					.show();
				}
			}
			
		});
		cancel.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				changeKeyDlg.dismiss();
			}
			
		});
		
		changeKeyDlg.show();
	}
    @Override
    protected void onDestroy() {
        boolean succ = true;
    	try {
			Setting.refreshSettingsFromPref(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			Intent i = new Intent(SettingActivity.this,SettingActivity.class);
			startActivity(i);
			Toast.makeText(LocationApplication.getInstance(), "Invalid change. Try again.",Toast.LENGTH_SHORT)
			.show();
			succ = false;
		}
    	super.onDestroy();
    	if(succ){
    		((LocationApplication)getApplication()).setScanSpan(Setting.scanSpanSec);
    		((LocationApplication)getApplication()).requestLocation();
    		Toast.makeText(LocationApplication.getInstance(), "Setting applied.",Toast.LENGTH_SHORT)
			.show();
    	}
    }
    
}
