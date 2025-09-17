package com.aster.cloud.filter;
import com.aster.cloud.utils.HttpSessionManager;
import com.aster.cloud.utils.SingleSessionManager;
import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.LogManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


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
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String sql = null;
        try{
            conn = DBManager.getConnection();
            conn.setAutoCommit(false);
            sql = "SELECT name, create_date FROM login_token WHERE login_token = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, loginToken);
            rs = preparedStatement.executeQuery();
            if(rs.next()){
                //携带loginToken
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime createTime = LocalDateTime.parse(rs.getString("create_date"), formatter);
                long createTimeTimeStamp = createTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
                long localTimeTimeStamp = LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
                if(localTimeTimeStamp-createTimeTimeStamp<1000*3600*24*10){
                    conn.commit();
                    //loginToken认证成功，session绑定信息
                    String userName = rs.getString("name");
                    SingleSessionManager.bindUserName(request, userName);
                    HttpSessionManager.addSession(userName, request.getSession());
                    SingleSessionManager.bindDirectory(request);//绑定用户目录，防止查询频繁
                    String nowTime = LocalDateTime.now().format(formatter);
                    LogManager.loginLogInsert(userName, nowTime, request.getRemoteHost());
                    System.out.println("token认证成功，设置session后，放行");

                    chain.doFilter(request, response);
                }else {

                    sql = "delete from login_token where login_token = ?";
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, loginToken);
                    int count = preparedStatement.executeUpdate();
                    conn.commit();
                    if(count == 1) System.out.println("过期token删除成功");
                    System.out.println("token过期，重定向到啊/login");
                    response.sendRedirect(contextPath+"/login");
                }
            }else{
                conn.commit();
                System.out.println("token无效，重定向到/login");
                response.sendRedirect(contextPath+"/login");
            }

        } catch (SQLException e) {
            request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
            System.err.println("LoginFilter中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                verify_login_token(request, response, chain);
            } catch (SQLException ex){
                System.err.println("LoginFilter中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }

    }

}