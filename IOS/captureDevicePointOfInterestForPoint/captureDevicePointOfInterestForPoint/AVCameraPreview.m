//
//  AVCameraPreview.m
//  captureDevicePointOfInterestForPoint
//
//  Created by zhangkai on 2019/3/25.
//  Copyright © 2019 zhangkai. All rights reserved.
//

#import "AVCameraPreview.h"

@implementation AVCameraPreview

+ (Class)layerClass
{
    return [AVCaptureVideoPreviewLayer class];
}

- (AVCaptureVideoPreviewLayer*) videoPreviewLayer
{
    return (AVCaptureVideoPreviewLayer *)self.layer;
}

@end
