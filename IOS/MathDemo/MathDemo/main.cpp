//
//  main.cpp
//  MathDemo
//
//  Created by zhangkai on 2019/3/26.
//  Copyright © 2019 zhangkai. All rights reserved.
//

#include <iostream>
#include <math.h>

void show()
{
    printf("this is a show \n");
}

int main(int argc, const char * argv[]) {
    // insert code here...
    std::cout << "Hello, World!\n";
    
//    float av = 2 * atanf(1.0f / 1.5f) * 180 / 3.1415926;
//    printf("av:%f \n",av);
    
    int pos = 1023;
    for(pos = 1023 + 1 ; pos  <= 1024 ; pos++ , show() )
    {
        printf("fuck....\n");
    }
    printf("pos:%d \n" , pos);
    
    
    //for (起始值; 条件 ; 执行代码 ;)
    //{ 代码块; }
    
    //条件满足，运行到代码块，代码块执行玩了之后，再走执行代码.
    
    return 0;
}
