package com.aster.cloud.servlet;

import com.aster.cloud.beans.LoginToken;
import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.LoginTokenMapper;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.ibatis.session.SqlSession;
import java.io.IOException;
import java.util.Date;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过LoginServlet的请求 = " + request.getRequestURI());
        // 获取表单提交的用户名和密码
        String userName = request.getParameter("name");
        String password = request.getParameter("password");


        if(userName != null && password != null){
            SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectByNameAndPassword(userName, password);
            SqlSessionUtils.closeSqlSession();
            if(user != null){
                SingleSessionManager.bindUserName(request, userName);// 保存用户名到 session
                SingleSessionManager.bindDirectory(request);
                HttpSessionManager.addSession(userName, request.getSession());
                LogManager.loginLogInsert(userName, new Date(), request.getRemoteHost());
                Object obj = request.getParameter("rememberMe");
                if(obj != null) {
                    System.out.println("返回token，存储token");
                    return_and_store_login_token(request, response);
                }
                System.out.println("账号密码认证成功，定向到home");
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                //绑定原来输入的数据，保持登陆
                System.out.println("账号或密码验证失败，登陆失败，转发到pages/login/login.jsp");
                request.setAttribute("login_error", "账号或者密码错误，请重试");
                // 转发到登录页面
                request.getRequestDispatcher("pages/login/login.jsp").forward(request, response);
            }
        } else {
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

        SqlSession sqlSession = null;
        try {
            HttpSession session = request.getSession();
            sqlSession = SqlSessionUtils.getSqlSession(false);
            LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
            loginTokenMapper.deleteByName((String) request.getSession().getAttribute("user_name"));
            loginTokenMapper.insertOne(new LoginToken((String) session.getAttribute("user_name"), login_token, new Date()));
            sqlSession.commit();
        } catch (Exception e){
            sqlSession.rollback();
            System.err.println("LoginServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
    private boolean login_token_is_repetitive(String loginToken) {
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
        LoginToken newLoginToken = loginTokenMapper.selectByLoginToken(loginToken);
        SqlSessionUtils.closeSqlSession();
        return newLoginToken != null;
    }
}
