//
//  ViewController.m
//  OpenGLForWhiteBoard
//
//  Created by zhangkai on 2019/4/3.
//  Copyright © 2019 zhangkai. All rights reserved.
//


#import "ViewController.h"

#include "JpegUtil.h"
#include "esUtil.h"
#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/ext.hpp"
#include "glm/gtc/type_ptr.hpp"

#include <list>
using namespace std;

typedef std::list<CGPoint> shapelist_t;

void CheckGlError(const char* op)
{
    for (GLint error = glGetError(); error; error = glGetError())
    {
        printf("after %s() glError (0x%x)\n", op, error);
    }
}

@interface ViewController ()
{
    shapelist_t m_points;
    
    GLuint programObject;
    GLuint attrPos;//形状
    GLuint attrUv;//纹理坐标
    GLuint mvpMatrixSam; //正交投影矩阵
    GLuint texSam;//纹理采样
    
    GLuint texId;//背景纹理单元
    
    glm::mat4 mvp;
    
    GLuint _drawableWidth;
    GLuint _drawableHeight;
}

@property (strong, nonatomic) EAGLContext *context;

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.context = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
    
    GLKView *glView = (GLKView*)self.view;
    glView.context = self.context;
    glView.drawableDepthFormat = GLKViewDrawableDepthFormat24;
    
    //添加手势滑动事件
    UIPanGestureRecognizer *panRecognizer = [[UIPanGestureRecognizer alloc]
                                             initWithTarget:self
                                             action:@selector(pandRecognizer:)];
    [self.view addGestureRecognizer:panRecognizer];
    
    //save test image -- for test
    NSString* resPath = [[NSBundle mainBundle] pathForResource:@"tx" ofType:@"jpg"];
    NSURL *url = [[NSURL alloc] initFileURLWithPath:resPath];
    NSData* fishdemoData = [NSData dataWithContentsOfURL:url];
    
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* documentDirectory = [paths objectAtIndex:0];
    NSString* imgTestPath = [documentDirectory stringByAppendingString:@"/tx.jpg"];
    [fishdemoData writeToFile:imgTestPath atomically:YES];
    
}


- (void) compileShader
{
    const char *vertShader =
    
    "precision highp float;                                         \n"
    "attribute vec3 a_Position;             \n"
    "attribute vec2 a_TexCoord;             \n"
    
    "uniform mat4   u_MvpMatrix;            \n"
    "varying vec2   v_TexCoord;             \n"
    
    "void main()                            \n"
    "{                                      \n"
    "   v_TexCoord = a_TexCoord;            \n"
    "   gl_Position =  u_MvpMatrix * vec4(a_Position,1.0); \n"
    "}                                      \n"
    ;
    
    const char *fragmentShader =
    
    "precision highp float;                                         \n"
    "varying vec2    v_TexCoord;                                    \n"
    "uniform sampler2D  u_TextureOES;                               \n"
    "uniform sampler2D  u_TextureMap;                               \n"
    "void main()                                                    \n"
    "{                                                              \n"
    "   gl_FragColor = texture2D(u_TextureOES, v_TexCoord );         \n"
    "}                                                              \n"
    ;
    
    programObject = esLoadProgram(vertShader, fragmentShader);
    
    attrPos = glGetAttribLocation(programObject, "a_Position");
    attrUv  = glGetAttribLocation(programObject, "a_TexCoord");
    
    mvpMatrixSam = glGetUniformLocation(programObject, "u_MvpMatrix");
    texSam = glGetUniformLocation(programObject, "u_TextureOES");
    
    
    //build textures
    glGenTextures(1, &texId);
    glBindTexture(GL_TEXTURE_2D, texId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    
    
    NSArray* paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString* documentDirectory = [paths objectAtIndex:0];
    NSString* imgPath = [documentDirectory stringByAppendingString:@"/tx.jpg"];
    
//    cv::Mat src_img = cv::imread([imgPath UTF8String]);
//    int photoWidth = src_img.cols;
//    int photoHeight = src_img.rows;
    
    //instead of libjpeg
    int photoWidth;
    int photoHeight;
    unsigned char* pixelBuf = read_jpeg_file([imgPath UTF8String],photoWidth,photoHeight);
    
//    unsigned char* photoBytes = new unsigned char [sizeof(unsigned char) * photoWidth * photoHeight * 3];
//    memcpy(photoBytes, src_img.data, sizeof(unsigned char) * photoWidth * photoHeight * 3 * sizeof(unsigned char));
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, photoWidth, photoHeight, 0, GL_RGB, GL_UNSIGNED_BYTE, pixelBuf);
    CheckGlError("step2");
    
    free(pixelBuf);
    pixelBuf = nullptr;
    
    glBindTexture(GL_TEXTURE_2D, 0);
    
    //init
    mvp = glm::mat4(1.0f);
    
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_DEPTH_TEST);
    glDepthFunc(GL_LESS);
}

- (void) pandRecognizer:(id)sender {
    
    UIPanGestureRecognizer *panRecognizer = (UIPanGestureRecognizer *)sender;
    //    if (panRecognizer.numberOfTouches != 1 ) return;
    
    CGPoint point = [panRecognizer locationInView:self.view];
    m_points.push_back(point);
}

//1080*1920--是我的原生的窗口的宽高
- (void)drawFrame:(NSUInteger)drawableWidth height:(NSUInteger)drawableHeight
{
    glViewport(0, 0, 3840*5, 1920*5);
    
    glUseProgram(programObject);
    
    GLfloat vertices [5*6]=
    {
        1.0f , -1.0f ,0.0f , 1.0f ,0.0f , // bottom right
        1.0f,1.0f,0.0f , 1.0 , 1.0f , // top right
        -1.0f,-1.0f, 0.0f , 0.0f ,0.0f ,// bottom left
    
        1.0f,1.0f,0.0f , 1.0 , 1.0f , // top right
        -1.0f,1.0f,0.0f , 0.0f ,1.0f , // top left
        -1.0f,-1.0f, 0.0f , 0.0f ,0.0f ,// bottom left
    };
    
    glEnableVertexAttribArray(attrPos);
    glVertexAttribPointer ( attrPos,
                           3,
                           GL_FLOAT,
                           GL_FALSE,
                           5 * sizeof ( GLfloat ),
                           //sphere_vertics
                           vertices
                           );
    
    glEnableVertexAttribArray(attrUv);
    glVertexAttribPointer ( attrUv,
                           2,
                           GL_FLOAT,
                           GL_FALSE,
                           5 * sizeof ( GLfloat ),
                           //texcoords
                           &vertices[3]
                           );
    
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texId);
    glUniform1i(texSam, 0);
    glUniformMatrix4fv(mvpMatrixSam, 1, GL_FALSE, glm::value_ptr(mvp));
    glDrawArrays(GL_TRIANGLES,0,6);
}

- (void)glkView:(GLKView *)view drawInRect:(CGRect)rect
{
    //draw code here
    
    //default background color -- white color
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear ( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
    
    if( _drawableWidth != view.drawableWidth && _drawableHeight  != view.drawableHeight )
    {
        _drawableWidth = view.drawableWidth;
        _drawableHeight = view.drawableHeight;
        [self compileShader];
    }
    [self drawFrame:view.drawableWidth height:view.drawableHeight];
}

@end
