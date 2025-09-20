package com.aster.cloud.servlet;

import com.aster.cloud.beans.IP;
import com.aster.cloud.beans.LoginLog;
import com.aster.cloud.beans.LoginLogPageResult;
import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.IpBlackListMapper;
import com.aster.cloud.mapper.LoginLogMapper;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.LogManager;
import com.aster.cloud.utils.SqlSessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/backend")
public class BackendServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过BackendServlet的请求 = " + request.getRequestURI());

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
                LogManager.deleteLogById(Long.parseLong(request.getParameter("log_id")));
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
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        users = userMapper.selectAll();
        SqlSessionUtils.closeSqlSession();
        return users;
    }
    private LoginLogPageResult getLoginLogs(int itemCountEveryPage, int pageCount) {
        List<LoginLog> loginLogList = new ArrayList<>();
        int totalCount = 0;

        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        LoginLogMapper loginLogMapper = sqlSession.getMapper(LoginLogMapper.class);
        totalCount = loginLogMapper.selectCountOfLoginLog();
        int maxPage = (int) Math.ceil((double) totalCount / itemCountEveryPage);
        if (pageCount >= 1 && pageCount <= maxPage) {
            int offset = (pageCount - 1) * itemCountEveryPage;
            loginLogList = loginLogMapper.selectLoginLogByPage(offset, itemCountEveryPage);
        }
        SqlSessionUtils.closeSqlSession();
        return new LoginLogPageResult(totalCount, loginLogList);
    }
    private String getIPBlackList(){
        StringBuilder IPBlackList = new StringBuilder();
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        IpBlackListMapper ipBlackListMapper = sqlSession.getMapper(IpBlackListMapper.class);
        List<IP> ipList = ipBlackListMapper.selectAll();
        SqlSessionUtils.closeSqlSession();
        for (IP ip: ipList){
            IPBlackList.append(ip.getIP()).append("\n");
        }
        return IPBlackList.toString();
    }
}
