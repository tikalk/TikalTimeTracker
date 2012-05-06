//
//  Project.h
//  TikalTimeTracker
//
//  Created by Dov Goldberg on 5/6/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Event;

@interface Project : NSManagedObject

@property (nonatomic, retain) NSString * fid;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * latitude;
@property (nonatomic, retain) NSNumber * longitude;
@property (nonatomic, retain) NSNumber * currentlyHere;
@property (nonatomic, retain) NSString * address;
@property (nonatomic, retain) NSNumber * shouldAutoUpdate;
@property (nonatomic, retain) NSSet *event;
@end

@interface Project (CoreDataGeneratedAccessors)

- (void)addEventObject:(Event *)value;
- (void)removeEventObject:(Event *)value;
- (void)addEvent:(NSSet *)values;
- (void)removeEvent:(NSSet *)values;

@end
