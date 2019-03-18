//
//  ViewController.m
//  TestAutoLayout
//
//  Created by zhangkai on 2019/3/15.
//  Copyright © 2019 zhangkai. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    UIView *yellow = [[UIView alloc] init];
    yellow.translatesAutoresizingMaskIntoConstraints = NO;
    yellow.backgroundColor = [UIColor yellowColor];
    [self.view addSubview:yellow];
    
    
    //类似android中的相对布局
    //左右:左边为正,右边为负
    //上下:上部为负，下部为正
    //添加约束
    CGFloat margin = 20;
    //距离self.view左边距离20
    [yellow.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor constant:margin].active = YES;
    //距离self.view右边距离20
    [yellow.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor constant:-margin].active = YES;
    //距离self.view的上部100
    [yellow.topAnchor constraintEqualToAnchor:self.view.topAnchor constant:100.0f].active = YES;
    //距离底部100像素
    [yellow.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-margin].active = YES;
    
    
    UIView *grayView = [[UIView alloc] init];
    grayView.backgroundColor = [UIColor lightGrayColor];
    [self.view addSubview:grayView];
    grayView.translatesAutoresizingMaskIntoConstraints = NO;
    
    //这个公式是理解约束的核心
    //grayView.left = 1.0 * self.view.NSLayoutAttributeLeft + 50;
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:grayView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:50];
    
    //grayView.top = 1.0 * self.view.NSLayoutAttributeTop + 100;
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:grayView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:100];
    
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:grayView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:100];
    
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:grayView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0f constant:100];
    
    //为何必须是self.view先添加，因为self.view是已知点，所以要先添加
    [self.view addConstraints:@[left,top]];
    [grayView addConstraints:@[width,height]];
    
//    //关于AutoLayout的理论,这里介绍的比较详细:
//    //https://www.jianshu.com/p/3a872a0bfe11

    

    
    
    //官方文档翻译:
    //https://www.jianshu.com/p/47091aeafc34
    
    
}


@end
