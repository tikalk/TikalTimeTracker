package com.tikalk.tools;
/**
 * Constants defined for multiple classes
 */
public class Defined {
	//intent details for notification EXACT SPELLING OR WILL NOT WORK
	public static final String INTENT_PACKAGE = "com.tikal.time.tracker";
	public static final String INTENT_ACTIVITY = "com.tikal.time.tracker.TikalTimeTrackerActivity";
	
	//ssid for location
	public static final String KEY_PROJECT_NAME = "project_name";
	//boolean for if user is logging in or out (true = user loggin into location)
	public static final String KEY_LOGGING_IN = "login";
	//id for the project being logged into or out of
	public static final String KEY_PROJECT_ID = "project_id";
	//key for jsonarray of log
	public static final String KEY_LOG_ARRAY = "log_array";
	//if declared ssid exists in the database of saved points
	public static final String KEY_EXISTS = "exists";
	//boolean for if spot is currently logged in or out
	public static final String KEY_LOGGED_IN = "logged_in";
	
	//ERROR KEYS
	// message key
	public static final String KEY_ERR_MESSAGE = "message";
	//stacktrace key
	public static final String KEY_ERR_STACK = "stack";
	

}
