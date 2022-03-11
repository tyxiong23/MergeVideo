package com.example.myapplication.utils.tools;

import android.content.Context;
import android.util.Log;

import java.io.File;

/**
 * 程序运行中的一些常数
 */
public class Constants {
    private static String CACHE_DIR = "/storage/emulated/0/Android/data/com.example.myapplication/cache";
    private static String SAVE_DIR = "/storage/emulated/0/Android/data/com.example.myapplication/files";
    private static long rand = System.currentTimeMillis(); //本次任务的编号，由系统时钟生成
    public static long update() {
        rand = System.currentTimeMillis();
        return rand;
    }
    public static String getCacheDir() { return CACHE_DIR; }
    public static String getRunningDir() {
        return CACHE_DIR + "/" + String.valueOf(rand);
    }
    public static long getId() {
        return rand;
    }
    public static String getSaveDir() { return SAVE_DIR; }
    public static int W = 1080, H =1080;
    public static float minRoughInterval = 4.5f;
    public static float defaultFinetuneInterval = 3.0f;
    public static final String TEMPDIR = "/data/data/com.example.myapplication/tempfile";
    public static String MUSICPATH = "/data/data/com.example.myapplication/music/summer.mp3";
    public static int MUSICFPM = 93;
    public static final float USER_FINETUNE_ALPHA = 0.2f;
    public static void updateBGM(MusicInfo music){
        MUSICFPM = music.fpm;
        MUSICPATH = music.src;
    }
    public static void init(Context ctx){
        CACHE_DIR = ctx.getCacheDir().getAbsolutePath();
        SAVE_DIR = ctx.getFilesDir().getAbsolutePath();
    }


}
