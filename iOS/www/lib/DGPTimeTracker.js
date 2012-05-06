/**
 * DGPTimeTracker.js
 *  
 * Phonegap Geofencing plugin
 * Copyright (c) Dov Goldber 2012
 *
 */
var DGPTimeTracker = {
	/*
	Params:
	#define KEY_REGION_ID       @"fid"
	#define KEY_PROJECT_NAME    @"projectname"
	#define KEY_PROJECT_LAT     @"latitude"
    #define KEY_PROJECT_LNG     @"longitude"
    #define KEY_PROJECT_ADDRESS @"address"
	*/
     addProject: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGPTimeTracker", "addProject", [params]);
     },

    /*
	Params:
	#define KEY_REGION_ID       @"fid"
	#define KEY_PROJECT_NAME    @"projectname"
	#define KEY_PROJECT_LAT     @"latitude"
    #define KEY_PROJECT_LNG     @"longitude"
    #define KEY_PROJECT_ADDRESS @"address"
	*/
     removeProject: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGPTimeTracker", "removeProject", [params]);
     },
	
	/*
	Params:
	#define KEY_REGION_ID       				@"fid"
	#define KEY_PROJECT_LAT     				@"latitude"
    #define KEY_PROJECT_LNG     				@"longitude"
	*/
     setShouldAutoUpdateProjectEvents: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGPTimeTracker", "setShouldAutoUpdateProjectEvents", [params]);
     },
	
	/*
	Params:
	#define KEY_REGION_ID       				@"fid"
	Returns
	#define KEY_PROJECT_SHOULD_AUTO_UPDATE      @"shouldautoupdate"
	*/
     getShouldAutoUpdateProjectEvents: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGPTimeTracker", "getShouldAutoUpdateProjectEvents", [params]);
     },

	/*
	Params:
	#define KEY_REGION_ID       				@"fid"
	*/
     checkinToProject: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGPTimeTracker", "checkinToProject", [params]);
     },
	
	/*
	Params:
	#define KEY_REGION_ID       				@"fid"
	*/
     checkoutOfProject: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "DGPTimeTracker", "checkoutOfProject", [params]);
     }
};