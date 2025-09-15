package com.aster.cloud.servlet;

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

import java.sql.SQLException;

@WebServlet("/resetLimitVolume")
public class ResetLimitVolumeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = request.getParameter("user_name");
        String userNewLimitVolume = request.getParameter("user_new_limit_volume");

        response.setContentType("application/json");
        JSONObject result = new JSONObject();

        if (resetLimitVolume(userName, userNewLimitVolume)) {
            result.put("success", true);
            result.put("message", "重置限制容量成功");
        } else {
            result.put("success", false);
            result.put("message", "重置限制容量失败");
        }

        response.getWriter().write(result.toString());
    }
    private boolean resetLimitVolume(String userName, String userLimitVolume){
        if(!UserManager.isUserExists(userName))return false;
        if(Integer.parseInt(userLimitVolume) < 0)return false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "update user set limit_volume = ? where name = ?";
        try {
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(userLimitVolume));
            preparedStatement.setString(2, userName);
            int count = preparedStatement.executeUpdate();
            conn.commit();
            return count == 1;
        } catch (SQLException e){
            System.err.println("ResetLimitVolumeServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return resetLimitVolume(userName, userLimitVolume);
            } catch (SQLException ex){
                System.err.println("ResetLimitVolumeServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }

}
