package com.aster.cloud.servlet;

import com.aster.cloud.mapper.LoginTokenMapper;
import com.aster.cloud.utils.HttpSessionManager;
import com.aster.cloud.utils.SqlSessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.ibatis.session.SqlSession;
import java.io.IOException;

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
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
            loginTokenMapper.deleteByName(userName);
            sqlSession.commit();
        } catch (Exception e){
            sqlSession.rollback();
            System.err.println("LogoutServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
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
