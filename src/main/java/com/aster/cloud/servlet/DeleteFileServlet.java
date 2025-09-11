package com.aster.cloud.servlet;

import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.PathManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/deleteFile")
public class DeleteFileServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取要删除的文件或目录路径
        String fileOrDirToDelete = request.getParameter("file_or_dir_to_delete");
        String userDirectory = (String) request.getSession().getAttribute("user_directory");

        // 用于调试，打印出接收到的路径
        System.out.println("file_or_dir_to_delete = " + fileOrDirToDelete);

        // 检查是否有权限删除该文件或文件夹
        if (fileOrDirToDelete != null && fileOrDirToDelete.startsWith(userDirectory.replace("\\", "/"))) {
            // 删除文件或目录
            boolean deleteSuccess = FileManager.deleteFileOrDirectory(fileOrDirToDelete);

            // 设置删除结果的响应
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (deleteSuccess) {
                // 删除成功，返回成功的 JSON 响应
                response.getWriter().write("{\"status\": \"success\", \"message\": \"删除成功\"}");
            } else {
                // 删除失败，返回失败的 JSON 响应
                response.getWriter().write("{\"status\": \"error\", \"message\": \"删除失败\"}");
            }
        } else {
            // 如果没有权限删除，返回拒绝的响应
            response.getWriter().write("{\"status\": \"error\", \"message\": \"拒绝删除\"}");
        }
    }
}
