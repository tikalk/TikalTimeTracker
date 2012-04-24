package com.tikalk.wifilistener;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.tikalk.tools.DBTool;
import com.tikalk.tools.PendingEvent;
import com.tikalk.tools.Shared;

public class WifiChangeBroadcastReciever extends BroadcastReceiver {
	/**
	 * CONSTANTS
	 */
	public static final String TAG = WifiChangeBroadcastReciever.class.getName();


	/**
	 * MEMBERS
	 */
	//local database tool
	DBTool mDB;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "wifi change");
		//if service is not running launch it
		if(!WifiListenerService.isServiceRunning(context)){
			Log.d(TAG, "service not running, launching from wifi change reciever");
			//insure that the service is running
			Intent startServiceIntent = new Intent(context, WifiListenerService.class);
			context.startService(startServiceIntent);
		}
		//grab all wifi objects
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> activeNetworkInfo = wifiManager.getScanResults();
		int size;
		if(activeNetworkInfo == null){
			size = 0;
		}
		else{
			size = activeNetworkInfo.size();
		}
		//grab database
		mDB = new DBTool(context);
		//check if currently logged into a location
		String loggedInProject = mDB.loggedInProject();
		boolean hasLoggedIn = !loggedInProject.matches("");
		/**
		 * NOTE: THAT THIS FUNCTION CHECKS ACCORDING TO 'OR' LOGIC IF ANY SSID IS PRESENT FOR A CURRENT
		 * PROJECT IT WILL ADD TO QUEUE TO NOTIFY ACCORDING TO GPS VERIFICATION
		 */
		//build list and set to static object for current wifi spots
		List<String> current = new ArrayList<String>();
		for(int i=0;i < size;i++){
			Log.d(TAG, activeNetworkInfo.get(i).SSID);
			String ssid = activeNetworkInfo.get(i).SSID;
			current.add(ssid);
			//if no current logged int
			if(!hasLoggedIn){
				//if in database and logged out then prompt user
				boolean exists = mDB.existsSSID(ssid);
				Log.d("notify", ssid + " exists " + exists);
				if(exists){
					boolean loggedin = mDB.isLoggedIn(ssid);
					Log.d("notify", ssid + " is logged in " + loggedin);
					//if project exists and not logged in the request to login
					String projectName = mDB.getProjectName(ssid);
					if(!mDB.isLoggedIn(projectName)){
						Shared.queueAddEvent(new PendingEvent(ssid, PendingEvent.EVENT_VERIFY_LOGIN_SPOT));	
						LocationSingleUpdateBroadcastReceiver.startSingleUpdate(context);
						
					}
				}
			}
		}
		Shared.setCurrentSpots(current);
		//check if we are logged into a project
		if(hasLoggedIn){
			//insure that at least one of the projects SSIDs is visible
			List<String> projectSSIDs = mDB.getAllSpots(loggedInProject);
			boolean containedCurrent = false;
			size = projectSSIDs.size();
			for(int i=0; i < size; i++){
				if(current.contains(projectSSIDs.get(i))){
					containedCurrent = true;
					break;
				}
			}
			if(!containedCurrent){
				//send arbitrary ssid
				Shared.queueAddEvent(new PendingEvent(projectSSIDs.get(0), PendingEvent.EVENT_LOGOUT));	
				LocationSingleUpdateBroadcastReceiver.startSingleUpdate(context);
			}
		}
		
		mDB.close();
		//if logged in an hotspot has disappeared

	}
	
	
	

}
