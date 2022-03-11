package com.example.myapplication.utils.edit;

import java.util.List;

public class EditBlock {
    private String sentence;
    private List<EditItem> items;

    public EditBlock(String _sentence, List<EditItem> _items) {
        sentence = _sentence;
        items = _items;
    }

    public List<EditItem> getBlockVideos() {
        return items;
    }

    public String getSentence() { return sentence; }



}
