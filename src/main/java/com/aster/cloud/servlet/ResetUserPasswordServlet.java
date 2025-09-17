package com.aster.cloud.servlet;
import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.HttpSessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.aster.cloud.utils.UserManager.isUserExists;

@WebServlet("/resetUserPassword")
public class ResetUserPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过ResetUserPasswordServlet的请求 = " + request.getRequestURI());
        String userName = request.getParameter("user_name");
        String userNewPassword = request.getParameter("user_new_password");

        response.setContentType("application/json");
        JSONObject result = new JSONObject();
        if (resetUserPassword(userName, userNewPassword)) {
            result.put("success", true);
            result.put("message", "重置密码成功");
        } else {
            result.put("success", false);
            result.put("message", "重置密码失败");
        }
        response.getWriter().write(result.toString());
    }
    private boolean resetUserPassword(String userName, String userNewPassword){
        if(!isUserExists(userName)) return false;
        //重设密码
        if (!resetPassword(userName, userNewPassword)) {
            return false;
        }
        //删除token
        if(deleteLoginToken(userName)){
            System.out.println("删除用户loginToken成功");
        } else {
            System.out.println("用户没有loginToken");
        }
        //重置session
        HttpSessionManager.invalidateSession(userName);
        return true;
    }
    private boolean resetPassword(String userName, String userNewPassword){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "update user set password = ? where name = ?";
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userNewPassword);
            preparedStatement.setString(2, userName);
            int count = preparedStatement.executeUpdate();
            conn.commit();
            return count == 1;
        } catch (SQLException e){
            System.err.println("ResetUserPasswordServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return resetUserPassword(userName, userNewPassword);
            } catch (SQLException ex){
                System.err.println("ResetUserPasswordServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
    private boolean deleteLoginToken(String userName) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "delete from login_token where name = ?";
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            int count = preparedStatement.executeUpdate();
            conn.commit();
            return count == 1;
        } catch (SQLException e){
            System.err.println("ResetUserPasswordServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return deleteLoginToken(userName);
            } catch (SQLException ex){
                System.err.println("ResetUserPasswordServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
}
