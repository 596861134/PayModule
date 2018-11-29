//
//  WechatManager.h
//  PayModule
//
//  Created by fanxianchao on 2018/11/29.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "WXApi.h"

@interface WechatManager : NSObject <WXApiDelegate>
+ (instancetype)shared;
@end
