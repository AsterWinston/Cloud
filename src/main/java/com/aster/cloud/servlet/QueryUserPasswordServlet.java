package com.aster.cloud.servlet;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.SqlSessionUtils;
import com.aster.cloud.utils.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/queryUserPassword")
public class QueryUserPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = request.getParameter("user_name");
        String password = queryUserPassword(userName);

        response.setContentType("application/json");

        JSONObject json = new org.json.JSONObject();
        if (password != null) {
            json.put("success", true);
            json.put("password", password);
            json.put("message", "查询成功");
        } else {
            json.put("success", false);
            json.put("message", "用户不存在或查询失败");
        }

        response.getWriter().write(json.toString());
    }
    private String queryUserPassword(String userName){
        if(!UserManager.isUserExists(userName))return null;
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByName(userName);
        SqlSessionUtils.closeSqlSession();
        if(user != null){
            return user.getPassword();
        } else {
            return null;
        }
    }
}
