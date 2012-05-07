//
//  NSDate+Utils.m
//  TikalTimeTracker
//
//  Created by Dov Goldberg on 5/7/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "NSDate+Utils.h"

@implementation NSDate (Utils)

-(NSDate *) toLocalTime
{
    NSTimeZone *tz = [NSTimeZone defaultTimeZone];
    NSInteger seconds = [tz secondsFromGMTForDate: self];
    return [NSDate dateWithTimeInterval: seconds sinceDate: self];
}

-(NSDate *) toGlobalTime
{
    NSTimeZone *tz = [NSTimeZone defaultTimeZone];
    NSInteger seconds = -[tz secondsFromGMTForDate: self];
    return [NSDate dateWithTimeInterval: seconds sinceDate: self];
}

@end
