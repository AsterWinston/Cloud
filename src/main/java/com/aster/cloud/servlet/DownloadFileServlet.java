package com.aster.cloud.servlet;

import com.aster.cloud.utils.FileManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/downloadFile")
public class DownloadFileServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("经过DownloadFileServlet的请求 = " + request.getRequestURI());
        String file_path_to_download = request.getParameter("file_path_to_download");
        String userDirectory = (String) request.getSession().getAttribute("user_directory");
        System.out.println("file_path_to_download = " + file_path_to_download);
        if(file_path_to_download.startsWith(userDirectory.replace("\\", "/"))){
            if(FileManager.downloadFileOrDirectory(response, file_path_to_download)){
                System.out.println("\"" + file_path_to_download + "\"下载成功");
            } else{
                System.err.println("\"" + file_path_to_download + "\"下载失败");
            }
        } else{
            System.err.println("\"" + file_path_to_download + "\"拒绝下载");
        }

    }
}
