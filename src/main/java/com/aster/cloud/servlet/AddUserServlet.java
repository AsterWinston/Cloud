package com.aster.cloud.servlet;
import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;

@WebServlet("/addUser")
public class AddUserServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("经过AddUserServlet的请求 = " + request.getRequestURI());

        String userName = request.getParameter("user_name");
        String userPassword = request.getParameter("user_password");
        String limitVolume = request.getParameter("limit_volume");

        JSONObject result = new JSONObject();

        if (createUser(request, userName, userPassword, limitVolume)) {
            result.put("status", "success");
            result.put("message", "用户创建成功");
        } else {
            result.put("status", "error");
            result.put("message", "用户创建失败");
        }

        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }

    public boolean createUser(HttpServletRequest request, String userName, String userPassword, String limitVolume){
        if(userName.length()>64 || userPassword.length()>128)return false;
        if(Integer.parseInt(limitVolume) <= 0)return false;
        if(UserManager.isUserExists(userName))return false;

        SqlSession sqlSession = null;

        try{
            String userDirectoryName = UUIDGenerator.generateUniqueDirectoryName();
            sqlSession = SqlSessionUtils.getSqlSession(false);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = new User(userName, userPassword, Long.parseLong(limitVolume), userDirectoryName, new Date());
            int count = userMapper.insertOne(user);
            sqlSession.commit();
            if(count == 1){
                System.out.println("创建用户" + userName + "成功");
                FileManager.createDirectory(((String) request.getSession().getServletContext().getAttribute("file_store_path")).replace("\\", "/"), userDirectoryName);
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            sqlSession.rollback();
            System.err.println("AddUserServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }

}
