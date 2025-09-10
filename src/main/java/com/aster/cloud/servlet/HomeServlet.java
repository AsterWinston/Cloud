package com.aster.cloud.servlet;

import com.aster.cloud.beans.DirectoryInformation;
import com.aster.cloud.beans.FileOrDirInformation;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.PathManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;


@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        System.out.println("经过HomeServlet的请求 = " + request.getRequestURI());

        String userDirectory = (String) request.getSession().getAttribute("user_directory");
        System.out.println("user_directory = " + userDirectory);

        String currentDirectory = request.getParameter("current_dir");
        System.out.println("odl_current_dir = " + currentDirectory);//未被处理的请求路径

        if(request.getAttribute("after_file_or_dir_delete") != null){
            currentDirectory = (String) request.getAttribute("current_dir");
        } else{
            currentDirectory = getNewCurrentDirectory(currentDirectory, userDirectory);
            System.out.println("new_current_dir = " + currentDirectory);
            request.setAttribute("current_dir", currentDirectory);//当前即将使用请求路径
        }


        DirectoryInformation di = getDI(currentDirectory);//根据当前请求路径返回信息
        request.setAttribute("dir_info", di);

        String relativePath = getRelativePath(userDirectory, currentDirectory);
        System.out.println("relative_path = " + relativePath);
        request.setAttribute("relative_path", relativePath);

        String parentPath = getParentPath(currentDirectory, userDirectory);
        System.out.println("parent_path = " + parentPath);
        request.setAttribute("parent_path", parentPath);

        request.getRequestDispatcher("pages/home/home.jsp").forward(request, response);
    }

    private String getNewCurrentDirectory(String currentDirectory, String userDirectory){
        String newCurrentDirectory = null;
        if(currentDirectory == null){
            newCurrentDirectory =  userDirectory;
        }else {
            //验证是否是用户自己的文件夹
            if (currentDirectory.startsWith(userDirectory.replace("\\", "/")) && FileManager.isPathExists(currentDirectory)) newCurrentDirectory =  currentDirectory;
            else newCurrentDirectory =  userDirectory;
        }
        return newCurrentDirectory.replace("\\", "/");
    }
    private DirectoryInformation getDI(String current_dir){
        DirectoryInformation di = new DirectoryInformation();
        di.setCurrent_dir(current_dir);
        di.setDir_list(FileManager.getFileOrDirInfo(current_dir));
        return di;
    }
    private String getRelativePath(String userDirectory, String currentDirectory){
        String relativePath = PathManager.getRelativePath(userDirectory.replace("\\", "/"), currentDirectory);
        relativePath = relativePath.replace("\\", "/");
        relativePath = "/" + relativePath;
        return relativePath;
    }
    private String getParentPath(String currentDirectory, String userDirectory){
        if(userDirectory.replace("\\", "/").equals(currentDirectory))return currentDirectory;
        File current = new File(currentDirectory);
        return current.getParent().replace("\\", "/");
    }

}
