#include "JpegUtil.h"
#include <jpeglib.h>
#include <setjmp.h>
#include <sys/time.h>

struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr *my_error_ptr;

static int get_timer_now() {
    struct timeval now;
    gettimeofday(&now, NULL);
    return (now.tv_sec * 1000 + now.tv_usec / 1000);
}

void my_error_exit(j_common_ptr cinfo) {
    /* cinfo->err really points to a my_error_mgr struct, so coerce pointer */
    my_error_ptr myerr = (my_error_ptr) cinfo->err;
    /* Always display the message. */
    /* We could postpone this until after returning, if we chose. */
    (*cinfo->err->output_message)(cinfo);
    /* Return control to the setjmp point */
    longjmp(myerr->setjmp_buffer, 1);
}

unsigned char *read_jpeg_file(const char *filename, int &width, int &height) {
    struct jpeg_decompress_struct cinfo;
    struct my_error_mgr jerr;

    FILE *infile;
    JSAMPARRAY buffer;
    int row_stride;

    if (NULL == (infile = fopen(filename, "rb"))) {
        //LOGE("can't open %s", filename);
        return NULL;
    }

    cinfo.err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = my_error_exit;

    if (setjmp(jerr.setjmp_buffer)) {
        jpeg_destroy_decompress(&cinfo);
        fclose(infile);
        return NULL;
    }

    /*初始化*/
    jpeg_create_decompress(&cinfo);

    /*指定输入文件*/
    jpeg_stdio_src(&cinfo, infile);

    /*读取文件信息*/
    jpeg_read_header(&cinfo, TRUE);

    /*这里是设置缩放比列，这里设置1/4 表示原图1920*1080会缩放到480*270,若不设置则按原图大小尺寸*/
    //cinfo.scale_num = 1;
    //cinfo.scale_denom = 4;

    /*解压*/
    jpeg_start_decompress(&cinfo);
    row_stride = cinfo.output_height * cinfo.output_width * cinfo.output_components;

    /*输出图像信息*/
    //printf("output_width = %d\n", cinfo.output_width);
    //printf("output_height = %d\n", cinfo.output_height);
    //printf("output_components = %d\n", cinfo.output_components);
    width = cinfo.output_width;
    height = cinfo.output_height;

    /*申请内存用于存储解压数据*/
    unsigned char *data = (unsigned char *) malloc(sizeof(unsigned char) * row_stride);

    /*逐行扫描获取解压信息*/
    JSAMPROW row_pointer[1];
    unsigned long location = 0;
    while (cinfo.output_scanline < cinfo.output_height) {
        row_pointer[0] = &data[(cinfo.output_height - cinfo.output_scanline - 1) *
                               cinfo.output_width * cinfo.output_components];
        jpeg_read_scanlines(&cinfo, row_pointer, 1);
    }

    /*完成解压*/
    jpeg_finish_decompress(&cinfo);

    /*销毁解压cinfo信息*/
    jpeg_destroy_decompress(&cinfo);

    fclose(infile);

    return data;
}

int write_jpeg_file(const char *filename, unsigned char *image_buffer, int quality, int image_height, int image_width) {
    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;
    FILE *outfile;
    JSAMPROW row_pointer[1];
    int row_stride;

    cinfo.err = jpeg_std_error(&jerr);

    /*压缩初始化*/
    jpeg_create_compress(&cinfo);

    /*创建并打开输出的图片文件*/
    if ((outfile = fopen(filename, "wb")) == NULL) {
        fprintf(stderr, "can't open %s\n", filename);
        return -1;
    }

    jpeg_stdio_dest(&cinfo, outfile);

    /*设置压缩各项图片参数*/
    cinfo.image_width = image_width;
    cinfo.image_height = image_height;
    cinfo.input_components = 3;
    cinfo.in_color_space = JCS_RGB;
    jpeg_set_defaults(&cinfo);

    /*设置压缩质量*/
    jpeg_set_quality(&cinfo, quality, TRUE);

    /*开始压缩*/
    jpeg_start_compress(&cinfo, TRUE);

    /*逐行扫描压缩写入文件*/
    row_stride = image_width * 3;
    while (cinfo.next_scanline < cinfo.image_height) {
        row_pointer[0] = &image_buffer[(cinfo.image_height - cinfo.next_scanline) * row_stride];
        jpeg_write_scanlines(&cinfo, row_pointer, 1);
    }

    /*完成压缩*/
    jpeg_finish_compress(&cinfo);
    jpeg_destroy_compress(&cinfo);
    fclose(outfile);

    /*释放存储的解压图像内容*/
    if (NULL != image_buffer) {
        free(image_buffer);
        image_buffer = NULL;
    }

    return 0;
}
