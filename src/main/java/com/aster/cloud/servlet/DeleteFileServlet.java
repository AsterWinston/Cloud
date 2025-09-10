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
        String file_or_dir_to_delete = request.getParameter("file_or_dir_to_delete");
        String userDirectory = (String) request.getSession().getAttribute("user_directory");
        System.out.println("file_or_dir_to_delete = " + file_or_dir_to_delete);
        request.setAttribute("after_file_or_dir_delete", new Object());
        if(file_or_dir_to_delete.startsWith(userDirectory.replace("\\", "/"))){
            if (FileManager.deleteFileOrDirectory(file_or_dir_to_delete)) {
                request.setAttribute("delete_success", "删除文件（夹）成功");
            } else{
                request.setAttribute("delete_error", "删除文件（夹）失败");
            }
        } else{
            request.setAttribute("delete_error","拒绝执行删除");
        }
        request.setAttribute("current_dir", PathManager.getParentPath(file_or_dir_to_delete));
        request.getRequestDispatcher("/home").forward(request, response);
    }

}
