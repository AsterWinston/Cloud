package com.aster.cloud.filter;

import com.aster.cloud.beans.LoginToken;
import com.aster.cloud.mapper.LoginTokenMapper;
import com.aster.cloud.utils.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import org.apache.ibatis.session.SqlSession;
import java.io.IOException;

@WebFilter(filterName = "LoginFilter") // 关键：补充拦截路径
public class LoginFilter extends HttpFilter {
    // 无需拦截的路径（如登录页、静态资源、验证码接口等），避免死循环
    private static final String[] EXCLUDE_URLS = {"login", ".css", ".js"};
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("经过LoginFilter的请求 = " + request.getRequestURI());

        String requestURI = request.getRequestURI();
        if(request_accessible(requestURI)){
            System.out.println("自由访问的资源，直接放行");
            chain.doFilter(request, response);
            return;
        };
        HttpSession session = request.getSession();
        if(session_accessible(session)){
            System.out.println("session认证成功，放行");
            chain.doFilter(request, response);
            return;
        }
        verify_login_token(request, response, chain);

    }
    private boolean request_accessible(String requestURI){
        for(String s: EXCLUDE_URLS) if(requestURI.endsWith(s)){
            return true;
        }
        return false;
    }
    private boolean session_accessible(HttpSession session){
        String loggedInUser = (String) session.getAttribute("user_name");
        return loggedInUser != null;
    }
    private void verify_login_token(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String loginToken = null;
        String contextPath = request.getContextPath();
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie: cookies){
                String name = cookie.getName();
                if(name.equals("login_token"))loginToken = cookie.getValue();
            }
        }
        if (loginToken == null) {
            // 重定向
            System.out.println("请求:"+ request.getRequestURI() + " " +"login_token为空，session认证失败，重定向到/login");
            response.sendRedirect(contextPath+"/login");
            return;
        }
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
        LoginToken newLoginToken = loginTokenMapper.selectByLoginToken(loginToken);
        SqlSessionUtils.closeSqlSession();
        if(newLoginToken != null){
            //携带loginToken
            java.util.Date now = new java.util.Date();
            long createTimeTimeStamp = newLoginToken.getCreateDate().getTime();
            long localTimeTimeStamp = now.getTime();
            if(localTimeTimeStamp - createTimeTimeStamp < 1000 * 3600 * 24 * 10L){
                //loginToken认证成功，session绑定信息
                String userName = newLoginToken.getName();
                SingleSessionManager.bindUserName(request, userName);
                HttpSessionManager.addSession(userName, request.getSession());
                SingleSessionManager.bindDirectory(request);//绑定用户目录，防止查询频繁
                LogManager.loginLogInsert(userName, now, request.getRemoteHost());
                System.out.println("token认证成功，设置session后，放行");

                chain.doFilter(request, response);
            }else {
                int count;
                sqlSession = SqlSessionUtils.getSqlSession(false);
                loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
                try{
                    count = loginTokenMapper.deleteByLoginToken(loginToken);
                    sqlSession.commit();
                } catch (Exception e) {
                    sqlSession.rollback();
                    System.err.println("LoginFilter中出现异常");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    SqlSessionUtils.closeSqlSession();
                }

                if(count == 1) System.out.println("过期token删除成功");
                System.out.println("token过期，重定向到啊/login");
                response.sendRedirect(contextPath+"/login");
            }
        }else{
            System.out.println("token无效，重定向到/login");
            response.sendRedirect(contextPath+"/login");
        }

    }

}