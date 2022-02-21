package com.example.myapplication.jni;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.myapplication.utils.tools.ClearCache;
import com.example.myapplication.utils.tools.Constants;
import com.example.myapplication.utils.tools.FFmpegUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 有关于细剪的相关函数定义
 */
public class FinetuneUtils {

    private static int FineImgH = 400, FineImgW = 400;
    private static int fps = 10;
    private static int music_fpm = 93;
    private static float a_light = 0.4f;

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("finetune-lib");
    }

    private static native float getLight(int[] buf, int w, int h);
    private static native float getSimilarity(int[] src, int[] cmp, int w, int h);
    private static native FineCutObj getFinetuneResult(float[] scores, int intervalFrames, int totalFrames, FineCutObj obj);


    /**
     * 对粗剪视频进行抽帧
     * @param videoPath 输入视频路径
     * @param outDir 保存帧的文件夹
     * @return 帧路径的数组
     */
    private static String[] getFinetuneFrames(String videoPath, String outDir){
        File f = new File(outDir);
        if (!f.exists()) {
            f.mkdirs();
        }
        else {
            ClearCache.delAllFile(outDir);
        }

        FFmpegUtils.getFrames(videoPath, outDir, fps);

        File result_file = new File(outDir);
        String[] result_paths = result_file.list();
        for (int i = 0; i < result_paths.length; ++i){
            result_paths[i] = outDir + "/" + result_paths[i];
        }
        return result_paths;

    }

    /**
     * 调用细剪的全过程，包括抽帧，计算分数和剪裁
     * @param inVideoPath 输入视频路径
     * @param FineDir 细剪结果保存文件夹
     * @param id 当前是第几个视频
     * @return 细剪结果（细剪视频路径/开始帧地址/结束帧地址（用于dp计算）/视频分数
     */
    public static FineResult fineTune(String inVideoPath, String FineDir, int id) {

        String frames_dir = FineDir + String.format("/%d", id);
        File f = new File(frames_dir);

        if (!f.exists()) {
            f.mkdirs();
        }
        String[] frames_path = getFinetuneFrames(inVideoPath, frames_dir);
        List<Bitmap> imgs = new ArrayList<>();
        for (String path : frames_path) {
            try {
                Bitmap b_img = BitmapFactory.decodeFile(path);
                imgs.add(b_img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        float[] light_scores = new float[imgs.size()];
        float[] similar_scores = new float[imgs.size()];
        float[] total_scores = new float[imgs.size()];

        Long light_t1 = System.currentTimeMillis();
        for (int i = 0; i < imgs.size(); ++i) {
            Bitmap img = imgs.get(i);
            int h = img.getHeight(), w = img.getWidth();
            int[] img_buf = new int[w * h];
            img.getPixels(img_buf, 0, w, 0, 0, w, h);
            light_scores[i] = FinetuneUtils.getLight(img_buf, w, h);
        }
        long light_t2 = System.currentTimeMillis();
        Log.d("LIGHT SCORES", String.format("Calculate lights for %d imgs in %d s",
                imgs.size(), (light_t2 - light_t1) / 1000));

        Long similar_t1 = System.currentTimeMillis();
        for (int i = 0; i < imgs.size() - 1; ++i) {
            Bitmap img1 = imgs.get(i), img2 = imgs.get(i + 1);
            int h = img1.getHeight(), w = img1.getWidth();
            int[] img1_buf = new int[w * h];
            int[] img2_buf = new int[w * h];
            img1.getPixels(img1_buf, 0, w, 0, 0, w, h);
            img2.getPixels(img2_buf, 0, w, 0, 0, w, h);
            float similarity = FinetuneUtils.getSimilarity(img1_buf, img2_buf, w, h);
            if (i == 0) {
                similar_scores[i] = similarity;
                similar_scores[i + 1] = similarity / 2.0f;
            } else if (i == imgs.size() - 2) {
                similar_scores[i] += similarity / 2.0f;
                similar_scores[i + 1] = similarity;
            } else {
                similar_scores[i] += similarity / 2.0f;
                similar_scores[i + 1] = similarity / 2.0f;
            }
        }
        Long similar_t2 = System.currentTimeMillis();
        Log.d("SIMILAR SCORES", String.format("Calculate similarity for %d imgs in %d s",
                imgs.size(), (similar_t2 - similar_t1) / 1000));

        for (int i = 0; i < imgs.size(); ++i) {
            total_scores[i] = a_light * light_scores[i] + (1 - a_light) * similar_scores[i];
            Log.d("Scores", String.format("light:%f similar:%f total:%f",
                    light_scores[i], similar_scores[i], total_scores[i]));
        }

        float interval = 0.0f;
        while (interval < Constants.defaultFinetuneInterval) {
            interval += 60.0f / music_fpm;
        }
        Log.d("INTERVAL", String.valueOf(interval));
        while (interval > Constants.minRoughInterval)
            interval -= 60.0f / music_fpm;
        int numFrames = (int) (interval * fps);
        FineCutObj obj = new FineCutObj();
        obj = getFinetuneResult(total_scores, numFrames, imgs.size(), obj);
        obj.start += 1; //从1开始
        Log.d("Finetune Cut", String.format("start %d score %f", obj.start, obj.score));
        String outVideoPath = FineDir + String.format("/fine-%d.mp4", id);
        FFmpegUtils.cutVideo(inVideoPath, outVideoPath, obj.start, obj.start + numFrames, fps);
        String startFramePath = FineDir + String.format("/start-%d.jpeg", id);
        String endFramePath = FineDir + String.format("/end-%d.jpeg", id);
        FFmpegUtils.getFrame(inVideoPath, startFramePath, obj.start, fps);
        FFmpegUtils.getFrame(inVideoPath, endFramePath, obj.start + numFrames - 1, fps);
        return new FineResult(outVideoPath, startFramePath, endFramePath, obj.score);
    }

    public static float getImageSimilarity(int[] src, int[] cmp, int w, int h) {
        return getSimilarity(src, cmp, w, h);
    }

}


