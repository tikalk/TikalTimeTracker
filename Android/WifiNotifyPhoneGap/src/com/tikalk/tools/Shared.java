package com.tikalk.tools;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Shared {

	/**
	 * SHARED RESOURCES
	 */
	//current wifi networks that are accessable
	private static List<String> mCurrentSpots;
	//id for notifications
	private static int mNotifyID = 0;
	//boolean that lets us know if we are waiting for a curring location
	public static boolean mRequestingLocation;
	//this queue holds all of the pending events for when locationsingleupdatebroadcastreceiver recieves an event
	private static Queue<PendingEvent> mEventQueue;
	//this string holds the last value for the last project name called from a notification click
	private static String mProjectName;
	//this is the last value of calling project id
	private static String mProjectID;
	//this string holds the last value for the boolean of logging in (or false=logging out)
	//for last notification click
	public static boolean mLoggingIn;


	/**
	 * STATIC AREA
	 */

	static{
		mRequestingLocation = false;
		mEventQueue = new LinkedList<PendingEvent>();
		mCurrentSpots = new ArrayList<String>();
	}

	/**
	 * METHODS
	 */
	//notification vairables
	public static String getProjectName() {
		return mProjectName;
	}

	public static String getProjectID() {
		return mProjectID;
	}

	public static void setNotifyClick(String ssid, boolean loggingIn, String projectID) {
		mProjectName = ssid;
		mLoggingIn = loggingIn;
		mProjectID = projectID;
	}

	//queue methods
	//TODO remove this and only use getNext?
	public static boolean queueHasNext(){
		synchronized (mEventQueue) {
			return(mEventQueue.peek() != null);
		}
	}

	public static PendingEvent queueGetNext(){
		synchronized (mEventQueue) {
			//get next item
			if(queueHasNext())
				return(mEventQueue.remove());
			else
				return null;
		}
	}

	public static boolean queueAddEvent(PendingEvent event){
		synchronized (mEventQueue) {
			return mEventQueue.offer(event);
		}
	}




	public synchronized static int getNotifyID() {
		if(mNotifyID == Integer.MAX_VALUE)
			mNotifyID =0;
		return mNotifyID++;
	}

	//list functions
	public static List<String> getCurrentSpots() {
		synchronized(mCurrentSpots){
			//if null list then create an empty list
			if(mCurrentSpots == null)
				mCurrentSpots = new ArrayList<String>();
			//return copy of list
			return new ArrayList<String>(mCurrentSpots);
		}
	}

	public static void setCurrentSpots(List<String> currentSpots) {
		synchronized(mCurrentSpots){
			Shared.mCurrentSpots = currentSpots;
		}
	}


}
