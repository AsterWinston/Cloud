package com.aster.cloud.servlet;

import com.aster.cloud.beans.User;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/backend")
public class BackendServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取用户信息
        List<User> userList = getAllUsersInfo();
        Integer userCount = userList.size(); // 获取用户数量

        // 将 count 和 list 作为请求属性传递给页面
        request.setAttribute("user_count", userCount);
        request.setAttribute("user_list", userList);

        // 处理页面跳转
        String destinationPage = request.getParameter("destination_page");
        if(destinationPage == null){
            request.getRequestDispatcher("pages/backend/user_management.jsp").forward(request, response);
        } else if(destinationPage.equals("access_control")){
            request.getRequestDispatcher("pages/backend/access_control.jsp").forward(request, response);
        } else if(destinationPage.equals("login_logs")){
            request.getRequestDispatcher("pages/backend/login_logs.jsp").forward(request, response);
        } else {
            // 默认页面
            request.getRequestDispatcher("pages/backend/user_management.jsp").forward(request, response);
        }
    }

    private List<User> getAllUsersInfo() {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBManager.getConnection();
            String sql = "SELECT name, create_date, limit_volume FROM user";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String userName = rs.getString("name");
                String createDate = rs.getString("create_date");
                String limitVolume = rs.getString("limit_volume");
                User user = new User(userName, createDate, limitVolume);
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("BackendServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return users;
    }
}
