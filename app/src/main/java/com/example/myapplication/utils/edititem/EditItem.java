package com.example.myapplication.utils.edititem;

public class EditItem {
    private String sentence;
    private String videopath;
    //    private Boolean[] ifchoose = new Boolean[3];
    private Boolean ifchoose = false;
    public EditItem(String _sentence, String path) {
        sentence = _sentence;
        videopath = path;
    }
    public String getSentence() {
        return sentence;
    }
    public void setState(boolean state) {ifchoose = state; }
    public Boolean getState() { return ifchoose; }
    public String getVideopath() { return videopath; }
}
