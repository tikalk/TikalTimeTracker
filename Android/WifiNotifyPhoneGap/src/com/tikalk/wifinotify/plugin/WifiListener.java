package com.tikalk.wifinotify.plugin;

import java.util.List;

import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.webkit.WebSettings.PluginState;

import com.phonegap.api.Plugin;
import com.tikalk.tools.DBTool;
import com.tikalk.tools.Defined;
import com.tikalk.tools.PendingEvent;
import com.tikalk.tools.Shared;
import com.tikalk.wifilistener.LocationSingleUpdateBroadcastReceiver;
import com.tikalk.wifilistener.WifiListenerService;
import com.tikalk.wifilistener.WifiListenerService.LocalBinder;

public class WifiListener extends Plugin {
	/**
	 * CONSTANTS
	 */
	public static final String TAG = WifiListener.class.getName();

	//ACTIONS
	//start up the monitoring service
	public static final String ACTION_START = "start";
	//stop the monitoring service
	public static final String ACTION_STOP = "stop";
	//returns if service is running or not
	public static final String ACTION_SERVICE_RUNNING = "service_running";
	//get all active wifi spots
	public static final String ACTION_GET_ACTIVE = "get_active";
	//set point for tracking
	public static final String ACTION_SET_POINT = "set_point";
	//confirm to login to a place
	public static final String ACTION_LOGIN = "login";
	//confirm to logout of a place
	public static final String ACTION_LOGOUT = "logout";
	//requests the details for the notificaiton click
	public static final String ACTION_GET_NOTIFY_DETAILS = "get_notify_details";
	//requests the formatted log for logins and logouts
	public static final String ACTION_GET_LOG = "get_log";
	//request for current wifi state
	public static final String ACTION_GET_WIFI_STATE = "get_wifi_state";
	//request to change wifi state
	public static final String ACTION_SET_WIFI_STATE = "set_wifi_state";
	//request project notification state
	public static final String ACTION_GET_NOTIFICATION_STATE = "get_project_notify_state";
	//set project notification state
	public static final String ACTION_SET_NOTIFICATION_STATE = "SET_project_notify_state";



	@Override
	public PluginResult execute(String action, JSONArray data, String callBackID) {
		PluginResult result = null;
		Context context = ctx.getApplicationContext();
		//create dbtool incase of need
		DBTool db = new DBTool(context);
		if(action.matches(ACTION_START)){
			//check if service is started
			if(!WifiListenerService.isServiceRunning(context)){
				//start service
				Intent startServiceIntent = new Intent(context, WifiListenerService.class);
				context.startService(startServiceIntent);
				//returns true since starting service
				return (new PluginResult(Status.OK, true));
			}
			//returns false because service already started
			return (new PluginResult(Status.OK, false));



		}
		else if(action.matches(ACTION_STOP)){
			if(WifiListenerService.isServiceRunning(ctx.getApplicationContext())){

				//stop service
				Intent stopServiceIntent = new Intent(context, WifiListenerService.class);
				context.bindService(stopServiceIntent, new ServiceConnection() {

					public void onServiceDisconnected(ComponentName name) {
						// TODO Auto-generated method stub

					}

					public void onServiceConnected(ComponentName name, IBinder service) {
						WifiListenerService wifiService = ((WifiListenerService.LocalBinder)service).getService();
						wifiService.unregisterRecievers();
						wifiService.stopSelf();

					}
				}, Context.BIND_NOT_FOREGROUND);
				//boolean serviceStoppedBool = context.stopService(stopServiceIntent);
				//returns true since starting service
				return (new PluginResult(Status.OK, true));
			}
			//returns false because service already stopped
			return (new PluginResult(Status.OK, false));

		}
		else if(action.matches(ACTION_SERVICE_RUNNING)){
			boolean running = WifiListenerService.isServiceRunning(ctx.getApplicationContext());
			return (new PluginResult(Status.OK, running));
		}
		else if(action.matches(ACTION_GET_ACTIVE)){
			//build list
			List<String> list = Shared.getCurrentSpots();
			JSONArray jsonListArray = new JSONArray();
			//build json objects
			int size = list.size();
			for(int i=0; i< size; i++){
				JSONObject newPoint = new JSONObject();
				String ssid = list.get(i);
				try {
					newPoint.put(Defined.KEY_PROJECT_NAME, ssid);
					newPoint.put(Defined.KEY_EXISTS, db.existsSSID(ssid));
					newPoint.put(Defined.KEY_LOGGED_IN, db.isLoggedIn(ssid));
				} catch (JSONException e) {
					db.close();
					return new PluginResult(Status.ERROR, getJsonObjectError(e));
				}
				jsonListArray.put(newPoint);

			}
			db.close();
			return(new PluginResult(Status.OK,jsonListArray));

		}
		else if(action.matches(ACTION_LOGIN)){
			db.close();
			return log(true, data, context);
		}
		else if(action.matches(ACTION_LOGOUT)){
			db.close();
			return log(false, data, context);
		}
		else if(action.matches(ACTION_SET_POINT)){
			//grab all the local ssids and add to database with given info
			List<String> current = Shared.getCurrentSpots();
			int size = current.size();
			//init json data variables
			String name = "";
			String id = "";
			String latitude = "";
			String longitude = "";
			//grab hotspot info from json
			try {
				name = data.getJSONObject(0).getString("projectname");
				id = data.getJSONObject(0).getString("projectid");
				latitude =  data.getJSONObject(0).getString("latitude");
				longitude =  data.getJSONObject(0).getString("longitude");

			}catch (JSONException e) {
				Log.d(TAG, "error setting new point:" + e.getMessage());
				return new PluginResult(Status.ERROR, getJsonObjectError(e));
			}
			//boolean for if add passes or fails (if not spots then false)
			boolean addPassed = size > 0;
			for(int i=0; i < size; i++){
				String ssid = current.get(i);

				//String ssid = data.getJSONObject(0).getString("ssid");


				if(ssid.matches(""))
					return new PluginResult(Status.ERROR, getJsonObjectError(new JSONException("no ssid")));
				/*//add hotspot to queue
				Shared.queueAddEvent(new PendingEvent(ssid, PendingEvent.EVENT_ADD_POINT));	
				LocationSingleUpdateBroadcastReceiver.startSingleUpdate(context);*/
				addPassed &= db.addPoint(id, ssid, name, longitude, latitude);

			}
			//only add project to project db if all previous adds passed
			if(addPassed)
				addPassed &= db.addProject(id, name);
			result = new PluginResult(addPassed?Status.OK:Status.ERROR);
			db.close();
		}
		else if(action.matches(ACTION_GET_NOTIFY_DETAILS)){
			JSONObject retVal = new JSONObject();
			try {
				retVal.put(Defined.KEY_PROJECT_NAME, Shared.getProjectName());
				retVal.put(Defined.KEY_LOGGING_IN, Shared.mLoggingIn);
				retVal.put(Defined.KEY_PROJECT_ID, Shared.getProjectID());
			} catch (JSONException e) {
				return  new PluginResult(Status.ERROR, getJsonObjectError(e));
			}
			db.close();
			return new PluginResult(Status.OK, retVal);
		}

		else if(action.matches(ACTION_GET_LOG)){

			JSONObject retVal = new JSONObject();
			try {
				JSONArray logDetails = new JSONArray(db.getAllTimeStamps());
				retVal.put(Defined.KEY_LOG_ARRAY, logDetails);
			} catch (JSONException e) {
				db.close();
				return  new PluginResult(Status.ERROR, getJsonObjectError(e));
			}
			db.close();
			return new PluginResult(Status.OK, retVal);
		}
		else if(action.matches(ACTION_GET_WIFI_STATE)){
			WifiManager wMNG = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
			return new PluginResult(Status.OK, wMNG.isWifiEnabled());
		}
		else if(action.matches(ACTION_SET_WIFI_STATE)){
			//grab boolean param
			Boolean state;
			try {
				state = data.getJSONObject(0).getBoolean("state");
			} catch (JSONException e) {
				db.close();
				return  new PluginResult(Status.ERROR, getJsonObjectError(e));
			}
			WifiManager wifiManager = (WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(state);
			//return current wifi state
			return new PluginResult(Status.OK, wifiManager.isWifiEnabled());
			
		}
		else if(action.matches(ACTION_GET_NOTIFICATION_STATE)){

		}
		else if(action.matches(ACTION_SET_NOTIFICATION_STATE)){

		}



		if(result == null)
			result = new PluginResult(Status.ERROR, getJsonObjectError(new JSONException("no value")));
		db.close();
		return result;
	}

	private JSONObject getJsonObjectError(JSONException e) {
		JSONObject retVal = new JSONObject();
		try {
			retVal.put(Defined.KEY_ERR_MESSAGE, e.getMessage());
			retVal.put(Defined.KEY_ERR_STACK, e.getStackTrace().toString());
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return retVal;
	}

	private PluginResult log(boolean login, JSONArray data, Context context){
		try {
			String projectID = data.getJSONObject(0).getString("project_id");
			if(projectID.matches(""))
				return new PluginResult(Status.ERROR);
			//grab database
			DBTool db = new DBTool(context);
			//set new login status
			boolean success = db.setLoggedIn(login, projectID);
			//log new status in tracker
			success &= db.setLoggedInTimestamp(login, projectID);
			db.close();
			if(success)
				return new PluginResult(Status.OK);
			else
				return new PluginResult(Status.ERROR);

		} catch (JSONException e) {
			Log.d(TAG, "error setting point:" + e.getMessage());
			return new PluginResult(Status.ERROR);
		}
	}

}
