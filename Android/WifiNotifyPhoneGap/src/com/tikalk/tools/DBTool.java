package com.tikalk.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "wifi_notify";
	public static final String TABLE_SPOTS = "wifi_spots";
	public static final String TABLE_TIMES = "checkin_times";
	public static final String TABLE_PROJECTS = "projects_status";

	//keys
	public static final String KEY_PROJECT_ID = "project_id";//TEXT
	public static final String KEY_PROJECT_NOTIFY_ID = "notify_id";//INT
	public static final String KEY_SSID = "ssid";//TEXT
	public static final String KEY_LONGITUDE = "long";//TEXT
	public static final String KEY_LATITUDE = "lat";//TEXT
	public static final String KEY_PROJECT_NAME = "project_name";//TEXT
	public static final String KEY_LOGGED_IN = "logged_in";//INT
	public static final String KEY_TIMESTAMP = "timestamp";//TEXT

	//values for logged in int/boolean
	public static final int LOGGED_IN = 0;
	public static final int NOT_LOGGED_IN = 1;

	public static final String HOTSPOT_TABLE_CREATE =
			"CREATE TABLE " + TABLE_SPOTS + " (" +
					KEY_PROJECT_ID + " TEXT, " + KEY_SSID + " TEXT, " +
					KEY_PROJECT_NAME + " TEXT, "   + KEY_LATITUDE + " TEXT, " + KEY_LONGITUDE + " TEXT);";

	public static final String TIMESTAMP_TABLE_CREATE =
			"CREATE TABLE " + TABLE_TIMES + " (" +
					KEY_PROJECT_ID + " INT, " + KEY_PROJECT_NAME + " TEXT, " +
					KEY_TIMESTAMP + " TEXT, " + KEY_LOGGED_IN + " INT);";

	public static final String PROJECTS_TABLE_CREATE =
			"CREATE TABLE " + TABLE_PROJECTS + " (" +
					KEY_PROJECT_ID + " TEXT, "+ KEY_PROJECT_NOTIFY_ID + " INT, " + KEY_PROJECT_NAME + " TEXT, " +
					KEY_LOGGED_IN + " INT);";



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
	public boolean addPoint(String id, String SSID, String projectName, String longitude, String latitude) {
		SQLiteDatabase db = this.getWritableDatabase();
		//make sure project doesn't already exist
		if(existsProject(id))
			return false;

		//create project argument
		ContentValues values = new ContentValues();
		values.put(KEY_PROJECT_ID, id); //id for spot
		values.put(KEY_SSID, SSID);// SSID for checkin spot
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
		values.put(KEY_LOGGED_IN, NOT_LOGGED_IN);
		values.put(KEY_PROJECT_NOTIFY_ID, Shared.getNotifyID());//id for the android notification

		// Inserting Row
		long row = db.insert(TABLE_PROJECTS, null, values);
		return(row != -1);
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
	//get id for ssid
	public Location getLocation(String SSID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_SPOTS, new String[] { 
				KEY_SSID, KEY_LONGITUDE, KEY_LATITUDE}, KEY_SSID + "=?",
				new String[] { SSID}, null, null, null, null);
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
		int loggedInInt = loggedIn?LOGGED_IN:NOT_LOGGED_IN;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues args = new ContentValues();
		args.put(KEY_LOGGED_IN,loggedInInt);
		rowsAffected += db.update(TABLE_PROJECTS, args, KEY_PROJECT_ID + " = \'" + projectID + "\'", null);

		return(rowsAffected >= 1);
	}
	//add timestamp
	public boolean setLoggedInTimestamp(boolean loggedIn, String projectID){
		if(!existsProject(projectID))
			return false;
		int rowsAffected =0;
		int loggedInInt = loggedIn?LOGGED_IN:NOT_LOGGED_IN;

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues newTimestamp = new ContentValues();
		newTimestamp.put(KEY_PROJECT_ID, projectID);
		newTimestamp.put(KEY_PROJECT_NAME, getProjectNameFromID(projectID));
		//get date
		Date current = new Date(System.currentTimeMillis());
		newTimestamp.put(KEY_TIMESTAMP, current.getDate() + ", " + current.getHours() + ":" + current.getMinutes());
		newTimestamp.put(KEY_LOGGED_IN, loggedInInt);
		//insert new timestamp to table
		rowsAffected += db.insert(TABLE_TIMES, null, newTimestamp);

		return(rowsAffected >= 1);
	}
	private String getProjectNameFromID(String projectID) {
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
		int status = NOT_LOGGED_IN;
		if (cursor != null && cursor.getCount()  >= 1){
			cursor.moveToFirst();
			status = cursor.getInt(1);
		}
		cursor.close();
		cursor = null;
		return status == LOGGED_IN;
	}
	//checks if currently logged into a project, if so then returns projectID
	public String loggedInProject(){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PROJECTS, new String[] { 
				KEY_PROJECT_ID, KEY_LOGGED_IN}, KEY_LOGGED_IN + " = " + LOGGED_IN,
				new String[] {}, null, null, null, null);
		String ssid = "";
		if (cursor != null && cursor.getCount()  >= 1){
			cursor.moveToFirst();
			ssid = cursor.getString(0);
		}
		cursor.close();
		cursor = null;
		return ssid;
	}


	// Getting All spots
	public List<String> getAllSpots() {
		List<String> spotsList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT " + KEY_SSID + " FROM " + TABLE_SPOTS;

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
		String selectQuery = "SELECT " + KEY_SSID + " FROM " + TABLE_SPOTS + " WHERE "
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

	// Getting All timestamps
	public List<String> getAllTimeStamps() {
		List<String> spotsList = new ArrayList<String>();
		// Select All Query
		String selectQuery = "SELECT " + KEY_PROJECT_NAME + ", " + KEY_TIMESTAMP +", " + KEY_LOGGED_IN + " " + "FROM " + TABLE_TIMES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String loggedIn = cursor.getInt(2) == LOGGED_IN?"--logged in":"--logged out";
				spotsList.add(cursor.getString(0) + ", " + cursor.getString(1) + ", "+ loggedIn);
			} while (cursor.moveToNext());
		}
		cursor.close();
		// return contact list
		return spotsList;
	}
	//checks if the waypoint exists
	public boolean existsSSID(String SSID){
		// Select All Query
		String selectQuery = "SELECT * FROM " + TABLE_SPOTS + " WHERE " + KEY_SSID + " = \'" + SSID + "\'";

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

	public String getProjectName(String ssid) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_SPOTS, new String[] { 
				KEY_PROJECT_NAME, KEY_SSID}, KEY_SSID + " = " + "\'" +  ssid + "\'",
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





}
