//
// Created by x-eri on 2022/1/21.
//
#include <jni.h>
#include <string>

#include <opencv2/opencv.hpp>


using namespace cv;

double getMSSIM(const Mat& i1, const Mat& i2)
{
    const double C1 = 6.5025, C2 = 58.5225;
    /***************************** INITS **********************************/
    int d = CV_32F;

    Mat I1, I2;
    i1.convertTo(I1, d);           // cannot calculate on one byte large values
    i2.convertTo(I2, d);

    Mat I2_2 = I2.mul(I2);        // I2^2
    Mat I1_2 = I1.mul(I1);        // I1^2
    Mat I1_I2 = I1.mul(I2);        // I1 * I2

    /*************************** END INITS **********************************/

    Mat mu1, mu2;   // PRELIMINARY COMPUTING
    GaussianBlur(I1, mu1, Size(11, 11), 1.5);
    GaussianBlur(I2, mu2, Size(11, 11), 1.5);

    Mat mu1_2 = mu1.mul(mu1);
    Mat mu2_2 = mu2.mul(mu2);
    Mat mu1_mu2 = mu1.mul(mu2);

    Mat sigma1_2, sigma2_2, sigma12;

    GaussianBlur(I1_2, sigma1_2, Size(11, 11), 1.5);
    sigma1_2 -= mu1_2;

    GaussianBlur(I2_2, sigma2_2, Size(11, 11), 1.5);
    sigma2_2 -= mu2_2;

    GaussianBlur(I1_I2, sigma12, Size(11, 11), 1.5);
    sigma12 -= mu1_mu2;

    Mat t1, t2, t3;

    t1 = 2 * mu1_mu2 + C1;
    t2 = 2 * sigma12 + C2;
    t3 = t1.mul(t2);              // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

    t1 = mu1_2 + mu2_2 + C1;
    t2 = sigma1_2 + sigma2_2 + C2;
    t1 = t1.mul(t2);               // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))

    Mat ssim_map;
    divide(t3, t1, ssim_map);      // ssim_map =  t3./t1;

    Scalar mssim = mean(ssim_map); // mssim = average of ssim map
    return (mssim[0] + mssim[1] + mssim[2])/3;
}



extern "C"
JNIEXPORT jfloat JNICALL
Java_com_example_myapplication_jni_FinetuneUtils_getLight(JNIEnv *env, jclass clazz, jintArray buf,
                                                          jint w, jint h) {
    jfloat result = 0;
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, JNI_FALSE);
    if (cbuf == NULL){
        return 0;
    }
    Mat imgData(h, w, CV_8UC4, (unsigned char*) cbuf);
    uchar* ptr = imgData.ptr(0);
    for(int i = 0; i < w*h; i ++){
        //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
        //对于一个int四字节，其彩色值存储方式为：BGRA
        result += ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114;
    }
    result = result / (w * h);
    return result / 255;

}
extern "C"
JNIEXPORT jfloat JNICALL
Java_com_example_myapplication_jni_FinetuneUtils_getSimilarity(JNIEnv *env, jclass clazz,
                                                               jintArray src, jintArray cmp, jint w,
                                                               jint h) {
    // TODO: implement getSimilarity()
    jfloat result = 0;
    jint *cbuf_src, *cbuf_cmp;
    cbuf_src = env->GetIntArrayElements(src, JNI_FALSE);
    cbuf_cmp = env->GetIntArrayElements(cmp, JNI_FALSE);
    if (cbuf_src == NULL || cbuf_cmp == NULL){
        return 0;
    }
    Mat srcData(h, w, CV_8UC4, (unsigned char*) cbuf_src);
    Mat cmpData(h, w, CV_8UC4, (unsigned char*) cbuf_cmp);
    return getMSSIM(srcData, cmpData);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_example_myapplication_jni_FinetuneUtils_getFinetuneResultStart(JNIEnv *env, jclass clazz,
                                                                        jfloatArray scores,
                                                                        jint interval_frames,
                                                                        jint total_frames) {
    // TODO: implement getFinetuneResultStart()
    jint start = 0; jfloat max_scores = 0, tmp_scores = 0;
    jfloat* rec = env->GetFloatArrayElements(scores, JNI_FALSE);
    if (rec == NULL || interval_frames > total_frames){
        return 0;
    }
    for (int i = 0; i <= interval_frames; ++i){
        tmp_scores += rec[i];
    }
    max_scores = tmp_scores;
    for (int i = 1; i + interval_frames < total_frames; ++i){
        tmp_scores += rec[i + interval_frames] - rec[i-1];
        if (tmp_scores > max_scores){
            max_scores = tmp_scores;
            start = i;
        }
    }
    return start;
}