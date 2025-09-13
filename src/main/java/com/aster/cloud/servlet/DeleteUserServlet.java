package com.aster.cloud.servlet;

import com.aster.cloud.utils.DBManager;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.UserManager;
import jakarta.servlet.ServletException;
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

@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("经过DeleteUserServlet的请求 = " + request.getRequestURI());
        // 设置响应类型为 JSON
        PrintWriter out = response.getWriter();

        JSONObject result = new JSONObject();

        try {
            String userName = request.getParameter("user_name");

            if (userName == null || userName.trim().isEmpty()) {
                result.put("status", "error");
                result.put("message", "用户名不能为空");
            } else if (userName.equals(request.getServletContext().getAttribute("admin_name"))) {
                result.put("status", "error");
                result.put("message", "不能删除管理员用户！");
            } else if (deleteUser(request, userName)) {
                result.put("status", "success");
                result.put("message", "用户删除成功");
            } else {
                result.put("status", "error");
                result.put("message", "用户删除失败，可能不存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "服务器异常：" + e.getMessage());
        }

        out.write(result.toString());
        out.flush();
    }

    /**
     * 执行数据库删除操作
     */
    private boolean deleteUser(HttpServletRequest request, String userName) {
        if(!UserManager.isUserExists(userName))return false;
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBManager.getConnection();
            String sql = "delete from login_token where name = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
            sql = "delete from login_log where name = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
            sql = "select dir_name from user where name = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                String userDirectoryName = rs.getString("dir_name");
                FileManager.deleteFileOrDirectory(((String)request.getSession().getServletContext().getAttribute("file_store_path")).replace("\\", "/") + "/" + userDirectoryName);
                sql = "delete from user where name = ?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, userName);
                preparedStatement.executeUpdate();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e){
            System.err.println("DeleteUserServlet中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBManager.closeConnection(conn);
        }
    }
}
