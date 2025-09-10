package com.aster.cloud.servlet;

import com.aster.cloud.utils.RandomTokenGenerator;
import com.aster.cloud.utils.DBUtils;
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
        String name = request.getParameter("name");
        String password = request.getParameter("password");

        Connection conn = null;
        // 判断用户名和密码是否存在
        if (name != null && password != null) {
            try {
                PreparedStatement preparedStatement = null;
                ResultSet rs = null;
                String sql = "SELECT name, password FROM user WHERE name = ? AND password = ?";
                conn = DBUtils.getConnection();
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, password);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    // 登录成功，设置 session
                    HttpSession session = request.getSession();
                    session.setAttribute("username", name);  // 保存用户名到 session
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String nowTime = LocalDateTime.now().format(formatter);

                    loginLogInsert(name, nowTime, request.getRemoteHost());
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
                    request.setAttribute("loginError", "账号或者密码错误，请重试");
                    // 转发到登录页面
                    request.getRequestDispatcher("pages/login/login.jsp").forward(request, response);
                }
            } catch (SQLException e) {
                request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
                System.out.println("LoginServlet出现sql异常");
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                // 关闭数据库连接等资源
                DBUtils.closeConnection(conn);
            }
        }else{
            System.out.println("账号或者密码为空，登陆失败，转发到pages/login/login.jsp");
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
            String sqlDelete = "delete from login_tokens where name = ?",sqlInsert = "insert into login_tokens values (?, ?, ?)";
            HttpSession session = request.getSession();
            int count;
            conn = DBUtils.getConnection();
            //清理旧的login_token
            preparedStatement = conn.prepareStatement(sqlDelete);
            preparedStatement.setString(1, (String) request.getSession().getAttribute("username"));
            count = preparedStatement.executeUpdate();
            if(count != 0) System.out.println("清理了旧的token");

            preparedStatement = conn.prepareStatement(sqlInsert);
            preparedStatement.setString(1, (String) session.getAttribute("username"));
            preparedStatement.setString(2, login_token);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            preparedStatement.setString(3, LocalDateTime.now().format(formatter));
            count = preparedStatement.executeUpdate();
            if(count == 1)System.out.println("存储login_token成功");
            else System.out.println("存储login_token失败");
        } catch (SQLException e) {
            request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
            System.out.println("LoginServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }
    }
    private boolean login_token_is_repetitive(String login_token) {
        Connection conn = null;
        try{
            PreparedStatement preparedStatement = null;
            String sql = "select * from login_tokens where login_token = ?";
            ResultSet rs = null;
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, login_token);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                return true;
            }
            return false;
        } catch (SQLException e){
            System.out.println("LoginServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }


    }
    private void loginLogInsert(String name, String loginTime, String ip){
        Connection conn = null;
        try{
            PreparedStatement preparedStatement = null;
            String sql = "insert into login_log (name, login_time, login_ip) values (?, ?, ?)";
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, loginTime);
            preparedStatement.setString(3, ip);
            int count = preparedStatement.executeUpdate();
            if(count == 1){
                System.out.println("LoginServlet中记录登陆成功");
            } else{
                System.out.println("LoginServlet中记录登陆失败");
            }

        } catch (SQLException e){
            System.out.println("LoginFilter中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }
    }
}
