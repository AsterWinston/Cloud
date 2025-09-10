package com.aster.cloud.utils;

import com.aster.cloud.beans.FileOrDirInformation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
    public static boolean isPathExists(String fullPath) {
        // 空路径直接返回不存在
        if (fullPath == null || fullPath.trim().isEmpty()) {
            return false;
        }

        // 创建File对象并检查是否存在
        File file = new File(fullPath);
        return file.exists();
    }
    /**
     * 获取目录下所有文件和文件夹的信息
     * @param dirPath 目录路径
     * @return List<FileOrDirInformation>
     */
    public static List<FileOrDirInformation> getFileOrDirInfo(String dirPath) {
        List<FileOrDirInformation> list = new ArrayList<>();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return list; // 如果不是目录，返回空列表
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return list;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (File file : files) {
            String name = file.getName();
            String time = sdf.format(file.lastModified());
            int size = 0;
            FileOrDirInformation.Type type;

            if (file.isFile()) {
                size = (int) (file.length() / (1024 * 1024)); // 转换为MB，取整
                type = FileOrDirInformation.Type.FILE;
            } else { // 文件夹
                size = getDirectorySize(file); // 文件夹大小
                type = FileOrDirInformation.Type.DIRECTORY;
            }

            list.add(new FileOrDirInformation(name, time, size, type));
        }

        return list;
    }

    /**
     * 递归计算文件夹中所有文件大小（MB）
     */
    private static int getDirectorySize(File dir) {
        int total = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;
        for (File file : files) {
            if (file.isFile()) {
                total += file.length();
            } else if (file.isDirectory()) {
                total += getDirectorySize(file);
            }
        }
        return (int) (total / (1024 * 1024)); // 转换为MB
    }
    //保证了path是linux样式，即D:/test或/home

    public static boolean deleteFileOrDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false; // 文件或文件夹不存在
        }

        if (file.isFile()) {
            // 是文件，直接删除
            return file.delete();
        } else {
            // 是目录，递归删除里面的内容
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    // 递归删除
                    deleteFileOrDirectory(f.getAbsolutePath());
                }
            }
            // 删除空目录
            return file.delete();
        }
    }



}
