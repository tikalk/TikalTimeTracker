package com.tikalk.wifinotify;

import org.apache.cordova.DroidGap;

import com.tikalk.tools.DBTool;
import com.tikalk.tools.Defined;
import com.tikalk.tools.Shared;
import com.tikalk.wifilistener.LocationSingleUpdateBroadcastReceiver;

import android.content.Intent;
import android.os.Bundle;

public class WifiNotifyPhoneGapActivity extends DroidGap {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		//check if this activity was called from notification
        Bundle extras = getIntent().getExtras();
        //grab database
        DBTool db = new DBTool(this);
        //make sure that fresh extras and not old extras and contains all needed keys
        
        if(extras != null && extras.containsKey(Defined.KEY_PROJECT_NAME) && 
        		extras.containsKey(Defined.KEY_PROJECT_ID) &&
        		extras.containsKey(Defined.KEY_LOGGING_IN) && 
        		extras.getBoolean(Defined.KEY_LOGGING_IN) != db.isLoggedIn(extras.getString(Defined.KEY_PROJECT_ID)) &&
        		//to fix double login issues we check if logging in and another user has already logged in
        		(db.loggedInProject().matches("") || !extras.getBoolean(Defined.KEY_LOGGING_IN))
        		){
        	super.loadUrl("file:///android_asset/www/login.html");
        	//set the ssid and boolean variables to shared resource
        	String projectName = extras.getString(Defined.KEY_PROJECT_NAME);
        	boolean loggingIn = extras.getBoolean(Defined.KEY_LOGGING_IN);
        	String projectID = extras.getString(Defined.KEY_PROJECT_ID);
        	Shared.setNotifyClick(projectName, loggingIn, projectID);
        }
        //if not called from a notification then call the regular montor screen
        else
        	super.loadUrl("file:///android_asset/www/index.html");
        
        db.close();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent == null)
			setIntent(new Intent());
		else
			setIntent(intent);
	}
}