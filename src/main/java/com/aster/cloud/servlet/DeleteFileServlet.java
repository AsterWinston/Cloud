package com.aster.cloud.servlet;

import com.aster.cloud.utils.FileManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

@WebServlet("/deleteFile")
public class DeleteFileServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("经过DeleteFileServlet的请求 = " + request.getRequestURI());

        response.setContentType("application/json");
        String fileOrDirToDelete = request.getParameter("file_or_dir_to_delete");
        String userDirectory = (String) request.getSession().getAttribute("user_directory");

        JSONObject json = new JSONObject();

        if (fileOrDirToDelete != null && fileOrDirToDelete.startsWith(userDirectory.replace("\\", "/"))) {
            boolean deleteSuccess = FileManager.deleteFileOrDirectory(fileOrDirToDelete);

            if (deleteSuccess) {
                json.put("status", "success");
                json.put("message", "删除成功");
            } else {
                json.put("status", "error");
                json.put("message", "删除失败");
            }
        } else {
            json.put("status", "error");
            json.put("message", "拒绝删除");
        }

        response.getWriter().write(json.toString());
    }
}
