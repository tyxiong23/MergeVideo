package com.example.myapplication.utils.tools;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SelectVideos {
    private static List<Record> selectedVideos = new ArrayList<>();
    public static List<String> getList() {
        List<String> result = new ArrayList<>();
        Collections.sort(selectedVideos);
        for (Record rec: selectedVideos)
            result.add(rec.path);
        return  result;
    }
    public static void insert(String str, int index) {
        Record rec = new Record(str, index);
        for (Record item: selectedVideos) {
            if (item.equals(rec)) return;
        }
        selectedVideos.add(rec);
        Log.d("add edit" + index, str);
    }
    public static void remove(String str, int index) {
        Record rec = new Record(str, index);
        for (Record item: selectedVideos) {
            if (item.equals(rec)) {
                selectedVideos.remove(item);
                Log.d("remove edit" + index, str);
            }
        }
    }
    public static void clear() {
        selectedVideos.clear();
    }
}

class Record implements Comparable {
    public String path;
    public int index;
    public Record(String p, int i) {
        path = p; index = i;
    }

    @Override
    public int compareTo(Object o) {
        Record obj = (Record) o;
        if (index < obj.index) return -1;
        else if (index == obj.index) return 0;
        else return 1;
    }

    @Override
    public boolean equals(Object o) {
        Record obj = (Record) o;
        if (obj.index == index && obj.path.equals(path))
            return true;
        else return false;
    }
}

