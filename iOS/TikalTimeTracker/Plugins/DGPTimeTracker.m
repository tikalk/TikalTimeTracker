//
//  DGPTimeTracker.m
//  TikalTimeTracker
//
//  Created by Dov Goldberg on 5/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "DGPTimeTracker.h"
#import "AppDelegate.h"
#import "Project.h"
#import "Event.h"
#import "NSDate+Utils.h"

@implementation DGPTimeTracker

- (void)addProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    NSUInteger argc = [arguments count];
    NSString* callbackId = (argc > 0)? [arguments objectAtIndex:0] : @"INVALID";
    
    [self saveGeofenceCallbackId:callbackId];
    
    // Add Project To Coredata
    Project *project = [[Project alloc] initWithEntity:[NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext]  insertIntoManagedObjectContext:self.managedObjectContext];
    NSString *fid = [options objectForKey:KEY_REGION_ID];
    NSString *latitude = [options objectForKey:KEY_PROJECT_LAT];
    NSString *longitude = [options objectForKey:KEY_PROJECT_LNG];
    NSString *projectName = [options objectForKey:KEY_PROJECT_NAME];
    NSString *projectAddress = [options objectForKey:KEY_PROJECT_ADDRESS];
    
    project.fid = fid;
    project.name = projectName;
    project.address = projectAddress;
    project.latitude = [NSNumber numberWithDouble:[latitude doubleValue]];
    project.longitude = [NSNumber numberWithDouble:[longitude doubleValue]];
    project.currentlyHere = [NSNumber numberWithBool:NO];
    project.shouldAutoUpdate = [NSNumber numberWithBool:1];
    
    [self.managedObjectContext insertObject:project];
    NSError *dberror;
    if (![self.managedObjectContext save:&dberror]) {
        NSLog(@"Error deleting - error:%@",dberror);
    }
    
    // Add Monitor Region
    
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            [self returnLocationError:PERMISSIONDENIED withMessage: nil];
			return;
		}
    }
    
    if (![self isAuthorized]) 
    {
        NSString* message = nil;
        BOOL authStatusAvailable = [CLLocationManager respondsToSelector:@selector(authorizationStatus)]; // iOS 4.2+
        if (authStatusAvailable) {
            NSUInteger code = [CLLocationManager authorizationStatus];
            if (code == kCLAuthorizationStatusNotDetermined) {
                // could return POSITION_UNAVAILABLE but need to coordinate with other platforms
                message = @"User undecided on application's use of location services";
            } else if (code == kCLAuthorizationStatusRestricted) {
                message = @"application use of location services is restricted";
            }
        }
        //PERMISSIONDENIED is only PositionError that makes sense when authorization denied
        [self returnLocationError:PERMISSIONDENIED withMessage: message];
        
        return;
    } 
    
    if (![self isRegionMonitoringAvailable])
	{
		[self returnLocationError:REGIONMONITORINGUNAVAILABLE withMessage: @"Region monitoring is unavailable"];
        return;
    }
    
    if (![self isRegionMonitoringEnabled])
	{
		[self returnLocationError:REGIONMONITORINGPERMISSIONDENIED withMessage: @"User has restricted the use of region monitoring"];
        return;
    }
    
    [super addRegion:options];
    
    [self returnRegionSuccess];    
}

- (void)removeProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    NSUInteger argc = [arguments count];
    NSString* callbackId = (argc > 0)? [arguments objectAtIndex:0] : @"INVALID";
    
    [self saveGeofenceCallbackId:callbackId];
    
    // Add Project To Coredata
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSString *fid = [options objectForKey:KEY_REGION_ID];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    for (NSManagedObject *mo in items) {
        [self.managedObjectContext deleteObject:mo];
        NSLog(@"%@ object deleted",@"ChatResultItem");
    }
    
    NSError *dberror1;
    if (![self.managedObjectContext save:&dberror1]) {
        NSLog(@"Error deleting - error:%@",dberror1);
    }
    
    // Add Monitor Region
    
    if (![self isLocationServicesEnabled])
	{
		BOOL forcePrompt = NO;
		if (!forcePrompt)
		{
            [self returnLocationError:PERMISSIONDENIED withMessage: nil];
			return;
		}
    }
    
    if (![self isAuthorized]) 
    {
        NSString* message = nil;
        BOOL authStatusAvailable = [CLLocationManager respondsToSelector:@selector(authorizationStatus)]; // iOS 4.2+
        if (authStatusAvailable) {
            NSUInteger code = [CLLocationManager authorizationStatus];
            if (code == kCLAuthorizationStatusNotDetermined) {
                // could return POSITION_UNAVAILABLE but need to coordinate with other platforms
                message = @"User undecided on application's use of location services";
            } else if (code == kCLAuthorizationStatusRestricted) {
                message = @"application use of location services is restricted";
            }
        }
        //PERMISSIONDENIED is only PositionError that makes sense when authorization denied
        [self returnLocationError:PERMISSIONDENIED withMessage: message];
        
        return;
    } 
    
    if (![self isRegionMonitoringAvailable])
	{
		[self returnLocationError:REGIONMONITORINGUNAVAILABLE withMessage: @"Region monitoring is unavailable"];
        return;
    }
    
    if (![self isRegionMonitoringEnabled])
	{
		[self returnLocationError:REGIONMONITORINGPERMISSIONDENIED withMessage: @"User has restricted the use of region monitoring"];
        return;
    }
    
    [super removeRegion:options];
    
    [self returnRegionSuccess]; 
    
}


- (void)setShouldAutoUpdateProjectEvents:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    NSUInteger argc = [arguments count];
    NSString* callbackId = (argc > 0)? [arguments objectAtIndex:0] : @"INVALID";
    
    [self saveGeofenceCallbackId:callbackId];
    
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSString *fid = [options objectForKey:KEY_REGION_ID];
    BOOL shouldAutoUpdate =  [[options objectForKey:KEY_PROJECT_SHOULD_AUTO_UPDATE] boolValue];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    Project *project = [items objectAtIndex:0];
    project.shouldAutoUpdate = [NSNumber numberWithBool:shouldAutoUpdate];
    
    NSError *dberror1;
    if (![self.managedObjectContext save:&dberror1]) {
        NSLog(@"Error deleting - error:%@",dberror1);
    }
    
    if (shouldAutoUpdate) {
        [self addRegion:options];
    } else {
        [self removeRegion:options];
    }
    
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    [super writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)getShouldAutoUpdateProjectEvents:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    NSUInteger argc = [arguments count];
    NSString* callbackId = (argc > 0)? [arguments objectAtIndex:0] : @"INVALID";
    
    [self saveGeofenceCallbackId:callbackId];
    
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSString *fid = [options objectForKey:KEY_REGION_ID];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    if (1) {
        Project *project = [items objectAtIndex:0];
        NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
        [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
        [posError setObject: project.shouldAutoUpdate forKey: KEY_PROJECT_SHOULD_AUTO_UPDATE];
        [posError setObject: project.currentlyHere forKey: KEY_PROJECT_CURRENTLY_HERE];
        CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
        [super writeJavascript:[result toSuccessCallbackString:callbackId]];
        
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
        [fetchRequest setEntity:entity];
        
        NSError *dberror;
        NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
        [fetchRequest release];
        
        for (Project *project in items) {
            for (Event *event in project.event) {
                NSLog(@"Project: %@ : Checkin: %@, Checkout: %@", ((Project*)event.project).name, event.checkin, event.checkout);
            }
        }
    }   
}


- (void)checkinToProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    NSUInteger argc = [arguments count];
    NSString* callbackId = (argc > 0)? [arguments objectAtIndex:0] : @"INVALID";
    
    [self saveGeofenceCallbackId:callbackId];
    
    [self doCheckIn:[options objectForKey:KEY_REGION_ID]];
    
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    [super writeJavascript:[result toSuccessCallbackString:callbackId]];
}

- (void)checkoutOfProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options {
    NSUInteger argc = [arguments count];
    NSString* callbackId = (argc > 0)? [arguments objectAtIndex:0] : @"INVALID";
    
    [self saveGeofenceCallbackId:callbackId];
    
    [self doCheckOut:[options objectForKey:KEY_REGION_ID]];
    
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: CDVCommandStatus_OK] forKey:@"code"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:posError];
    [super writeJavascript:[result toSuccessCallbackString:callbackId]];
}

-(void) doCheckIn:(NSString *)fid {   
    
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    // Now check out of any current projects
    [self forceCheckoutFromAllProjects];
    
    Project *project = [items objectAtIndex:0];
    
    project.currentlyHere = [NSNumber numberWithBool:YES];
    
    // Create New Event Item
    Event *event = [[Event alloc] initWithEntity:[NSEntityDescription entityForName:@"Event" inManagedObjectContext:self.managedObjectContext]  insertIntoManagedObjectContext:self.managedObjectContext];
    event.checkin = [[NSDate date] toLocalTime];
    event.checkout = event.checkin;
    [project addEventObject:event];
    
    NSError *dberror1;
    if (![self.managedObjectContext save:&dberror1]) {
        NSLog(@"Error deleting - error:%@",dberror1);
    }
    
    //[self postLocalNotificationWithMessage:[NSString stringWithFormat:NSLocalizedString(@"You have checked in to %@.", nil), project.name]];
}

- (void) forceCheckoutFromAllProjects {
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
    
    for (Project *project in items) {
        if (project.currentlyHere == [NSNumber numberWithBool:YES]) {
            [self doCheckOut:project.fid];
        }
    }
}

-(void) doCheckOut:(NSString *)fid {
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    Project *project = [items objectAtIndex:0];
    
    // Create New Event Item
    NSSet *events = project.event;
    
    for (Event *event in events.allObjects) {
        if ([event.checkout isEqualToDate:event.checkin]) {
            event.checkout = [[NSDate date] toLocalTime];
            project.currentlyHere = [NSNumber numberWithBool:NO];
        }
    }
    
    NSError *dberror1;
    if (![self.managedObjectContext save:&dberror1]) {
        NSLog(@"Error deleting - error:%@",dberror1);
    }
    
    //[self postLocalNotificationWithMessage:[NSString stringWithFormat:NSLocalizedString(@"You have checked out of %@.", nil), project.name]];
}

- (void)returnTimeTrackerError: (NSUInteger) errorCode withMessage: (NSString*) message
{
    NSMutableDictionary* posError = [NSMutableDictionary dictionaryWithCapacity:2];
    [posError setObject: [NSNumber numberWithInt: errorCode] forKey:@"code"];
    [posError setObject: message ? message : @"" forKey: @"message"];
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:posError];
    NSString *callbackId = [self.locationData.locationCallbacks dequeue];
    if (callbackId) {
        [super writeJavascript:[result toErrorCallbackString:callbackId]];
    }
}

-(NSManagedObjectContext *) managedObjectContext {
    id ad = [super appDelegate];
    return ((AppDelegate *)ad).managedObjectContext;
}

- (void) locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region
{
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSString *fid = region.identifier;
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    Project *project = [items objectAtIndex:0];
    
    if (project.shouldAutoUpdate) {
        [self doCheckIn:project.fid];
    } else {
        [self postLocalNotificationWithMessage:[NSString stringWithFormat:NSLocalizedString(@"You have arrived at %@.", nil), project.name]];
    }    
}

- (void) locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region
{
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSString *fid = region.identifier;
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    Project *project = [items objectAtIndex:0];
    
    if (project.shouldAutoUpdate) {
        [self doCheckOut:project.fid];
    } else {
       [self postLocalNotificationWithMessage:[NSString stringWithFormat:NSLocalizedString(@"You have left %@.", nil), project.name]]; 
    }    
}

- (void)locationManager:(CLLocationManager *)manager monitoringDidFailForRegion:(CLRegion *)region withError:(NSError *)error {
    NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"Project" inManagedObjectContext:self.managedObjectContext];
    [fetchRequest setEntity:entity];
    
    NSString *fid = region.identifier;
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"fid=%@", fid];
	[fetchRequest  setPredicate:predicate];
	
    NSError *dberror;
    NSArray *items = [self.managedObjectContext executeFetchRequest:fetchRequest error:&dberror];
    [fetchRequest release];
	
    if (items.count == 0) {
        [self returnTimeTrackerError:PROJECTNOTFOUND withMessage: @"Project was not found"];
        return;
    }
    
    [self.locationManager stopMonitoringForRegion:region];
    Project *project = [items objectAtIndex:0];
    project.shouldAutoUpdate = NO; 
    NSError *dberror1;
    if (![self.managedObjectContext save:&dberror1]) {
        NSLog(@"Error deleting - error:%@",dberror1);
    }
    
    [self postLocalNotificationWithMessage:[NSString stringWithFormat:NSLocalizedString(@"Failed to monitor: %@ with error: %@.", nil), project.name, error.description]];
}

-(void) postLocalNotificationWithMessage:(NSString *)message {
    UILocalNotification *localNotif = [[UILocalNotification alloc] init];
    if (localNotif) {
        localNotif.alertBody = message;
        //localNotif.alertAction = NSLocalizedString(@"Read Message", nil);
        //localNotif.soundName = @"alarmsound.caf";
        //localNotif.applicationIconBadgeNumber = 1;
        [[UIApplication sharedApplication] presentLocalNotificationNow:localNotif];
        [localNotif release];
    }
}

@end
