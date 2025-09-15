package com.aster.cloud.servlet;

import com.aster.cloud.utils.DBManager;
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
            throws ServletException, IOException {
        System.out.println("经过ResetPasswordServlet的请求" + request.getRequestURI());
        String old_password = request.getParameter("old_password"), new_password = request.getParameter("new_password");
        if(old_password != null && new_password != null){
            if (resetPassword((String) request.getSession().getAttribute("user_name"), old_password, new_password)) {
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
    private boolean resetPassword(String userName, String oldPassword, String newPassword) {
        if(newPassword.length() > 128) return false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = null;
        ResultSet rs = null;
        try{
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            sql = "select * from user where name = ? and password = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, oldPassword);
            rs = preparedStatement.executeQuery();
            if(!rs.next()){
                System.err.println("验证原账号密码失败");
                return false;
            }
            sql = "update user set password = ? where name = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, userName);
            int count = preparedStatement.executeUpdate();
            conn.commit();
            if(count == 0){
                System.err.println("更新用户"+ userName +"的密码失败");
                return false;
            } else {
                System.out.println("更新用户" + userName + "的密码成功");
                return true;
            }
        } catch (SQLException e){
            System.err.println("ResetPasswordServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return resetPassword(userName, oldPassword, newPassword);
            } catch (SQLException ex){
                System.err.println("ResetPasswordServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }

    }
}
