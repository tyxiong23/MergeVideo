package com.example.myapplication.utils.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.jni.FFmpegCmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于调用FFmpeg命令行的工具类
 * 需要自己新建线程
 */

public class FFmpegUtils {

    public static boolean resampleVideo(String inPath, String outPath) { //先resize之后crop
        final int resampleFPS = 24;
        File outF = new File(outPath);
        String testFramePath = outF.getParent() + "/test.jpeg";

        getFrame(inPath, testFramePath, 5, 10);
        Bitmap testFrame = BitmapFactory.decodeFile(testFramePath);
        int origin_w = testFrame.getWidth(), origin_h = testFrame.getHeight(); //原视频宽高

        if (origin_w < Constants.W || origin_h < Constants.H){
            Log.d("ResampleVideo", "origin video too small! rescale!");
            String interVideo = outF.getParent() + "/test.mp4";
            String cmd = "ffmpeg -i %s -vf scale=%d:%d -ar 44100 -r %d -y %s";
            Log.d("original video", "w:" + origin_w + " h:" + origin_h);
            if (origin_w * Constants.H > origin_h * Constants.W){
                cmd = String.format(cmd, inPath, -1, Constants.H, resampleFPS, interVideo);
            } else {
                cmd = String.format(cmd, inPath, Constants.W, -1, resampleFPS, interVideo);
            }
            FFmpegCmd.run(cmd.split(" "));
            inPath = interVideo;
        }


        getFrame(inPath, testFramePath, 5, 10);
        testFrame = BitmapFactory.decodeFile(testFramePath);
        origin_w = testFrame.getWidth(); origin_h = testFrame.getHeight(); //原视频宽高
        Log.d("resize", origin_w + " " + origin_h);

        String cmd1 = "ffmpeg -i %s -vf crop=%d:%d -ar 44100 -r %d -y %s";
        cmd1 = String.format(cmd1, inPath, Constants.W, Constants.H, resampleFPS, outPath);
        FFmpegCmd.run(cmd1.split(" "));
        return true;
    }
    public static void mergeVideos(List<String> selectedVideos, String finalPath) {
        List<String> inter_ts = new ArrayList<>();
        File f = new File(selectedVideos.get(0));
        String txt_tmp = f.getParent() + "/concat.txt";
        try{
            BufferedWriter txt_out = new BufferedWriter(new FileWriter(txt_tmp));
            for (int i = 0; i < selectedVideos.size(); ++i) {
                String mp4_tmp = selectedVideos.get(i);
                txt_out.write( "file \'" + mp4_tmp + "\'\n");
            }
            txt_out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        String cmd = "ffmpeg -f concat -safe 0 -i %s -c copy %s"; // 这里需要设置-safe 为0
        cmd = String.format(cmd, txt_tmp, finalPath);

        Log.d("final", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }
    public static void getFrames(String inPath, String outDir, int fps) {

        File outFileDir = new File(outDir);
        if (!outFileDir.exists()){
            outFileDir.mkdirs();
        }
        String cmd = "ffmpeg -i %s -y -f image2 -vf fps=fps=%d -qscale:v 2 %s"; // 这里需要设置-safe 为0
        cmd = String.format(cmd, inPath, fps, outDir + "/%05d.jpeg") ;
        Log.d("#getFrames", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }

    private static String getTimeString(int frame, int fps) {
        int f_min = (int)(frame / (fps * 60)), f_sec = (int)(frame / fps) % 60;
        int f_rem = (int)(((float)frame / (float)fps - (int)(frame / fps)) * 100);
        String timeString = String.format("00:%02d:%02d.%02d", f_min, f_sec, f_rem);
        return timeString;
    }

    // startFrame 从1开始, intv 持续时间
    public static void cutVideo(String inPath, String outPath, int startF, int endF, int fps) {
        File outFile = new File(outPath);
        if (!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        String start_time = getTimeString(startF, fps), intv_time = getTimeString(endF - startF, fps);
        String cmd = String.format("ffmpeg -ss %s -i %s -t %s -c copy -y %s", start_time, inPath,  intv_time, outPath);
        Log.d("#CutVideo", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }

    public static String getFrame(String inPath, String outPath, int frame, int fps){
        File outFile = new File(outPath);
        if (!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        String timeString = getTimeString(frame, fps);
        String cmd = String.format("ffmpeg -ss %s -i %s -y -f image2 -vframes 1 -qscale:v 2 %s",
                timeString, inPath, outPath);
        FFmpegCmd.run(cmd.split(" "));
        return outPath;
    }


    public static String AddBGMForVideoDelete(String inVideo, String inAudio, String outVideo) {
        String cmd = "ffmpeg -i %s -i %s -map 0:v -map 1:a -c:v copy -shortest -y %s";
        cmd = String.format(cmd, inVideo, inAudio, outVideo);
        FFmpegCmd.run(cmd.split(" "));
        return outVideo;
    }




}
