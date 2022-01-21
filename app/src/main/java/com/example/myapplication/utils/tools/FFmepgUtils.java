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
        String cmd = "ffmpeg -i %s -s 800x800 -aspect \"1:1\" -ar 44100 -r 30 %s"; //30fps
        cmd = String.format(cmd, inPath, outPath);
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
    public static void getFrames(String inPath, String outDir) {
        final int fps = 5;
        File outFileDir = new File(outDir);
        if (!outFileDir.exists()){
            outFileDir.mkdirs();
        }
        String cmd = "ffmpeg -i %s -f image2 -vf fps=fps=%d -qscale:v 2 %s"; // 这里需要设置-safe 为0
        cmd = String.format(cmd, inPath, fps, outDir) + "/%05d.jpeg";
        Log.d("#getFrames", cmd);
        FFmpegCmd.run(cmd.split(" "));
    }

}
