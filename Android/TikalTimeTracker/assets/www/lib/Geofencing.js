/**
 * Geofencing.js
 *  
 * Phonegap Geofencing plugin
 * Copyright (c) Dov Goldber 2012
 *
 */
var Geofencing = {
	/*
	Params:
	#define KEY_REGION_ID       @"fid"
	#define KEY_PROJECT_NAME    @"projectname"
	#define KEY_PROJECT_LAT     @"latitude"
	#define KEY_PROJECT_LNG     @"longitude"
    #define KEY_PROJECT_ADDRESS @"address"
	*/
     addRegion: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "Geofencing", "addRegion", [params]);
     },

     /*
	Params:
	#define KEY_REGION_ID       @"fid"
	#define KEY_PROJECT_NAME    @"projectname"
	#define KEY_PROJECT_LAT     @"latitude"
    #define KEY_PROJECT_LNG     @"longitude"
    #define KEY_PROJECT_ADDRESS @"address"
	*/
     removeRegion: function(params, success, fail) {
          return PhoneGap.exec(success, fail, "Geofencing", "removeRegion", [params]);
     }
};