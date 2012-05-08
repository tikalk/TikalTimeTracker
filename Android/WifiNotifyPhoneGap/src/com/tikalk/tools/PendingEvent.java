package com.tikalk.tools;


//this class holds a pending event
public class PendingEvent {
	/**
	 * CONSTANTS
	 */
//	public static final int EVENT_ADD_POINT = 0;
	public static final int EVENT_VERIFY_LOGIN = 1;
	public static final int EVENT_VERIFY_LOGOUT = 2;
	
	/**
	 * MEMBERS
	 */
	//bssid of hotspot
	private String mBssid;
	//id of project
	private String mProjectID;
	//type of event (login or logout)
	private int mType;
	
	/**
	 * METHODS
	 */
	//constructor
	public PendingEvent(String bssid, String projectID, int eventType){
		mType = eventType;
		mBssid = bssid;
		mProjectID = projectID;
	}

	
	//getters
	public int getType(){
		return mType;
	}
	
	public String getBSSID(){
		return mBssid;
	}
	
	public String getProjectID(){
		return mProjectID;
	}
	
	
}
