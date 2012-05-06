//
//  Event.h
//  TikalTimeTracker
//
//  Created by Dov Goldberg on 5/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Event : NSManagedObject

@property (nonatomic, retain) NSDate * checkin;
@property (nonatomic, retain) NSDate * checkout;
@property (nonatomic, retain) NSManagedObject *project;

@end
