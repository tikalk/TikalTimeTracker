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
		String loggedInProjectID = mDB.loggedInProject();
		boolean hasLoggedIn = !loggedInProjectID.matches("");
		/**
		 * NOTE: THAT THIS FUNCTION CHECKS ACCORDING TO 'OR' LOGIC IF ANY BSSID IS PRESENT FOR A CURRENT
		 * PROJECT IT WILL ADD TO QUEUE TO NOTIFY ACCORDING TO GPS VERIFICATION (IF NO
		 * OTHER PROJECT IS CHECKED IN)
		 */
		//build list and set to static object for current wifi spots
		List<String> current = new ArrayList<String>();
		for(int i=0;i < size;i++){
			Log.d(TAG, activeNetworkInfo.get(i).BSSID);
			String bssid = activeNetworkInfo.get(i).BSSID;
			current.add(bssid);
			//if no current logged int
			if(!hasLoggedIn){
				//if in database and logged out then prompt user
				boolean exists = mDB.existsBSSID(bssid);
				Log.d("notify", bssid + " exists " + exists);
				if(exists){
					boolean loggedin = mDB.isLoggedIn(bssid);
					Log.d("notify", bssid + " is logged in " + loggedin);
					//grab array of all projects with associated bssid and if not logged in then poll
					List<String> projectIDs= mDB.getProjectIDsForBSSID(bssid);
					int sizeIDs = projectIDs.size();
					//boolean that says if we added to queue so we know to call single update
					boolean addedToQueue = false;
					//for each project if it isn't logged in then send queue request
					for(int item = 0 ; item < sizeIDs; item++){
						String projectID = projectIDs.get(item);
						if(!mDB.isLoggedIn(projectID)){
							addedToQueue = true;
							Shared.queueAddEvent(new PendingEvent(bssid,projectID, PendingEvent.EVENT_VERIFY_LOGIN));	
						}
					}
					//if we added to the queue then request a single update
					if(addedToQueue){	
						LocationSingleUpdateBroadcastReceiver.startSingleUpdate(context);
					}
				}
			}
		}
		Shared.setCurrentSpots(current);
		//check if we are logged into a project
		if(hasLoggedIn){
			//insure that at least one of the projects BSSIDs is visible
			List<String> projectBSSIDs = mDB.getAllSpots(loggedInProjectID);
			boolean containedCurrent = false;
			size = projectBSSIDs.size();
			for(int i=0; i < size; i++){
				if(current.contains(projectBSSIDs.get(i))){
					containedCurrent = true;
					break;
				}
			}
			if(!containedCurrent){
				//send arbitrary ssid
				Shared.queueAddEvent(new PendingEvent(projectBSSIDs.get(0),loggedInProjectID, PendingEvent.EVENT_VERIFY_LOGOUT));	
				LocationSingleUpdateBroadcastReceiver.startSingleUpdate(context);
			}
		}
		
		mDB.close();

	}
	
	
	

}
