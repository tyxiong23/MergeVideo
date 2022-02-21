package com.example.myapplication.utils.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 用于复制文件，在最后保存视频时用到
 */
public class CopyFile {
    public static boolean fileChannelCopy(String srcDirName, String destDirName) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        boolean result = true;

        try {
            fi = new FileInputStream(new File(srcDirName));
            fo = new FileOutputStream(new File(destDirName));
            in = fi.getChannel(); // 得到对应的文件通道
            out = fo.getChannel(); // 得到对应的文件通道
            in.transferTo(0, in.size(), out); // 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }
}
