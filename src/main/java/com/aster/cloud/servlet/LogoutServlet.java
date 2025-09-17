package com.aster.cloud.servlet;
import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.HttpSessionManager;
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
            throws IOException {
        System.out.println("经过LogoutServlet的请求 = " + request.getRequestURI());
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("username");
        deleteLoginToken(userName);
        deleteCookieLoginToken(response);
        //销毁session和存储的session
        HttpSessionManager.invalidateSession(userName);
        session.invalidate();
        response.sendRedirect(request.getContextPath() + "/login");
    }
    private void deleteLoginToken(String userName){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "delete from login_token where name = ?";
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            int count = preparedStatement.executeUpdate();
            if(count == 1){
                System.out.println("LogoutServlet中删除login_token成功");
            } else {
                System.out.println("LogoutServlet中删除login_token失败或无login_token");
            }
            conn.commit();
        } catch (SQLException e){
            System.err.println("Logout中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                deleteLoginToken(userName);
            } catch (SQLException ex){
                System.err.println("LogoutServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
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
