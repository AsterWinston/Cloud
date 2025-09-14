package com.aster.cloud.servlet;

import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.IPManager;
import com.mysql.cj.x.protobuf.MysqlxSession;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        if (IPBlackList == null || IPBlackList.isEmpty() || !isAllIPLegal(IPBlackList)) {
            return false; // 空字符串直接返回
        }
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "delete from ip_black_list";
        try {
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.err.println("SaveBlackList中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBManager.closeConnection(conn);
        }
        // 按行拆分（兼容 \n 和 \r\n）
        String[] IPs = IPBlackList.split("\\r?\\n");
        for (String IP : IPs) {
            IP = IP.trim();
            if (IP.isEmpty()) continue; // 跳过空行
            saveSingleIP(IP);
        }
        return true;
    }
    private void saveSingleIP(String IP){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql;
        ResultSet rs = null;
        try {
            conn = DBManager.getConnection();
            sql = "select * from ip_black_list where ip = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, IP);
            rs = preparedStatement.executeQuery();
            if(rs.next())return;
            sql = "insert into ip_black_list values (?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, IP);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.err.println("SaveBlackListServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBManager.closeConnection(conn);
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
