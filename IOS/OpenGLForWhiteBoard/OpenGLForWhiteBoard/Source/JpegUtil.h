//========================================================
/**
*  @file      JpegUtil.h
*
*  项目描述：  jpeg图片解析工具
*  文件描述:
*  适用平台：
*
*  作者：     赖忠安
*  电子邮件:  zhonganlai@gmail.com
*  创建日期： 20018-12-25
*  修改日期： 20018-12-25
*/
//========================================================
#pragma once
#include <stdio.h>
#include <stdlib.h>
#include <string>

#ifdef __cplusplus
extern "C" {
#endif

    extern unsigned char* read_jpeg_file(const char* filename, int& width, int& height);

    extern int write_jpeg_file(const char* filename, unsigned char* image_buffer, int quality, int image_height, int image_width);

#ifdef __cplusplus
}
#endif