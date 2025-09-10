package com.aster.cloud.servlet;

import com.aster.cloud.utils.PathManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("downloadFile")
public class DownloadFileServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String file_path_to_download = request.getParameter("file_path_to_download");
        String userDirectory = (String) request.getSession().getAttribute("user_directory");

        //执行下载文件或目录.zip
        request.setAttribute("current_dir", PathManager.getParentPath(file_path_to_download));
        request.getRequestDispatcher("/home").forward(request, response);
    }
}
