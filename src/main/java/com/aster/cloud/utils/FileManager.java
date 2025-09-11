package com.aster.cloud.utils;

import com.aster.cloud.beans.FileOrDirInformation;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    //
    public static boolean downloadFileOrDirectory(HttpServletResponse response, String path) throws IOException{
        if(!PathManager.isPathExists(path))return false;
        if(PathManager.isFile(path)){
            String[] strings = path.split("/");
            String fileName = strings[strings.length - 1];
            System.out.println("fileName = " + fileName);
            response.setContentType("application/octet-stream");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
            File file = new File(path);
            response.setContentLengthLong(file.length());
            try (InputStream is = new FileInputStream(file);OutputStream os = response.getOutputStream()){
                byte[] buffer = new byte[8192];
                int len;
                while((len = is.read(buffer)) != -1){
                    os.write(buffer, 0, len);
                }
            } catch (FileNotFoundException e) {
                System.err.println("文件" + fileName + "没找到");
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IOException e) {
                System.err.println("FileManager中出现IO异常");
                throw e;
            }
        } else{
            //打包zip
            String[] strings = path.split("/");
            String fileName = strings[strings.length - 1] + ".zip";
            System.out.println("fileName = " + fileName);

            response.setContentType("application/zip");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);

            File file = new File(path);
            try(ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())){
                zipFolder(file, file.getName(), zos);
            } catch(IOException e){
                System.err.println("FileManager中出现IO异常");
                e.printStackTrace();
                throw e;
            }
        }
        return true;
    }
    private static void zipFolder(File folder, String baseName, ZipOutputStream zos)
            throws IOException {
        for(File file: folder.listFiles()) {
            if(file.isDirectory()){
                zipFolder(folder, baseName + "/" + file.getName(), zos);
            } else{
                try(FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(baseName + "/" + file.getName());
                    zos.putNextEntry(entry);
                    byte[] buffer = new byte[8192]; // 边读边写
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                } catch (FileNotFoundException e){
                    System.err.println("文件" + file.getName() + "未找到");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } catch (IOException e){
                    System.err.println("FileManager中出现IO异常");
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }
    public static void uploadFile(HttpServletRequest request, HttpServletResponse response, String currentDir) throws IOException, ServletException {
        // 确保目录存在
        File uploadDir = new File(currentDir);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 遍历所有上传的 Part
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            String fileName = part.getSubmittedFileName();

            // 有些 Part 不是文件（比如普通表单字段），要过滤掉
            if (fileName == null || fileName.isEmpty()) continue;

            File file = new File(uploadDir, fileName);

            try (InputStream is = part.getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }
        }

    }


}
