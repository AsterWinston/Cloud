package com.aster.cloud.servlet;

import com.aster.cloud.beans.IP;
import com.aster.cloud.mapper.IpBlackListMapper;
import com.aster.cloud.utils.IPManager;
import com.aster.cloud.utils.SqlSessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/saveBlackList")
public class SaveBlackListServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json;charset=UTF-8");
        JSONObject result = new JSONObject();
        try {
            boolean ok = saveIPBlackList(request.getParameter("ip_black_list"));
            if (ok) {
                result.put("status", "success");
                result.put("message", "保存成功");
            } else {
                result.put("status", "error");
                result.put("message", "输入有误");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "服务器异常: " + e.getMessage());
        }

        try (PrintWriter out = response.getWriter()) {
            out.print(result.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean saveIPBlackList(String IPBlackList) {
        if (IPBlackList == null || !isAllIPLegal(IPBlackList)) {
            return false;
        }

        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            IpBlackListMapper ipBlackListMapper = sqlSession.getMapper(IpBlackListMapper.class);
            ipBlackListMapper.deleteAll();
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("SaveBlackList中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }

        // 按行拆分（兼容 \n 和 \r\n）
        String[] IPs = IPBlackList.split("\\r?\\n");
        for (String IP : IPs) {
            IP = IP.trim();
            if (IP.isEmpty()) continue; // 跳过空行
            if (!saveSingleIP(IP)) {
                return false;
            }
        }
        return true;
    }
    private boolean saveSingleIP(String IP){

        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            IpBlackListMapper ipBlackListMapper = sqlSession.getMapper(IpBlackListMapper.class);
            IP ip = ipBlackListMapper.selectByIP(IP);
            if(ip != null){
                sqlSession.commit();
                return false;
            }
            int count = ipBlackListMapper.insertOne(new IP(IP));
            sqlSession.commit();
            return count == 1;
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("SaveBlacklistServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
    private boolean isAllIPLegal(String IPBlackList){
        // 按行拆分（兼容 \n 和 \r\n）
        String[] IPs = IPBlackList.split("\\r?\\n");
        for (String IP : IPs) {
            IP = IP.trim();
            if (IP.isEmpty()) continue; // 跳过空行
            if (!IPManager.isIPv4(IP) && !IPManager.isIPv6(IP) && !IPManager.isIPv4Cidr(IP) && !IPManager.isIPv6Cidr(IP)) {
                return false;
            }
        }
        return true;
    }
}
