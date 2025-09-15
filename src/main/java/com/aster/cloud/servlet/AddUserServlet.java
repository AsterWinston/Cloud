package com.aster.cloud.servlet;

import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.UUIDGenerator;
import com.aster.cloud.utils.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

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
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = null;
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            sql = "insert into user values (?, ?, ?, ?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, userPassword);
            preparedStatement.setInt(3, Integer.parseInt(limitVolume));
            String userDirectoryName = UUIDGenerator.generateUniqueDirectoryName();
            preparedStatement.setString(4, userDirectoryName);
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            int count = preparedStatement.executeUpdate();
            conn.commit();
            if(count == 1){
                System.out.println("创建用户" + userName + "成功");
                FileManager.createDirectory(((String) request.getSession().getServletContext().getAttribute("file_store_path")).replace("\\", "/"), userDirectoryName);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e){
            System.err.println("AddUserServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex) {
                System.err.println("AddUserServlet中rollback失败");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }

}
