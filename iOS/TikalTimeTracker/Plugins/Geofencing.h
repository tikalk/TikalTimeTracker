//
//  Geofencing.h
//  TikalTimeTracker
//  Sections of this code adapted from Apache Cordova
//
//  Created by Dov Goldberg on 5/3/12.
//  Copyright (c) 2012 Ogonium. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

#ifdef CORDOVA_FRAMEWORK
#import <Cordova/CDVPlugin.h>
#else
#import "CDVPlugin.h"
#endif

enum DGLocationStatus {
    PERMISSIONDENIED = 1,
    POSITIONUNAVAILABLE,
    TIMEOUT
};
typedef NSUInteger DGLocationStatus;

// simple ojbect to keep track of location information
@interface DGLocationData : NSObject

@property (nonatomic, assign) DGLocationStatus locationStatus;
@property (nonatomic, retain) CLLocation* locationInfo;
@property (nonatomic, retain) NSMutableArray* locationCallbacks;

@end

@interface Geofencing : CDVPlugin <CLLocationManagerDelegate>

@property (nonatomic, retain) CLLocationManager *locationManager;
@property (nonatomic, retain) DGLocationData* locationData;

- (BOOL) isLocationServicesEnabled;
- (BOOL) isAuthorized;
- (void) saveGeofenceCallbackId:(NSString *) callbackId;

- (void) returnLocationError: (NSUInteger) errorCode withMessage: (NSString*) message;
- (void) returnRegionSuccess;

#pragma mark Plugin Functions
- (void)addRegion:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

@end
