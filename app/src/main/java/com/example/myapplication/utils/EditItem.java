package com.example.myapplication.utils;

import java.util.ArrayList;
import java.util.List;

public class EditItem {
    private String sentence;
    private String[] videopaths;
    private Boolean[] ifchoose = new Boolean[3];
    public EditItem(String _sentence, String[] paths) {
        sentence = _sentence;
        videopaths = paths;
        for (int i = 0; i < 3; ++i) {
            ifchoose[i] = false;
        }
    }
    public String getSentence() {
        return sentence;
    }
    public void setState(int i, boolean state) {ifchoose[i] = state; }
    public Boolean[] getStates() { return ifchoose; }
    public String[] getVideopaths() { return videopaths; }
}
