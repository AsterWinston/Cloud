package com.aster.cloud.filter;

import com.aster.cloud.beans.IP;
import com.aster.cloud.mapper.IpBlackListMapper;
import com.aster.cloud.utils.IPManager;
import com.aster.cloud.utils.SqlSessionUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebFilter(filterName = "NetFilter")
public class NetFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("经过NetFilter的请求 = "+request.getRequestURI());

        // 设置请求的编码格式
        request.setCharacterEncoding("UTF-8");
        // 获取客户端 IP 地址
        String IP = request.getRemoteAddr();
//        String UA = request.getHeader("User-Agent");
        System.out.println("本次请求ip = " + IP);
        try{
            if(!ip_accessible(IP)){
                request.getRequestDispatcher("pages/login/refuse.jsp").forward(request, response);//加不加/在前面都可以，不像是重定向，必须加
                return;
            }
        } catch (SQLException e){
            request.getRequestDispatcher("pages/sql/error.jsp").forward(request, response);
            System.err.println("NetFilter中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // 如果 IP 不在黑名单中，继续处理请求
        chain.doFilter(request, response);
    }
    private boolean ip_accessible(String IP) throws SQLException {
        // 连接数据库并查询 IP 是否在黑名单中
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        IpBlackListMapper ipBlackListMapper = sqlSession.getMapper(IpBlackListMapper.class);
        List<IP> ipList = ipBlackListMapper.selectAll();
        SqlSessionUtils.closeSqlSession();
        for (IP ip: ipList){
            String blackIP = ip.getIP();
            if(IPManager.isSingleIp(blackIP)){
                if (IPManager.equalsIp(blackIP, IP)) {
                    return false;
                }
            } else {
                if (IPManager.ipInCidr(IP, blackIP)){
                    return false;
                }
            }
        }
        return true;
    }
}
