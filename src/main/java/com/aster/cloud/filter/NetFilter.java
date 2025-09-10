package com.aster.cloud.filter;

import com.aster.cloud.utils.DBManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//*.*.*.*
@WebFilter(filterName = "NetFilter")
public class NetFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("经过NetFilter的请求 = "+request.getRequestURI());
        // 设置请求的编码格式
        request.setCharacterEncoding("UTF-8");

        // 获取客户端 IP 地址
        String ip = request.getRemoteAddr();
        System.out.println("本次请求ip = " + ip);
        try{
            if(!ip_accessible(ip)){
                request.getRequestDispatcher("pages/login/refuse.jsp").forward(request, response);//加不加/在前面都可以，不像是重定向，必须加
                return;
            }
        } catch (SQLException e){
            System.err.println("NetFilter中出现sql异常");
            request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
            throw new RuntimeException(e);
        }

        // 如果 IP 不在黑名单中，继续处理请求
        chain.doFilter(request, response);
        // 过滤器结束后
    }
    private boolean ip_accessible(String ip) throws SQLException {
        // 连接数据库并查询 IP 是否在黑名单中
        Connection conn = null;
        try {
            PreparedStatement preparedStatement = null;
            String sql = "SELECT ip FROM ip_black_list WHERE ip = ?";
            ResultSet rs = null;

            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, ip);  // 防止 SQL 注入，使用参数化查询

            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                DBManager.closeConnection(conn);
                return false; // 跳出，避免继续执行后续操作
            }
            DBManager.closeConnection(conn);
            return true;
        } catch (SQLException e){
            System.err.println("NetFilter中出现sql异常");
            e.printStackTrace();
            throw e;
        } finally {
            DBManager.closeConnection(conn);
        }

    }
}
