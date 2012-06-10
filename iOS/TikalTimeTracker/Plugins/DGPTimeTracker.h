//
//  DGPTimeTracker.h
//  TikalTimeTracker
//
//  Created by Dov Goldberg on 5/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "Geofencing.h"
#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>

#define KEY_PROJECT_NAME                    @"projectname"
#define KEY_EVENT_CHECKIN                   @"checkin"
#define KEY_EVENT_CHECKOUT                  @"checkout"
#define KEY_PROJECT_NAME                    @"projectname"
#define KEY_PROJECT_ADDRESS                 @"projectaddress"
#define KEY_PROJECT_SHOULD_AUTO_UPDATE      @"shouldautoupdate"
#define KEY_PROJECT_CURRENTLY_HERE          @"currentlyhere"
#define KEY_YEAR                            @"year"
#define KEY_MONTH                           @"month"
#define KEY_EVENTS                          @"events"

enum DGPTimeTrackerStatusCode {
    PROJECTNOTFOUND = 20
};
typedef NSUInteger DGPTimeTrackerStatusCode;

@interface DGPTimeTracker : Geofencing <MFMailComposeViewControllerDelegate>

- (void)addProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)removeProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)setShouldAutoUpdateProjectEvents:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)getShouldAutoUpdateProjectEvents:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)checkinToProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)checkoutOfProject:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)retrieveProjectEventsForDate:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;
- (void)exportProjectsForDate:(NSMutableArray*)arguments withDict:(NSMutableDictionary*)options;

- (void)returnTimeTrackerError: (NSUInteger) errorCode withMessage: (NSString*) message;
- (void) doCheckIn:(NSString *)fid;
- (void) doCheckOut:(NSString *)fid;
- (void) postLocalNotificationWithMessage:(NSString *)message;
- (void) forceCheckoutFromAllProjects;
-(NSString *) formatDate:(NSDate *)date;
-(NSString *) dateMonthYear:(NSDate *)date;
-(NSString *) dateDay:(NSDate *)date;
-(NSString *) dateTime:(NSDate *)date;
// CoreData Functions
- (NSManagedObjectContext *) managedObjectContext;

@end
