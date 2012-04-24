package com.tikalk.tools;


//this class holds a pending event
public class PendingEvent {
	/**
	 * CONSTANTS
	 */
//	public static final int EVENT_ADD_POINT = 0;
	public static final int EVENT_VERIFY_LOGIN_SPOT = 1;
	public static final int EVENT_LOGOUT = 2;
	
	/**
	 * MEMBERS
	 */
	private String mSsid;
	private int mType;
	
	/**
	 * METHODS
	 */
	//constructor
	public PendingEvent(String ssid, int eventType){
		mType = eventType;
		mSsid = ssid;
	}

	
	//getters
	public int getType(){
		return mType;
	}
	
	public String getSSID(){
		return mSsid;
	}
	
	
}
