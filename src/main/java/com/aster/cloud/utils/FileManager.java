package com.aster.cloud.utils;

import com.aster.cloud.beans.FileOrDirInformation;
import com.oracle.wls.shaded.org.apache.regexp.RE;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
                size = Math.toIntExact(getDirectorySize(file) / (1024 * 1024)); // 文件夹大小
                type = FileOrDirInformation.Type.DIRECTORY;
            }

            list.add(new FileOrDirInformation(name, time, size, type));
        }

        return list;
    }

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
    public static boolean uploadFile(HttpServletRequest request, HttpServletResponse response,
                                     String currentDir) throws IOException, ServletException {
        // 创建上传目录（如果不存在）
        File uploadDir = new File(currentDir);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                return false; // 目录创建失败
            }
        }

        // 处理上传的文件
        for (Part part : request.getParts()) {
            String fileName = part.getSubmittedFileName();
            if (fileName == null || fileName.isEmpty()) {
                continue; // 跳过非文件字段
            }

            // 保存文件
            File file = new File(uploadDir, fileName);
            try (InputStream is = part.getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前已使用空间(MB)
     */
    public static long getSizeNow(HttpServletRequest request) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String userName = (String) request.getSession().getAttribute("user_name");

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement("select dir_name from user where name = ?");
            pstmt.setString(1, userName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String dirName = rs.getString("dir_name");
                String basePath = (String) request.getServletContext().getAttribute("file_store_path");
                String fullDir = basePath.replace("\\", "/") + "/" + dirName;

                return getDirectorySize(new File(fullDir)) / (1024L * 1024);
            } else {
                throw new RuntimeException("用户目录不存在");
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库操作错误：" + e.getMessage());
        } finally {
            DBManager.closeConnection(conn);
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        }
    }

    /**
     * 获取空间限制(MB)
     */
    public static long getLimitSize(HttpServletRequest request) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String userName = (String) request.getSession().getAttribute("user_name");

        try {
            conn = DBManager.getConnection();
            pstmt = conn.prepareStatement("select limit_volume from user where name = ?");
            pstmt.setString(1, userName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("limit_volume");
            } else {
                throw new RuntimeException("未找到用户存储限制");
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库操作错误：" + e.getMessage());
        } finally {
            DBManager.closeConnection(conn);
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        }
    }

    /**
     * 计算目录总大小，单位为Byte
     */
    public static long getDirectorySize(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("无效的目录: " + directory.getAbsolutePath());
        }

        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }
}
