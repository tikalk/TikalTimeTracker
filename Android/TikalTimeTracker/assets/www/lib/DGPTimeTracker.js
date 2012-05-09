/**
 * DGPTimeTracker.js
 *  
 * Phonegap Geofencing plugin
 * Copyright (c) Dov Goldberg 2012
 *
 */
var DGPTimeTracker = {
		
		/**
		 * Initialize Wifi listening
		 * 
		 * @param data				Currently no data is needed, may be utilized in future editions
		 * @param successCallback	The success callback
		 * @param failureCallback	The error callback
		 */
		start: function(data, successCallback, failureCallback) {
			return PhoneGap.exec(
					successCallback,			 
					failureCallback,						
					'DGPTimeTracker',				
					'start',								
					[data]);
		},

		/**
		 * Kill Wifi listening
		 * 
		 * @param data				Currently no data is needed, may be utilized in future editions
		 * @param successCallback	The success callback
		 * @param failureCallback	The error callback
		 */
		stop: function(data, successCallback, failureCallback) {
			return PhoneGap.exec(
					successCallback,			 
					failureCallback,						
					'DGPTimeTracker',				
					'stop',								
					[data]);
		},

		/**
		 * checks if listening service is running
		 * 
		 * @param data				Currently no data is needed, may be utilized in future editions
		 * @param successCallback	The success callback
		 * @param failureCallback	The error callback
		 */
		service_running: function(data, successCallback, failureCallback) {
			return PhoneGap.exec(
					successCallback,			 
					failureCallback,						
					'DGPTimeTracker',				
					'service_running',								
					[data]);
		},

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
	#define KEY_PROJECT_CURRENTLY_HERE          @"currentlyhere"
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