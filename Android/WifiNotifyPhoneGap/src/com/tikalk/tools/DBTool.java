package com.tikalk.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tikalk.wifilistener.WifiListenerService;
import com.tikalk.wifinotify.plugin.WifiListener;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class DBTool extends SQLiteOpenHelper {

	/**
	 * CONSTANTS
	 */
	public static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "wifi_notify";
	public static final String TABLE_SPOTS = "wifi_spots";
	public static final String TABLE_TIMES = "checkin_times";
	public static final String TABLE_PROJECTS = "projects_status";

	//keys
	//project id as obtained from 4[]
	public static final String KEY_PROJECT_ID = "project_id";//TEXT
	//a unique int for sending notifications
	public static final String KEY_PROJECT_NOTIFY_ID = "notify_id";//INT
	//bssid of a hotspot
	public static final String KEY_BSSID = "bssid";//TEXT
	//longitude for a project
	public static final String KEY_LONGITUDE = "long";//TEXT
	//latitude for a project
	public static final String KEY_LATITUDE = "lat";//TEXT
	//name of a project as provided by 4[]
	public static final String KEY_PROJECT_NAME = "project_name";//TEXT
	//int/boolen that declares if currently logged into a specific project
	public static final String KEY_LOGGED_IN = "logged_in";//INT
	//a timestamp for when logging into a project, stored as mili since epoch
	public static final String KEY_TIMESTAMP_START = "timestamp_start";//INT
	//a timestamp for when logging out of project, stored as mili since epoch
	public static final String KEY_TIMESTAMP_STOP = "timestamp_stop";//INT
	//int/boolean that declares if the project is set up for auto updating
	public static final String KEY_AUTO_UPDATE = "auto_update";//INT

	//values for logged in int/boolean
	public static final int BOOL_TRUE = 0;
	public static final int BOOL_FALSE = 1;

	public static final String HOTSPOT_TABLE_CREATE =
			"CREATE TABLE " + TABLE_SPOTS + " (" +
					KEY_PROJECT_ID + " TEXT, " + KEY_BSSID + " TEXT, " +
					KEY_PROJECT_NAME + " TEXT, "   + KEY_LATITUDE + " TEXT, " + KEY_LONGITUDE + " TEXT);";

	public static final String TIMESTAMP_TABLE_CREATE =
			"CREATE TABLE " + TABLE_TIMES + " (" +
					KEY_PROJECT_ID + " INT, " + KEY_PROJECT_NAME + " TEXT, " +
					KEY_TIMESTAMP_START + " INT, " + KEY_TIMESTAMP_STOP + " INT);";

	public static final String PROJECTS_TABLE_CREATE =
			"CREATE TABLE " + TABLE_PROJECTS + " (" +
					KEY_PROJECT_ID + " TEXT, "+ KEY_PROJECT_NOTIFY_ID + " INT, " + KEY_PROJECT_NAME + " TEXT, " +
					KEY_LOGGED_IN + " INT, " + 
					KEY_AUTO_UPDATE+ " INT);";



	public DBTool(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("db", "on create called");
		try{
			db.execSQL(HOTSPOT_TABLE_CREATE);
			db.execSQL(TIMESTAMP_TABLE_CREATE);
			db.execSQL(PROJECTS_TABLE_CREATE);
		}
		catch(SQLException e){
			Log.d("db", "create exception: " + e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPOTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
		// Create tables again
		onCreate(db);
	}

	//access functions

	// Adding new spot
	public boolean addPoint(String id, String bSSID, String projectName, String longitude, String latitude) {
		SQLiteDatabase db = this.getWritableDatabase();
		//make sure project doesn't already exist
		if(existsProject(id))
			return false;

		//create project argument
		ContentValues values = new ContentValues();
		values.put(KEY_PROJECT_ID, id); //id for spot
		values.put(KEY_BSSID, bSSID);// SSID for checkin spot
		values.put(KEY_PROJECT_NAME, projectName); // name of this checkin spot
		values.put(KEY_LONGITUDE, longitude); // location data
		values.put(KEY_LATITUDE, latitude);  // location data


		// Inserting Row
		long row = db.insert(TABLE_SPOTS, null, values);
		return(row != -1);
	}
	// Adding project with not loged in value
	public boolean addProject(String id, String projectName) {
		SQLiteDatabase db = this.getWritableDatabase();
		//make sure project doesn't already exist
		if(existsProject(id))
			return false;

		//create project argument
		ContentValues values = new ContentValues();
		values.put(KEY_PROJECT_ID, id); //id for spot
		values.put(KEY_PROJECT_NAME, projectName); // name of this checkin spot
		values.put(KEY_LOGGED_IN, BOOL_FALSE);
		values.put(KEY_PROJECT_NOTIFY_ID, Shared.getNotifyID());//id for the android notification
		values.put(KEY_AUTO_UPDATE, BOOL_TRUE);//all projects are originally set for auto login
		// Inserting Row
		long row = db.insert(TABLE_PROJECTS, null, values);
		return(row != -1);
	}

	//removes project and all references to it in the table_spots
	public boolean removeProject(String id){
		SQLiteDatabase db = this.getWritableDatabase();
		int projectDelete = db.delete(TABLE_PROJECTS, KEY_PROJECT_ID + " = ?", new String[]{id});
		int spotsDelete = db.delete(TABLE_SPOTS, KEY_PROJECT_ID + " = ?" ,new String[]{ id});
		return(projectDelete>0 && spotsDelete > 0);
	}


	//get id for project
	public String getID(String projectName) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_NAME, KEY_PROJECT_ID}, KEY_PROJECT_NAME + "=?",
				new String[] { projectName}, null, null, null, null);
		String pairs = "";
		if (cursor != null){
			cursor.moveToFirst();
			pairs = cursor.getString(1);
		}
		cursor.close();
		cursor = null;
		return pairs;
	}

	//get notify id for project
	public int getNotificationID(String projectName) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_NAME, KEY_PROJECT_NOTIFY_ID}, KEY_PROJECT_NAME + "=?",
				new String[] { projectName}, null, null, null, null);
		int pairs = -1;
		if (cursor != null){
			cursor.moveToFirst();
			pairs = cursor.getInt(1);
		}
		cursor.close();
		cursor = null;
		return pairs;
	}
	//get location
	//get id for bssid
	public Location getLocation(String bSSID, String projectID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_SPOTS, new String[] { 
				KEY_BSSID, KEY_LONGITUDE, KEY_LATITUDE}, KEY_BSSID + "=? AND " + KEY_PROJECT_ID + " =?",
				new String[] { bSSID, projectID}, null, null, null, null);
		Location pairs = new Location("dummy");
		if (cursor != null && cursor.moveToFirst()){
			pairs.setLatitude(Double.valueOf(cursor.getString(2)));
			pairs.setLongitude(Double.valueOf(cursor.getString(1)));
		}
		cursor.close();
		cursor = null;
		return pairs;
	}

	//returns if succesfully logs in or logs out of spot
	public boolean setLoggedIn(boolean loggedIn, String projectID){
		//if doesn't exist or is already logged in/out
		if(!existsProject(projectID) || (isLoggedIn(projectID) == loggedIn))
			return false;
		//if someone else is already logged in
		if(loggedIn && !loggedInProject().matches(""))
			return false;
		int rowsAffected =0;
		int loggedInInt = loggedIn?BOOL_TRUE:BOOL_FALSE;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues args = new ContentValues();
		args.put(KEY_LOGGED_IN,loggedInInt);
		rowsAffected += db.update(TABLE_PROJECTS, args, KEY_PROJECT_ID + " = \'" + projectID + "\'", null);

		return(rowsAffected >= 1);
	}
	//set auto updates for a specific project
	public boolean setAutoUpdate(boolean toUpdate, String projectID){
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues args = new ContentValues();
		args.put(KEY_AUTO_UPDATE,((toUpdate)?BOOL_TRUE:BOOL_FALSE));
		int rowsAffected = db.update(TABLE_PROJECTS, args, KEY_PROJECT_ID + " = \'" + projectID + "\'", null);

		return(rowsAffected >= 1);

	}
	//get the auto update value for a specific project
	public boolean getAutoUpdate(String id){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_ID, KEY_AUTO_UPDATE}, KEY_PROJECT_ID + " = " + "\'" +  id + "\'",
				new String[] {}, null, null, null, null);
		int status = BOOL_FALSE;
		if (cursor != null && cursor.getCount()  >= 1){
			cursor.moveToFirst();
			status = cursor.getInt(1);
		}
		cursor.close();
		cursor = null;
		return status == BOOL_TRUE;
	}

	//add timestamp for logged in
	public boolean setLoggedInTimestamp(boolean loggedIn, String projectID){
		if(!existsProject(projectID))
			return false;
		int rowsAffected =0;

		SQLiteDatabase db = this.getWritableDatabase();
		//if logging in
		if(loggedIn){
			ContentValues newTimestamp = new ContentValues();
			newTimestamp.put(KEY_PROJECT_ID, projectID);
			newTimestamp.put(KEY_PROJECT_NAME, getProjectNameFromID(projectID));
			//set start and end time to the the same time
			long currentTime = System.currentTimeMillis();
			newTimestamp.put(KEY_TIMESTAMP_START,currentTime);
			newTimestamp.put(KEY_TIMESTAMP_STOP,currentTime);
			//insert new timestamp to table
			rowsAffected += db.insert(TABLE_TIMES, null, newTimestamp);
		}
		//if logging out
		else{
			//find and update where start = stop and id = given
			ContentValues updateTimestamp = new ContentValues();
			updateTimestamp.put(KEY_TIMESTAMP_STOP,Long.toString(System.currentTimeMillis()));
			
			rowsAffected += db.update(TABLE_TIMES, updateTimestamp, 
					KEY_TIMESTAMP_START + "="  + KEY_TIMESTAMP_STOP + " AND " 
					+ KEY_PROJECT_ID + "=?", new String[]{projectID});  
			
		}

		return(rowsAffected >= 1);
	}
	public String getProjectNameFromID(String projectID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_NAME, KEY_PROJECT_ID}, KEY_PROJECT_ID + " = " + "\'" +  projectID + "\'",
				new String[] {}, null, null, null, null);
		String projectName = "";
		if (cursor != null && cursor.getCount()  >= 1){
			cursor.moveToFirst();
			projectName = cursor.getString(0);
		}
		cursor.close();
		cursor = null;
		return projectName;
	}

	// Getting single spot's logged in status
	public boolean isLoggedIn(String projectID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_ID, KEY_LOGGED_IN}, KEY_PROJECT_ID + " = " + "\'" +  projectID + "\'",
				new String[] {}, null, null, null, null);
		int status = BOOL_FALSE;
		if (cursor != null && cursor.getCount()  >= 1){
			cursor.moveToFirst();
			status = cursor.getInt(1);
		}
		cursor.close();
		cursor = null;
		return status == BOOL_TRUE;
	}
	//checks if currently logged into a project, if so then returns projectID
	public String loggedInProject(){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_ID, KEY_LOGGED_IN}, KEY_LOGGED_IN + " = " + BOOL_TRUE,
				new String[] {}, null, null, null, null);
		String bssid = "";
		if (cursor != null && cursor.getCount()  >= 1){
			cursor.moveToFirst();
			bssid = cursor.getString(0);
		}
		cursor.close();
		cursor = null;
		return bssid;
	}


	// Getting All spots
	public List<String> getAllSpots() {
		List<String> spotsList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT " + KEY_BSSID + " FROM " + TABLE_SPOTS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				spotsList.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		// return contact list
		return spotsList;
	}
	// Getting All wifi spots for a specific project
	public List<String> getAllSpots(String projectID) {
		List<String> spotsList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT " + KEY_BSSID + " FROM " + TABLE_SPOTS + " WHERE "
				+ KEY_PROJECT_ID + " = \'" + projectID + "\'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				spotsList.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		cursor.close();
		// return contact list
		return spotsList;
	}

	// Getting All timestamps for a certain month in a certain year
	public JSONArray getAllTimeStamps(int year, int month) {
		Date startDate = new Date(year, month, 0);
		long startTimeStamp = startDate.getTime();
		Date endDate = new Date(year, month, 31);
		long endTimeStamp = endDate.getTime();
		JSONArray timeLogJSON = new JSONArray();

		// Select All Query
		//String selectQuery = "SELECT " + KEY_PROJECT_NAME + ", " + KEY_TIMESTAMP_START +", " + KEY_TIMESTAMP_STOP + " " + "FROM " + TABLE_TIMES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TIMES, new String[]{KEY_PROJECT_NAME, KEY_TIMESTAMP_START, KEY_TIMESTAMP_STOP},
								KEY_TIMESTAMP_START + " > ?" + " AND " + KEY_TIMESTAMP_START + " < ?",
								new String[]{Long.toString(startTimeStamp), Long.toString(endTimeStamp)}
								, null, null, KEY_TIMESTAMP_START);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				try {
					//String loggedIn = cursor.getInt(2) == BOOL_TRUE?"--logged in":"--logged out";
					//create a json object with all of the items
					JSONObject tempObject = new JSONObject();
					tempObject.put(WifiListener.KEY_PROJECT_NAME, cursor.getString(0));
					//convert timestamps to strings
					long tStart = cursor.getLong(1), tEnd = cursor.getLong(2);
					SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  hh:mm");
					tempObject.put(WifiListener.KEY_TIMESTAMP_START, format.format(new Date(tStart)));
					tempObject.put(WifiListener.KEY_TIMESTAMP_STOP, format.format(new Date(tEnd)));

					timeLogJSON.put(tempObject);
				} catch (JSONException e) {
					// uh oh errors return null
					return null;
				}

				//spotsList.add(cursor.getString(0) + ", " + cursor.getString(1) + ", "+ loggedIn);
			} while (cursor.moveToNext());
		}
		cursor.close();
		// return contact list
		return timeLogJSON;
	}
	//checks if the waypoint exists
	public boolean existsBSSID(String bSSID){
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_SPOTS + " WHERE " + KEY_BSSID + " = \'" + bSSID + "\'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor;
		try{
			cursor = db.rawQuery(selectQuery, null);
		}
		catch (Exception e) {
			//if column does not exist
			return false;
		}
		if(cursor == null)
			return false;
		int count  = cursor.getCount();
		cursor.close();
		return (count >=1);
	}
	//checks if the project is saved exists
	public boolean existsProject(String projectID){
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_PROJECT_ID + " = \'" + projectID + "\'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor;
		try{
			cursor = db.rawQuery(selectQuery, null);
		}
		catch (Exception e) {
			//if column does not exist
			return false;
		}
		if(cursor == null)
			return false;
		int count  = cursor.getCount();
		cursor.close();
		return (count >=1);
	}
	//clears all items from the table
	public void clearTable(){
		// Select All Query
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_SPOTS, null, null);
		db.delete(TABLE_TIMES, null, null);
	}
	//returns all projectids with associated bssid with the 
	public List<String> getProjectIDsForBSSID(String bssid) {
		List<String> projecIDs = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_SPOTS, new String[] { 
				KEY_PROJECT_ID}, KEY_BSSID + " = ?",
				new String[] {bssid}, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				projecIDs.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		cursor.close();
		cursor = null;
		return projecIDs;
	}





}
