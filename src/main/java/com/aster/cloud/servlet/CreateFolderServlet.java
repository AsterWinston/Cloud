package com.aster.cloud.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

@WebServlet("/createFolder")
public class CreateFolderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过CreateFolderServlet的请求 = " + request.getRequestURI());
        JSONObject result = new JSONObject();
        try {
            // 获取前端传过来的新建文件夹路径
            String folderPath = request.getParameter("folderPath");
            System.out.println("folderPath = " + folderPath);

            // 检查文件夹路径是否为空
            if (folderPath == null || folderPath.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("status", "error");
                result.put("message", "文件夹名不能为空");
                response.getWriter().write(result.toString());
                return;
            }

            // 获取当前用户目录（防止越权）
            String userDirectory = (String) request.getSession().getAttribute("user_directory");
            if (userDirectory == null || !folderPath.replace("\\", "/").startsWith(userDirectory.replace("\\", "/"))) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                result.put("status", "error");
                result.put("message", "无权限操作此目录");
                response.getWriter().write(result.toString());
                return;
            }

            // 创建文件夹对象
            File newFolder = new File(folderPath);
            if (newFolder.exists()) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                result.put("status", "error");
                result.put("message", "文件夹已存在");
                response.getWriter().write(result.toString());
                return;
            }

            // 执行创建
            boolean created = newFolder.mkdir();
            if (!created) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.put("status", "error");
                result.put("message", "文件夹创建失败");
                response.getWriter().write(result.toString());
                return;
            }

            // 成功
            response.setStatus(HttpServletResponse.SC_OK);
            result.put("status", "success");
            result.put("message", "文件夹创建成功");
            response.getWriter().write(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("status", "error");
            result.put("message", "服务器异常：" + e.getMessage());
            response.getWriter().write(result.toString());
        }
    }
}
