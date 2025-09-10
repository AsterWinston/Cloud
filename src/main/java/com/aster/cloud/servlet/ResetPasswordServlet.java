package com.aster.cloud.servlet;

import com.aster.cloud.utils.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        System.out.println("经过resetPassword的请求" + request.getRequestURI());
        String old_password = (String) request.getParameter("old_password"), new_password = (String) request.getParameter("new_password");
        if(old_password != null && new_password != null){
            if (resetPassword((String) request.getSession().getAttribute("username"), old_password, new_password)) {
                request.setAttribute("reset_password_success", "重置密码成功");
                request.getRequestDispatcher("pages/other/reset_password.jsp").forward(request, response);
            } else {
                request.setAttribute("reset_password_fail", "验证旧的密码失败，请检查输入");
                request.getRequestDispatcher("pages/other/reset_password.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("pages/other/reset_password.jsp").forward(request, response);
        }

    }
    private boolean resetPassword(String username, String old_password, String new_password) {
        if(new_password.length() > 128) return false;
        Connection conn = null;
        try{
            PreparedStatement preparedStatement = null;
            String sql = "select * from user where name = ? and password = ?";
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, old_password);
            ResultSet rs = preparedStatement.executeQuery();
            if(!rs.next()){
                System.out.println("验证原账号密码失败");
                return false;
            }
            sql = "update user set password = ? where name = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, new_password);
            preparedStatement.setString(2, username);
            int count = preparedStatement.executeUpdate();
            if(count == 0){
                System.out.println("更新用户"+ username +"的密码失败");
                return false;
            }
            System.out.println("更新用户" + username + "的密码成功");
            return true;
        } catch (SQLException e){
            System.out.println("ResetPasswordServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }

    }
}
