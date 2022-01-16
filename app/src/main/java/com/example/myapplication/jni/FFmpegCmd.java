package com.example.myapplication.jni;

public class FFmpegCmd {
    static {
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("swresample");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("avfilter");
//        System.loadLibrary("avpostproc");
        System.loadLibrary("ffmpeg-invoke");
        System.loadLibrary("myapplication");
    }

    private static native int run(int cmdLen, String[] cmd);
    public static native String test();

    public static int run(String[] cmd){
        return run(cmd.length,cmd);
    }
}
