package com.tikalk.wifilistener;

import java.util.List;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.tikalk.tools.DBTool;
import com.tikalk.tools.Defined;
import com.tikalk.tools.PendingEvent;
import com.tikalk.tools.Shared;

public class LocationSingleUpdateBroadcastReceiver extends BroadcastReceiver {	
	/**
	 * CONSTANTS
	 */
	public static final String TAG = LocationSingleUpdateBroadcastReceiver.class.getName();
	//constant for singleupdate intent filter
	public static final String SINGLE_LOCATION_UPDATE_ACTION = "com.radioactiveyak.places.SINGLE_LOCATION_UPDATE_ACTION";
	//constant for max distance from origional location to perform login
	public static final float MAX_DIST = 500.0f;
	//constant for min distance for a logout if user is less than that distance away won't perform logout
	public static final float MIN_DIST = 30.0f;

	//KEYS FOR BUNDLE
	//value that lets the phonegap activity know that it is being called form a notification
	public static final String KEY_CALLER = "caller";


	/**
	 * MEMBERS
	 */
	private DBTool mDB;

	@Override
	public void onReceive(Context context, Intent intent) {
		Shared.mRequestingLocation = false;;
		Log.d("location test", "Location update fired");
		context.unregisterReceiver(this);
		//grab database
		mDB = new DBTool(context);

		String key = LocationManager.KEY_LOCATION_CHANGED;
		Location location = (Location)intent.getExtras().get(key);
		if(location != null){
			Log.d("location test", "location recieved at " + System.currentTimeMillis() + 
					" location is <" + printLocation(location) + ">");
			//check queue and dequeue all items and perform actions
			while(Shared.queueHasNext()){
				PendingEvent pending = Shared.queueGetNext();
				/*if(pending.getType() == PendingEvent.EVENT_ADD_POINT)
	    			addSpot(pending.getSSID(), location);
	    		else*/ if(pending.getType() == PendingEvent.EVENT_VERIFY_LOGIN_SPOT){
	    			verifyLogin(pending.getSSID(), location, context);
	    		}
	    		else if(pending.getType() == PendingEvent.EVENT_LOGOUT){
	    			verifyLogout(pending.getSSID(), location, context);
	    		}
			}
		}
		mDB.close();
	}
	//verify user is logging into correct location
	private void verifyLogin(String ssid, Location verifyLoc, Context context) {
		//verify that spot is within a certain distance if so then post notification
		Location origLoc = mDB.getLocation(ssid);
		float dist = origLoc.distanceTo(verifyLoc);
		if(dist < MAX_DIST){
			String projectName = mDB.getProjectName(ssid);

			String projectID = mDB.getID(projectName);
			//if project is set for auto-update then login/logout
			if(mDB.getAutoUpdate(projectID)){
				//if loggin in passes, then log timestamp
				if(mDB.setLoggedIn(true, projectID))
					mDB.setLoggedInTimestamp(true, projectID);
			}else{
				int id = mDB.getNotificationID(projectName);
				addNotification(true, projectName, id, context);
			}
		}
	}

	//verify user is far enough away(MIN_DIST) to logout
	private void verifyLogout(String ssid, Location verifyLoc, Context context) {
		//verify that spot is within a certain distance if so then post notification
		Location origLoc = mDB.getLocation(ssid);
		float dist = origLoc.distanceTo(verifyLoc);
		if(dist > MIN_DIST){
			String projectName = mDB.getProjectName(ssid);

			//if project is set for auto-update then login/logout
			String projectID = mDB.getID(projectName);
			if(mDB.getAutoUpdate(projectID)){
				//if loggin in passes, then log timestamp
				if(mDB.setLoggedIn(false, projectID))
					mDB.setLoggedInTimestamp(false, projectID);
			}else{
				int id = mDB.getNotificationID(projectName);
				addNotification(false, projectName, id, context);
			}

		}
	}
	//
	//	//add spot to database
	//	private void addSpot(String ssid, Location location) {
	//		mDB.addPoint(ssid, "", "" +  location.getLongitude(), "" + location.getLatitude());
	//	}


	//TODO used for testing
	private String printLocation(Location location) {
		String retVal = "";
		if(!(location == null)){
			retVal += "|long:" + location.getLongitude() + "|";
			retVal += "|lat:" + location.getLatitude() + "|";
			retVal += "|speed:" + location.getSpeed() + "|";
			retVal += "|accuracy:" + location.getAccuracy() + "|";
			retVal += "|alt:" + location.getAltitude() + "|";
			retVal += "|bearing:" + location.getBearing()+ "|";
			retVal += "|provider:" + location.getProvider()+ "|";
			retVal += "|time:" + location.getTime() + "|";
		}
		return retVal;
	}

	//user has encountered or left a tagged spot so pop notificaton
	private void addNotification(boolean loggingIn, String projectName,int notifyID,  Context context) {

		/*Location spotLoc = mDB.getLocation(ssid);
		Log.d("notify", "user is close to " + ssid + " at " + spotLoc.getLatitude() + "," + spotLoc.getLongitude());
		 *///init login/logout variables
		String loginString1 = "", loginString2 = "";
		String title = "";
		if(loggingIn){
			loginString1 = "You are near ";
			loginString2 = " click to login";
			title = "login";
		}
		else{
			loginString1 = "You have left ";
			loginString2 = " click to logout";
			title = "logout";
		}
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		int icon = R.drawable.btn_star;
		CharSequence tickerText = loginString1 + projectName + loginString2;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle = title;
		CharSequence contentText = loginString1 + projectName + loginString2;
		Intent notificationIntent = new Intent();
		notificationIntent.setClassName(Defined.INTENT_PACKAGE, Defined.INTENT_ACTIVITY);
		notificationIntent.putExtra(KEY_CALLER, "notification");
		notificationIntent.putExtra(Defined.KEY_PROJECT_NAME, projectName);
		notificationIntent.putExtra(Defined.KEY_LOGGING_IN, loggingIn);
		notificationIntent.putExtra(Defined.KEY_PROJECT_ID, mDB.getID(projectName));

		PendingIntent contentIntent = PendingIntent.getActivity(context,notifyID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		//add vibrate
		long[] vibrate = {0,100,200,300, 0, 0, 100, 100, 0, 0, 0, 100, 100, 0, 0, 0, 0, 0, 300, 300, 300};
		notification.vibrate = vibrate;

		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(notifyID, notification);
	}

	/**
	 * STATIC METHODS
	 */

	public static void startSingleUpdate(Context context){
		//if we are already requesting the location then don't re-request
		if(Shared.mRequestingLocation)
			return;

		Shared.mRequestingLocation = true;
		//set listener
		Log.d("location test", "starting single update at " + System.currentTimeMillis());
		IntentFilter accurateLocation = new IntentFilter(SINGLE_LOCATION_UPDATE_ACTION);
		context.registerReceiver(new LocationSingleUpdateBroadcastReceiver(), accurateLocation);
		//create pending intent to fire
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Intent updateIntent = new Intent(SINGLE_LOCATION_UPDATE_ACTION);  
		PendingIntent singleUpatePI = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Criteria criteria = new Criteria();
		String majorProvider = locationManager.getBestProvider(criteria, true);
		Log.d("location test", "major provider " + majorProvider);
		List<String> allProviders = locationManager.getAllProviders();
		for(int i=0; i < allProviders.size();i++){
			Log.d("location test", "provider " + i + ": " + allProviders.get(i));

		}
		Log.d("location test", "enabled...");
		allProviders = locationManager.getProviders(true);
		for(int i=0; i < allProviders.size();i++){
			Log.d("location test", "enabled provider " + i + ": " + allProviders.get(i));

		}

		//set listener
		locationManager.requestSingleUpdate("network", singleUpatePI);

	}

}
