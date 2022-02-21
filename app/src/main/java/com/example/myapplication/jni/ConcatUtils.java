package com.example.myapplication.jni;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ConcatUtils {
    /**
     *
     * @param videoScores 细剪部分计算得到的视频分数
     * @param startFramePaths 每个视频的第一帧地址
     * @param endFramePaths 米格视频的最后一帧地址
     * @return 被选中的视频的rank（按顺序）
     */
    static {
        System.loadLibrary("concat-lib");
    }
    public static List<Integer> concat(float[] videoScores, String[] startFramePaths, String[] endFramePaths) {
        List<Integer> result = new ArrayList<>();
        int length = videoScores.length;
        if (startFramePaths.length != length || endFramePaths.length != length){
            Log.e("CONCAT Length Mismatch", String.format("videos: %d startFrames:%d endFrames:%d",
                    length, startFramePaths.length, endFramePaths.length));
            result.add(0);
            return result;
        }
        List<Bitmap> imgs_start = new ArrayList<>(), imgs_end = new ArrayList<>();
        for (String path : startFramePaths) {
            try {
                Bitmap b_img = BitmapFactory.decodeFile(path);
                System.out.println("start frames add:" + path);
                imgs_start.add(b_img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (String path : endFramePaths) {
            try {
                Bitmap b_img = BitmapFactory.decodeFile(path);
                System.out.println("end frames add:" + path);
                imgs_end.add(b_img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        float[] concatScores = new float[length * length];
        for (int i = 0; i < length * length; ++i) {
            concatScores[i] = 0.0f;
        }
        for (int i = 0; i < length - 1; ++i) {
            for (int j = i + 1; j < length; ++j) {
                Bitmap img1 = imgs_end.get(i), img2 = imgs_start.get(j);
                int w = img1.getWidth(), h = img1.getHeight();
                int[] img1_buf = new int[w * h];
                int[] img2_buf = new int[w * h];
                img1.getPixels(img1_buf, 0, w, 0, 0, w, h);
                img2.getPixels(img2_buf, 0, w, 0, 0, w, h);
                concatScores[i * length + j] = FinetuneUtils.getImageSimilarity(img1_buf, img2_buf,
                        w, h);
                System.out.println(i + " " + j + " concatScore " + concatScores[i * length + j]);
            }
        }
        for (int i = 0; i < length; ++i) {
            System.out.println("video" + i + " score:" + videoScores[i]);
        }
        int[] dp_results = dp(videoScores, concatScores, length);
        for (int i = 0; i < length; ++i) {
            System.out.println("result " + i+ " " + dp_results[i]);
            if (dp_results[i] == 1) {

                result.add(i);
            }
        }
        return result;
    }

    /**
     * JNI 进行dp
     * @param videoScores 视频平均分
     * @param concatScores 视频连接分数 cancatScores[i*len+j] = calc(i,j)
     * @param len 总视频数量
     * @return 每个视频是否选择，1代表选中，0代表不选
     */
    private static native int[] dp(float[] videoScores, float[] concatScores, int len);
}
