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
 * @param data				"projectid" - id passed by 4[], "logitude", "latitude", "projectname" - name passed by 4[]
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
 * @param data				"project_id" - id passed by 4[]
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
 * @param data				"project_id" - id passed by 4[]
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

/**
 * returns boolean of what wifi state is
 * 
 * @param data				Currently no data is needed, may be utilized in future editions
 * @param successCallback	The success callback (returns boolean: if true wifi is on if false wifi is off)
 * @param failureCallback	The error callback
 */
WifiListener.prototype.get_wifi_state = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'get_wifi_state',								
			[data]);
};

/**
 * sets current wifi state to the given boolean
 * 
 * @param data				"state" - true (turn on wifi), false (turn off wifi)
 * @param successCallback	The success callback (returns boolean: if true wifi is on if false wifi is off)
 * @param failureCallback	The error callback
 */
WifiListener.prototype.set_wifi_state = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'set_wifi_state',								
			[data]);
};

/**
 * returns boolean if project is enabled for notifications
 * 
 * @param data				"fid" - project id and will return if auto-or manual updating state
 * @param successCallback	The success callback (returns boolean: if true notifications are enabled for this project)
 * @param failureCallback	The error callback
 */
WifiListener.prototype.getShouldAutoUpdateProjectEvents = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'getShouldAutoUpdateProjectEvents',								
			[data]);
};

/**
 * sets the project notify state for a given 
 * 
 * @param data				"fid" - project id, "shouldautoupdate" - boolean to autoupdate or notify
 * @param successCallback	The success callback (returns boolean: if true notifications are enabled for this project)
 * @param failureCallback	The error callback
 */
WifiListener.prototype.setShouldAutoUpdateProjectEvents = function(data, successCallback, failureCallback) {
	return PhoneGap.exec(
			successCallback,			 
			failureCallback,						
			'WifiListener',				
			'setShouldAutoUpdateProjectEvents',								
			[data]);
};


PhoneGap.addConstructor(function() {
	PhoneGap.addPlugin("WifiListener", new WifiListener());
});