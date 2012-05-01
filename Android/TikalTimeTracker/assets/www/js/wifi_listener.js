/**
 *  
 * @return Object literal singleton instance of DirectoryListing
 */
var WifiListener = function() {
};


/**
 * Initialize Wifi listening
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.start = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'start',								
			[data]);
};

/**
 * Kill Wifi listening
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.stop = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'stop',								
			[data]);
};

/**
 * checks if listening service is running
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.service_running = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'service_running',								
			[data]);
};

/**
 * Get all current active wifi spots
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback
 * @param failureCallback	The error callback 
 */
WifiListener.prototype.get_active = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'get_active',								
			[data]);
};

///**
//* NOT IMPLEMENTED YET
//* Get all projects
//* 
//* @param data				Currently no data is needed, may be utilized in future editions
//* @param successCallback	The success callback
//* @param failureCallback	The error callback 
//*/
//WifiListener.prototype.get_active = function(data, successCallback, failureCallback) {
//	return PhoneGap.exec(
//			successCallback,			 
//			failureCallback,						
//			'WifiListener',				
//			'get_active',								
//			[data]);
//};

/**
 * Get all current wifi spots
 * 
 * @param data				the ssid, longitude, and latitude for the point
 * @param successCallback	The success callback
 * @param failureCallback	The error callback 
 */
WifiListener.prototype.set_point = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'set_point',								
			[data]);
};


/**
 * gets the details of the notification that the user just clicked
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.get_notify_details = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'get_notify_details',								
			[data]);
};

/**
 * logs in to the given spot
 * 
 * @param data				ssid for given spot
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.login = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'login',								
			[data]);
};

/**
 * logs out of the given spot
 * 
 * @param data				ssid for given spot
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.logout = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'logout',								
			[data]);
};

/**
 * returns the formatted log of loggins and logouts
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback
 * @param failureCallback	The error callback
 */
WifiListener.prototype.get_log = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'get_log',								
			[data]);
};

PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("WifiListener", new WifiListener());
});