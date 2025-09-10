package com.aster.cloud.servlet;

import com.aster.cloud.utils.DBUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过logout的请求 = " + request.getRequestURI());
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        deleteLoginToken(username);
        deleteCookieLoginToken(response);
        session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login");
    }
    private void deleteLoginToken(String username){
        Connection conn = null;
        try {
            PreparedStatement preparedStatement = null;
            String sql = "delete from login_tokens where name = ?";
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            int count = preparedStatement.executeUpdate();
            if(count == 1){
                System.out.println("LogoutServlet中删除login_token成功");
            } else {
                System.out.println("LogoutServlet中删除login_token失败");
            }

        } catch (SQLException e){
            System.out.println("Logout中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }
    }
    private void deleteCookieLoginToken(HttpServletResponse response){
        Cookie cookie = new Cookie("login_token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println("删除浏览器cookie成功");
    }

}
