//
//  xRTCEngineObjC.h
//  xRTCEngineCore-iOS
//
//  Created by sunhui on 2017/10/8.
//  Copyright © 2017年 sunhui. All rights reserved.
//

#ifndef __xRTCEngineObjC_H__
#define __xRTCEngineObjC_H__

#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>
#include "xRTCEngine.h"
#include <stdint.h>

typedef NS_ENUM(uint32_t, xRTCChannelProfile) {
    xRTC_ChannelProfile_Communication = 0,
    xRTC_ChannelProfile_LiveBroadcasting = 1,
    xRTC_ChannelProfile_Game = 2 ,
};

/*
 #define RENDER_TYPE_FULL        ( 0 )       // 拉升或者缩放适应尺寸
 #define RENDER_TYPE_ADAPTIVE    ( 1 )       // 自适应 如果尺寸比例有差异 上下、左右加黑边
 #define RENDER_TYPE_CROP        ( 2 )       // 裁剪原始图片以适应显示view
 
 //
 // 根据情况自适应选择 RENDER_TYPE_FULL/RENDER_TYPE_ADAPTIVE/RENDER_TYPE_CROP
 //
 //
 // 如果都是显示尺寸 w1>h1／h2>w2，选择 RENDER_TYPE_CROP
 // 如果 w>h / h>w 则选择 RENDER_TYPE_ADAPTIVE
 // 该模式，内部不使用，内部会计算w/h选择上面3种模式
 //
 //
 #define RENDER_TYPE_AUTO        ( 3 )
 */
typedef NS_ENUM(uint32_t, xRTCRenderMode){
    xRTC_Render_Full = 1,
    xRTC_Render_Adaptive = 2,
    xRTC_Render_Crop = 3,
    xRTC_Render_Auto = 4 ,
};

typedef NS_ENUM(uint32_t, xRTCMirrorMode){
    MirrorMode_NO  = 0,
    MirrorMode_OnlyLocal = 1, //default front camera use mirror
    MirrorMode_All = 2,
};

__attribute__((visibility("default"))) @interface xRTCVideoCanvas : NSObject

@property (strong, nonatomic) UIView* view;
@property (assign, nonatomic) xRTCRenderMode renderMode; // the render mode of view: hidden, fit and adaptive
@property (assign, nonatomic) uint64_t uid; // the user id of view
@property (assign, nonatomic) xRTCMirrorMode mirrorMode;
@end


@class xRTCEngineKit;
@protocol xRTCEngineDelegate <NSObject>
@optional

- (void)rtcEngine:(xRTCEngineKit *)
    engine didOccurWarning:(int32_t)warningCode;

- (void)rtcEngine:(xRTCEngineKit *)engine
    didOccurError:(int32_t)errorCode;

- (void)rtcEngine:(xRTCEngineKit *)engine
    reportAudioVolumeIndicationOfSpeakers:(NSArray*)speakers totalVolume:(int32_t)totalVolume;

- (void)rtcEngine:(xRTCEngineKit *)engine
            firstLocalVideoFrameWithSize:(CGSize)size elapsed:(int32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            firstRemoteVideoDecodedOfUid:(uint64_t)uid
            size:(CGSize)size
            elapsed:(uint32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            firstRemoteVideoFrameOfUid:(uint64_t)uid
            size:(CGSize)size
            elapsed:(uint32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didJoinedOfUid:(uint64_t)uid
            elapsed:(uint32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didOfflineOfUid:(uint64_t)uid
           reason:(int32_t)reason;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didAudioMuted:(BOOL)muted
            byUid:(uint64_t)uid;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didVideoMuted:(BOOL)muted byUid:(uint64_t)uid;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didVideoEnabled:(BOOL)enabled
            byUid:(uint64_t)uid;

- (void)rtcEngine:(xRTCEngineKit *)engine
            localVideoStats:(int32_t)stats;

- (void)rtcEngine:(xRTCEngineKit *)engine
            remoteVideoStats:(int32_t)stats;

- (void)rtcEngineMediaEngineDidLoaded:(xRTCEngineKit *)engine;

- (void)rtcEngineMediaEngineDidStartCall:(xRTCEngineKit *)engine;

- (void)rtcEngineMediaEngineDidAudioMixingFinish:(xRTCEngineKit *)engine;

- (void)rtcEngineCameraDidReady:(xRTCEngineKit *)engine;

- (void)rtcEngineVideoDidStop:(xRTCEngineKit *)engine;

- (void)rtcEngineConnectionDidInterrupted:(xRTCEngineKit *)engine;

- (void)rtcEngineConnectionDidLost:(xRTCEngineKit *)engine;

- (void)rtcEngine:(xRTCEngineKit *)engine
    firstLocalAudioFrame:(uint32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            firstRemoteAudioFrameOfUid:(uint64_t)uid
                               elapsed:(uint32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
             didJoinChannel:(NSString*)channel
                    withUid:(uint64_t)uid
                    elapsed:(uint32_t)elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didRejoinChannel:(NSString*)channel
                     withUid:(uint64_t)uid
                     elapsed:(uint32_t) elapsed;

- (void)rtcEngine:(xRTCEngineKit *)engine
            reportRtcStats:(uint32_t)stats;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didApiCallExecute:(NSString*)api
                        error:(int32_t)error;

- (void)rtcEngine:(xRTCEngineKit *)engine
            didRefreshRecordingServiceStatus:(uint32_t)status;

- (void)rtcEngine:(xRTCEngineKit *)engine
            receiveStreamMessageFromUid:(uint64_t)uid
                                streamId:(int32_t)streamId
                                data:(NSData*)data;

- (void)rtcEngineRequestChannelKey:(xRTCEngineKit *)engine;

@end


__attribute__((visibility("default"))) @interface xRTCEngineKit : NSObject


+ (instancetype)sharedEngineWithAppId:(NSString*)appId
                             delegate:(id<xRTCEngineDelegate>)delegate;

+ (void)destroy ;

- (int)setVideoProfile:(int)profile
    swapWidthAndHeight:(BOOL)bSwap ;

- (int)enableVideo ;

- (int)enableAudio ;

- (int)setChannelProfile:(xRTCChannelProfile)profile ;

- (int)joinChannelByKey:(NSString *)channelKey
            channelName:(NSString *)channelName
                   info:(NSString *)info
                    uid:(uint64_t)uid ;

- (int)setupRemoteVideo:(xRTCVideoCanvas*)remoteCanvas ;

- (int)setupLocalVideo:(xRTCVideoCanvas*)localCanvas ;

// 不发送本地视频流
- (int)muteLocalVideoStream:(BOOL)bMute;

// 不显示远程视频流
- (int)muteRemoteVideoStream:(uint64_t)uid
                        mute:(BOOL)bMute;

// 不发送本地音频流
- (int)muteLocalAudioStream:(BOOL)bMute ;

// 不显示远程音频流
- (int)muteRemoteAudioStream:(uint64_t)uid
                        mute:(BOOL)bMute ;

- (int)leaveChannel:(int)reson ;

- (int)stopPreview ;

- (int)removeRemoteVideo:(uint64_t)uid ;

- (int)setAudioExtendDevice:(void *)pAudioDevice ;

- (int)switchCamera ;

- (AVCaptureSession *)getAVCaptureSession ;

- (void) focusWithMode:(AVCaptureFocusMode)focusMode
        exposeWithMode:(AVCaptureExposureMode)exposureMode
         atDevicePoint:(CGPoint)point
monitorSubjectAreaChange:(BOOL)monitorSubjectAreaChange ;

@end



#endif /* xRTCEngineObjC_h */
