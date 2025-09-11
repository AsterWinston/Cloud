package com.aster.cloud.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
@WebServlet("/createFolder")
public class CreateFolderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取前端传过来的新建文件夹路径
        String folderPath = request.getParameter("folderPath");
        System.out.println("folderPath = " + folderPath);

        // 检查文件夹路径是否为空
        if(folderPath == null || folderPath.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"文件夹名不能为空\"}");
            return;
        }

        // 获取当前用户目录（防止越权）
        String userDirectory = (String) request.getSession().getAttribute("user_directory");
        if(userDirectory == null || !folderPath.startsWith(userDirectory.replace("\\","/"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"无权限操作此目录\"}");
            return;
        }

        // 创建文件夹对象
        File newFolder = new File(folderPath);
        if(newFolder.exists()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"文件夹已存在\"}");
            return;
        }

        // 执行创建
        boolean created = newFolder.mkdir();
        if(!created) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"创建失败\"}");
            return;
        }

        // 成功
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\": \"success\", \"message\": \"文件夹创建成功\"}");
    }
}
