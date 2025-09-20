package com.aster.cloud.utils;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.session.SqlSession;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SingleSessionManager {
    public static void bindDirectory(HttpServletRequest request){
        //session绑定用户的根目录
        HttpSession session = request.getSession();
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByName((String) session.getAttribute("user_name"));
        SqlSessionUtils.closeSqlSession();
        if(user != null){
            String parent = (String) request.getServletContext().getAttribute("file_store_path");
            String child = user.getDirName();
            Path fullPath = Paths.get(parent, child);
            String fullPathStr = fullPath.toString();
            session.setAttribute("user_directory",fullPathStr);
            System.out.println("session成功绑定用户的目录 = " + fullPathStr);
        } else {
            System.err.println("session绑定用户的目录失败");
        }

    }
    public static void bindUserName(HttpServletRequest request, String username){
        HttpSession session = request.getSession();
        session.setAttribute("user_name", username);
    }
}
