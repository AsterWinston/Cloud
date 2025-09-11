package com.aster.cloud.servlet;

import com.aster.cloud.utils.FileManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/uploadFile")
@MultipartConfig
public class UploadFileServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("UploadFile中收到上传请求");
        String path_to_upload = request.getParameter("path_to_upload");
        String userDirectory = (String) request.getSession().getAttribute("user_directory");
        System.out.println("path_to_upload = " + path_to_upload);
        if(path_to_upload.startsWith(userDirectory.replace("\\", "/"))){
            FileManager.uploadFile(request, response, path_to_upload);
            System.out.println("上传成功");
        } else{
            System.out.println("拒绝上传");
        }

    }
}
