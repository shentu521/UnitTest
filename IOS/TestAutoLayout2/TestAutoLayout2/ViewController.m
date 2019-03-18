//
//  ViewController.m
//  TestAutoLayout2
//
//  Created by zhangkai on 2019/3/17.
//  Copyright © 2019 zhangkai. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    //Masonry
    
//    UIView *yellow = [[UIView alloc] init];
//    yellow.backgroundColor = [UIColor yellowColor];
//    [self.view addSubview:yellow];
//
//    CGFloat margin = 20;
    
    
    UIStackView *stackView = [[UIStackView alloc] init];
    stackView.backgroundColor = [UIColor redColor];
    stackView.layer.cornerRadius = 50;
    stackView.layer.masksToBounds = YES;
    
   
    
}


@end
