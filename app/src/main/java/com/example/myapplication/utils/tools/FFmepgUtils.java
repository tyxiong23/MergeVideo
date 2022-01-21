package com.example.myapplication.utils.tools;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.jni.FFmpegCmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

// 需要自己新建线程
public class FFmepgUtils {

    public static void resampleVideo(String inPath, String outPath) {
        final int resampleFPS = 24;
        String cmd = "ffmpeg -i %s -s %dx%d -aspect \"1:1\" -ar 44100 -r %d %s"; //30fps
        cmd = String.format(cmd, inPath, Constants.W, Constants.H, resampleFPS, outPath);
        FFmpegCmd.run(cmd.split(" "));
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

        Log.e("final", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }
    public static void getFrames(String inPath, String outDir, int fps) {

        File outFileDir = new File(outDir);
        if (!outFileDir.exists()){
            outFileDir.mkdirs();
        }
        String cmd = "ffmpeg -i %s -f image2 -vf fps=fps=%d -qscale:v 2 %s"; // 这里需要设置-safe 为0
        cmd = String.format(cmd, inPath, fps, outDir) + "/%05d.jpeg";
        Log.d("#getFrames", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }

    // startFrame 从1开始
    public static void cutVideo(String inPath, String outPath, int startF, int endF, int fps) {
        File outFile = new File(outPath);
        if (!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        int s_min = (int)(startF / (fps * 60)), s_sec = (int)(startF / fps) % 60;
        int e_min = (int)(endF / (fps * 60)), e_sec = (int)(endF / fps) % 60;
        int s_rem = (int)(((float)startF / (float)fps - (int)(startF / fps)) * 100);
        int e_rem =  (int)(((float)endF / (float)fps - (int)(endF / fps)) * 100);
        String start_time = String.format("00:%02d:%02d.%02d", s_min, s_sec, s_rem);
        String end_time = String.format("00:%02d:%02d.%02d", e_min, e_sec, e_rem);
        String cmd = String.format("ffmpeg -i %s -ss %s -to %s -c copy %s", inPath, start_time, end_time, outPath);
        Log.d("#CutVideo", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }

}
