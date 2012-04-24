package com.tikalk.wifilistener;

import java.util.Iterator;
import java.util.List;

import com.tikalk.tools.DBTool;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class WifiListenerService extends Service {
	/**
	 * CONSTANTS
	 */
	public final static String TAG = WifiListenerService.class.getName();

	/**
	 * MEMBERS
	 */
	//broadcast receiver for wifi changes
	BroadcastReceiver mWifiChanges;
	//broadcast receiver for boot completed
	BroadcastReceiver mBootCompleted;
	// This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//int retCode =  super.onStartCommand(intent, flags, startId);
		//add broadcast receievers\
		Log.d("registerReciever", "registering");
		//for wifi we want when scan results are available or wifi state changed
		Log.d(TAG, "adding wifi broadcast receiver");
		mWifiChanges = new WifiChangeBroadcastReciever();
		
		IntentFilter scansAvailable = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mWifiChanges, scansAvailable);
		IntentFilter wifiChange = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(mWifiChanges, wifiChange);
		
		Log.d(TAG, "adding boot broadcast receiver");
		IntentFilter bootFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
		mBootCompleted = new BootBroadcastReceiver();
		registerReceiver(mBootCompleted, bootFilter);
		
		return START_STICKY;
	}

	
	
	public static boolean isServiceRunning(Context context){
		boolean serviceRunning = false;
		ActivityManager actManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> runningServicesList = actManager.getRunningServices(200);
		Iterator<ActivityManager.RunningServiceInfo> listIterator = runningServicesList.iterator();
		while (listIterator.hasNext()) {
			ActivityManager.RunningServiceInfo runningServiceInfo =
				(ActivityManager.RunningServiceInfo) listIterator.next();
			//Log.d(TAG, runningServiceInfo.service.getClassName() + "is running");
			if(runningServiceInfo.service.getClassName().equals(WifiListenerService.class.getName())){
				serviceRunning = true;
			}
		}
		return serviceRunning;
	}
	//when destroyed make sure to remove all of the intent listners
	public void unregisterRecievers(){
		Log.d("registerReciever", "unregistering");
		unregisterReceiver(mBootCompleted);
		unregisterReceiver(mWifiChanges);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	/**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
    	public WifiListenerService getService() {
            return WifiListenerService.this;
        }
    }
}
