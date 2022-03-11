package com.example.myapplication.utils.tools;

import java.util.ArrayList;
import java.util.List;

public class Theme {
    List<MusicInfo> musics;
    int themeID;
    List<String> themes;
    public Theme(){
        musics = new ArrayList<>();
        themes = new ArrayList<>();

        musics.add(new MusicInfo(134, "/data/data/com.example.myapplication/music/unravel.mp3"));
        themes.add("激烈");

        musics.add(new MusicInfo(93, "/data/data/com.example.myapplication/music/summer.mp3"));
        themes.add("欢快");

        musics.add(new MusicInfo(65, "/data/data/com.example.myapplication/music/innocent.mp3"));
        themes.add("忧伤");

        themeID = 0;
    }
    public MusicInfo getMusic(){
        return musics.get(themeID);
    }
    public String getTheme(){
        return themes.get(themeID);
    }
    public void recogize(String[] frames, String words){
        themeID = (int)(words.length() + frames.length + System.currentTimeMillis()) % musics.size();
    }
}
