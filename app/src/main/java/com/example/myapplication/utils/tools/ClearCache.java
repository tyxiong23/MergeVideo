package com.example.myapplication.utils.tools;

import java.io.File;

public class ClearCache {
    //	// 删除完文件后删除文件夹
    // param folderPath 文件夹完整绝对路径
    public static int delFolder(String folderPath) {
        try {
            int count =  delAllFile(folderPath); // 删除完里面所有内容
            //不想删除文佳夹隐藏下面
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
            return count;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 删除指定文件夹下所有文件
    // param path 文件夹完整绝对路径
    public static int delAllFile(String path) {
        int count = 0;
        File file = new File(path);
        if (!file.exists()) {
            return count;
        }
        if (!file.isDirectory()) {
            return count;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
                count += 1;
            }
            if (temp.isDirectory()) {
				count += delFolder(path + "/" + tempList[i]);// 再删除空文件夹
            }
        }
        return count;
    }
}
