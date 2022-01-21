#include <jni.h>
#include <string>

extern "C" {
#include <libavcodec/version.h>
#include <libavcodec/avcodec.h>
#include <libavformat/version.h>
#include <libavutil/version.h>
#include <libavutil/avutil.h>
#include <libavfilter/version.h>
#include <libswresample/version.h>
#include <libswscale/version.h>

};



extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_jni_FFmpegCmd_test(JNIEnv *env, jclass clazz) {
//    return env->NewStringUTF(av_version_info());
    char strBuffer[1024 * 4] = {0};
    strcat(strBuffer, "libavcodec : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVCODEC_VERSION));
    strcat(strBuffer, "\nlibavformat : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFORMAT_VERSION));
    strcat(strBuffer, "\nlibavutil : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVUTIL_VERSION));
    strcat(strBuffer, "\nlibavfilter : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFILTER_VERSION));
    strcat(strBuffer, "\nlibswresample : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWRESAMPLE_VERSION));
    strcat(strBuffer, "\nlibswscale : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWSCALE_VERSION));
    strcat(strBuffer, "\navcodec_configure : \n");
    strcat(strBuffer, avcodec_configuration());
    strcat(strBuffer, "\navcodec_license : ");
//    strcat(strBuffer, avcodec_license());
//    LOGCATE("GetFFmpegVersion\n%s", strBuffer);
    return env->NewStringUTF(strBuffer);
}
