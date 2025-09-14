package com.aster.cloud.servlet;

import com.aster.cloud.beans.LoginLog;
import com.aster.cloud.beans.LoginLogPageResult;
import com.aster.cloud.beans.User;
import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.LogManager;
import com.mysql.cj.log.Log;
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
        System.out.println("经过BackendServlet的请求 = " + request.getRequestURI());
        // 获取用户信息

        // 处理页面跳转
        String destinationPage = request.getParameter("destination_page");
        if(destinationPage == null){
            List<User> userList = getAllUsersInfo();
            Integer userCount = userList.size(); // 获取用户数量

            // 将 count 和 list 作为请求属性传递给页面
            request.setAttribute("user_count", userCount);
            request.setAttribute("user_list", userList);
            request.getRequestDispatcher("pages/backend/user_management.jsp").forward(request, response);
        } else if(destinationPage.equals("access_control")){
            String ipBlackList = getIPBlackList();
            request.setAttribute("ip_black_list", ipBlackList);
            request.getRequestDispatcher("pages/backend/access_control.jsp").forward(request, response);
        } else if(destinationPage.equals("login_log")){
            String function = request.getParameter("function");
            if(function == null){
                request.setAttribute("item_count_every_page", "10");
                request.setAttribute("page_count", "1");

            } else if(function.equals("reset_item_count_every_page")) {
                request.setAttribute("item_count_every_page", request.getParameter("item_count_every_page"));
                request.setAttribute("page_count", "1");
            } else if(function.equals("clear_log")){
                LogManager.clearLog();
                request.setAttribute("item_count_every_page", request.getParameter("item_count_every_page"));
                request.setAttribute("page_count", "1");
            } else if(function.equals("switch_page")){
                request.setAttribute("item_count_every_page", request.getParameter("item_count_every_page"));
                request.setAttribute("page_count", request.getParameter("page_count"));
            } else if(function.equals("delete_log")) {
                LogManager.deleteLogById(request.getParameter("log_id"));
                request.setAttribute("item_count_every_page", request.getParameter("item_count_every_page"));
                request.setAttribute("page_count", request.getParameter("page_count"));
            } else {
                return;
            }
            int itemCountEveryPage = Integer.parseInt((String) request.getAttribute("item_count_every_page"));
            int pageCount = Integer.parseInt((String) request.getAttribute("page_count"));
            LoginLogPageResult loginLogPageResult = getLoginLogs(itemCountEveryPage, pageCount);
            List<LoginLog> loginLogList = loginLogPageResult.getLoginLogs();
            Integer totalCount = loginLogPageResult.getTotalCount();
            request.setAttribute("login_log_list", loginLogList);
            request.setAttribute("total_count", totalCount);

            request.getRequestDispatcher("pages/backend/login_log.jsp").forward(request, response);
        } else {
            // 默认页面
            List<User> userList = getAllUsersInfo();
            Integer userCount = userList.size(); // 获取用户数量

            // 将 count 和 list 作为请求属性传递给页面
            request.setAttribute("user_count", userCount);
            request.setAttribute("user_list", userList);
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
        } finally {
            DBManager.closeConnection(conn);
        }
        return users;
    }
    private LoginLogPageResult getLoginLogs(int itemCountEveryPage, int pageCount) {
        List<LoginLog> loginLogList = new ArrayList<>();
        int totalCount = 0;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            conn = DBManager.getConnection();

            // 先查总数
            String countSql = "SELECT COUNT(*) FROM login_log";
            preparedStatement = conn.prepareStatement(countSql);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                totalCount = rs.getInt(1);
            }
            int maxPage = (int) Math.ceil((double) totalCount / itemCountEveryPage);

            if (pageCount >= 1 && pageCount <= maxPage) {
                int offset = (pageCount - 1) * itemCountEveryPage;
                String sql = "SELECT id, name, login_time, login_ip FROM login_log " +
                        "ORDER BY login_time DESC LIMIT ?, ?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setInt(1, offset);
                preparedStatement.setInt(2, itemCountEveryPage);
                rs = preparedStatement.executeQuery();

                while (rs.next()) {
                    LoginLog log = new LoginLog();
                    log.setId(String.valueOf(rs.getInt("id")));
                    log.setName(rs.getString("name"));
                    log.setLoginTime(rs.getString("login_time"));
                    log.setLoginIP(rs.getString("login_ip"));
                    loginLogList.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("BackendServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBManager.closeConnection(conn);
        }

        return new LoginLogPageResult(totalCount, loginLogList);
    }
    private String getIPBlackList(){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "select * from ip_black_list";
        ResultSet rs = null;
        StringBuilder IPBlackList = new StringBuilder();
        try {
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            while(rs.next()){
                IPBlackList.append(rs.getString("ip")).append("\n");
            }
            return IPBlackList.toString();
        } catch (SQLException e){
            System.err.println("BackendServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBManager.closeConnection(conn);
        }
    }
}
