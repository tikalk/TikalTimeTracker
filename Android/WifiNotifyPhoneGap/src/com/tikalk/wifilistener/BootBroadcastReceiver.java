package com.tikalk.wifilistener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {	
	/**
	 * CONSTANTS
	 */
	public static final String TAG = BootBroadcastReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "boot receiver loaded");
		//if service is not running launch it
		if(!WifiListenerService.isServiceRunning(context)){
			Log.d(TAG, "service not running, launching from wifi change reciever");
			//insure that the service is running
			Intent startServiceIntent = new Intent(context, WifiListenerService.class);
			context.startService(startServiceIntent);
		}
	}

}
