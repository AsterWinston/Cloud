package com.aster.cloud.utils;
import java.io.File;
import java.nio.file.*;

public class PathManager {
    public static String getRelativePath(String parent, String path){

        Path parentPath = Paths.get(parent);
        Path fullPath = Paths.get(path);
        Path relative = parentPath.relativize(fullPath);
        return relative.toString();
    }
    //可以确保dir是linux类型的/这种分隔符
    public static String getParentPath(String dir) {
        if (dir == null || dir.isEmpty() || dir.equals("/")) {
            return ""; // 已经是根目录或者无效路径
        }
        // 去掉末尾多余的 /
        if (dir.endsWith("/")) {
            dir = dir.substring(0, dir.length() - 1);
        }
        int lastSlash = dir.lastIndexOf('/');
        if (lastSlash == -1) {
            return ""; // 没有父路径
        }
        if (lastSlash == 0) {
            return "/"; // 父路径是根目录
        }
        return dir.substring(0, lastSlash);
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
