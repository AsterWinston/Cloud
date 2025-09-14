package com.aster.cloud.servlet;

import com.aster.cloud.beans.User;
import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/queryUserPassword")
public class QueryUserPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = request.getParameter("user_name");
        String password = queryUserPassword(userName);

        response.setContentType("application/json");

        JSONObject json = new org.json.JSONObject();
        if (!password.isEmpty()) {
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
        if(!UserManager.isUserExists(userName))return "";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "select password from user where name = ?";
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            rs = preparedStatement.executeQuery();
            if(rs.next()) return rs.getString("password");
            else return "";
        } catch (SQLException e) {
            System.err.println("QueryUserPasswordServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return queryUserPassword(userName);
            } catch (SQLException ex){
                System.err.println("QueryUserPasswordServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
}
