//
//  ViewController.m
//  captureDevicePointOfInterestForPoint
//
//  Created by zhangkai on 2019/3/25.
//  Copyright © 2019 zhangkai. All rights reserved.
//

#import "ViewController.h"
#import "AVCameraPreview.h"

@interface ViewController ()

@property(nonatomic,strong) IBOutlet AVCameraPreview *preview;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    //添加手势识别
    UITapGestureRecognizer * ges = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(focusAndExposeTap:)];
    [self.preview addGestureRecognizer:ges];
    
    UIInterfaceOrientation statusBarOrientation = [UIApplication sharedApplication].statusBarOrientation;
    AVCaptureVideoOrientation initialVideoOrientation = AVCaptureVideoOrientationPortrait;
    if (statusBarOrientation != UIInterfaceOrientationUnknown) {
        initialVideoOrientation = (AVCaptureVideoOrientation)statusBarOrientation;
    }
    self.preview.videoPreviewLayer.connection.videoOrientation = initialVideoOrientation;

}

- (UIInterfaceOrientationMask)supportedInterfaceOrientations
{
    return UIInterfaceOrientationMaskAll;
}

- (void) viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator
{
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
    
    UIDeviceOrientation deviceOrientation = [UIDevice currentDevice].orientation;
    
    if (UIDeviceOrientationIsPortrait(deviceOrientation) || UIDeviceOrientationIsLandscape(deviceOrientation)) {
        self.preview.videoPreviewLayer.connection.videoOrientation = (AVCaptureVideoOrientation)deviceOrientation;
    }
}

//问题? 为什么这里的坐标没有转为摄像机后面的坐标，肯定是因为某种原因导致的?
//
- (IBAction) focusAndExposeTap:(UIGestureRecognizer*)gestureRecognizer
{
    CGPoint viewPoint = [gestureRecognizer locationInView:gestureRecognizer.view];
    CGPoint devicePoint = [self.preview.videoPreviewLayer captureDevicePointOfInterestForPoint:viewPoint];
    
    NSLog(@"device point:(%f %f)" , devicePoint.x ,devicePoint.y);
    
}

@end
