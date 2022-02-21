package com.example.myapplication.jni;

public class FineResult{
    public String videoPath;
    public String startFramePath;
    public String endFramePath;
    public float score;
    public FineResult(String _vpath, String _sFpath, String _eFpath, float _score){
        videoPath = _vpath;
        startFramePath = _sFpath;
        endFramePath = _eFpath;
        score = _score;
    }
}
