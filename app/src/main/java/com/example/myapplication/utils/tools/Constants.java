package com.example.myapplication.utils.tools;

public class Constants {
    private static String CACHE_DIR = "/storage/emulated/0/Android/data/com.example.myapplication/files/Movies";
    private static long rand = System.currentTimeMillis();
    public static long update() {
        rand = System.currentTimeMillis();
        return rand;
    }
    public static String getCacheDir() { return CACHE_DIR; }
    public static String getRunningDir() {
        return CACHE_DIR + "/" + String.valueOf(rand);
    }
    public static void updateCacheDir(String dir) {
        CACHE_DIR = dir;
    }
    public static long getId() {
        return rand;
    }
    public static String getSaveDir() { return CACHE_DIR.replace("Movies", "Save"); }

}
