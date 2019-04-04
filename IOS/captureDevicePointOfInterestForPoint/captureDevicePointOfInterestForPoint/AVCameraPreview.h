//
//  AVCameraPreview.h
//  captureDevicePointOfInterestForPoint
//
//  Created by zhangkai on 2019/3/25.
//  Copyright © 2019 zhangkai. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AVCameraPreview : UIView

@property (nonatomic, readonly) AVCaptureVideoPreviewLayer *videoPreviewLayer;

@end

NS_ASSUME_NONNULL_END
