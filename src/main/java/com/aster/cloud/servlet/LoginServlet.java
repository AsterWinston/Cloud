package com.aster.cloud.servlet;

import com.aster.cloud.utils.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过LoginServlet的请求 = " + request.getRequestURI());
        // 获取表单提交的用户名和密码
        String userName = request.getParameter("name");
        String password = request.getParameter("password");

        Connection conn = null;
        // 判断用户名和密码是否存在
        if (userName != null && password != null) {
            try {
                PreparedStatement preparedStatement = null;
                ResultSet rs = null;
                String sql = "SELECT name, password FROM user WHERE name = ? AND password = ?";
                conn = DBManager.getConnection();
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, password);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    // 登录成功，session绑定用户的名字
                    SingleSessionManager.bindUserName(request, userName);// 保存用户名到 session
                    SingleSessionManager.bindDirectory(request);
                    HttpSessionManager.addSession(userName, request.getSession());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String nowTime = LocalDateTime.now().format(formatter);

                    LogManager.loginLogInsert(userName, nowTime, request.getRemoteHost());
                    //判断是否要存token
                    Object obj = request.getParameter("rememberMe");
                    if(obj != null) {
                        System.out.println("返回token，存储token");
                        return_and_store_login_token(request, response);
                    }
                    System.out.println("账号密码认证成功，定向到home");
                    response.sendRedirect(request.getContextPath() + "/home");
                } else{
                    //绑定原来输入的数据，保持登陆
                    System.out.println("账号或密码验证失败，登陆失败，转发到pages/login/login.jsp");
                    request.setAttribute("login_error", "账号或者密码错误，请重试");
                    // 转发到登录页面
                    request.getRequestDispatcher("pages/login/login.jsp").forward(request, response);
                }
            } catch (SQLException e) {
                request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
                System.err.println("LoginServlet出现sql异常");
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException ex){
                    System.err.println("LoginServlet中出现rollback异常");
                    e.printStackTrace();
                    throw new RuntimeException(ex);
                }
            } finally {
                // 关闭数据库连接等资源
                DBManager.closeConnection(conn);
            }
        }else{
            //首次访问走这
            request.getRequestDispatcher("pages/login/login.jsp").forward(request, response);
        }
    }
    private void return_and_store_login_token(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean flag = true;
        String login_token = "";
        while(flag){
            login_token = RandomTokenGenerator.generateRandomToken(255);
            if(!login_token_is_repetitive(login_token))flag = false;
        }
        Cookie cookie = new Cookie("login_token", login_token);
        cookie.setMaxAge(3600*24*10);
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println("返回login_token成功");

        Connection conn = null;
        try{
            PreparedStatement preparedStatement = null;
            String sqlDelete = "delete from login_token where name = ?", sqlInsert = "insert into login_token values (?, ?, ?)";
            HttpSession session = request.getSession();
            int count;
            conn = DBManager.getConnection();
            //清理旧的login_token
            preparedStatement = conn.prepareStatement(sqlDelete);
            preparedStatement.setString(1, (String) request.getSession().getAttribute("user_name"));
            count = preparedStatement.executeUpdate();
            if(count != 0) System.out.println("清理了旧的token");

            preparedStatement = conn.prepareStatement(sqlInsert);
            preparedStatement.setString(1, (String) session.getAttribute("user_name"));
            preparedStatement.setString(2, login_token);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            preparedStatement.setString(3, LocalDateTime.now().format(formatter));
            count = preparedStatement.executeUpdate();
            if(count == 1)System.out.println("存储login_token成功");
            else System.err.println("存储login_token失败");
        } catch (SQLException e) {
            request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
            System.err.println("LoginServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return_and_store_login_token(request, response);
            } catch (SQLException ex){
                System.err.println("LoginServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
    private boolean login_token_is_repetitive(String login_token) {
        Connection conn = null;
        try{
            PreparedStatement preparedStatement = null;
            String sql = "select * from login_token where login_token = ?";
            ResultSet rs = null;
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, login_token);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                return true;
            }
            return false;
        } catch (SQLException e){
            System.err.println("LoginServlet中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return login_token_is_repetitive(login_token);
            } catch (SQLException ex){
                System.err.println("LoginServlet中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }

    }
}
