package com.aster.cloud.utils;

import java.io.File;
import java.nio.file.*;

public class PathManager {
    public static String getRelativePath(String parent, String path){
        Path parentPath = Paths.get(parent);
        Path fullPath = Paths.get(path);
        Path relativePath = parentPath.relativize(fullPath);
        return relativePath.toString();
    }

    public static boolean isPathExists(String fullPath) {
        // 空路径直接返回不存在
        if (fullPath == null || fullPath.trim().isEmpty()) {
            return false;
        }
        // 创建File对象并检查是否存在
        File file = new File(fullPath);
        return file.exists();
    }
    public static boolean isFile(String path){
        if(path == null){
            return false;
        }
        File file = new File(path);
        return file.isFile();
    }

}
