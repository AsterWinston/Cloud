package com.aster.cloud.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionManager {
    public static void bindDirectory(HttpServletRequest request){
        //session绑定用户的根目录
        HttpSession session = request.getSession();
        Connection conn = null;
        try {
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement("select dir_name from user where name = ?");
            preparedStatement.setString(1, (String) session.getAttribute("user_name"));
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                String parent = (String) request.getServletContext().getAttribute("file_store_path");
                String child = rs.getString("dir_name");
                Path fullPath = Paths.get(parent, child);
                String fullPathStr = fullPath.toString();
                session.setAttribute("user_directory",fullPathStr);
                System.out.println("session成功绑定用户的目录 = " + fullPathStr);
            } else{
                System.err.println("session绑定用户的目录失败");
            }
        } catch(SQLException e){
            System.err.println("SessionManager中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    public static void bindUserName(HttpServletRequest request, String username){
        HttpSession session = request.getSession();
        session.setAttribute("user_name", username);
    }
}
