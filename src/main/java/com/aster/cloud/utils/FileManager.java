package com.aster.cloud.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

    // 静态方法：根据给定父路径和目录名称创建文件夹，如果已存在则不做任何操作
    public static void createDirectory(String parentPath, String directoryName) {
        // 使用File.separator确保跨平台的路径分隔符
        Path parent = Paths.get(parentPath);
        Path directoryPath = parent.resolve(directoryName);  // 组合路径
        createDirectories(String.valueOf(directoryPath));
    }
    public static void createDirectories(String path) {
        Path directoryPath = Paths.get(path);

        // 如果路径不存在，级联创建所有缺失的文件夹
        if (Files.notExists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);  // 自动创建所有缺失的文件夹
                System.out.println("文件夹已级联创建: " + directoryPath);
            } catch (IOException e) {
                System.err.println("FileManager中级联创建文件夹失败: " + directoryPath);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("文件夹已存在: " + directoryPath);
        }
    }

}
