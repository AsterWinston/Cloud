package com.aster.cloud.servlet;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.LoginLogMapper;
import com.aster.cloud.mapper.LoginTokenMapper;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.SqlSessionUtils;
import com.aster.cloud.utils.UserManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;

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
        if (!UserManager.isUserExists(userName)) return false;
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(false);
        LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
        LoginLogMapper loginLogMapper = sqlSession.getMapper(LoginLogMapper.class);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        try{
            loginTokenMapper.deleteByName(userName);
            loginLogMapper.deleteByName(userName);
            User user = userMapper.selectByName(userName);
            if (user != null) {
                FileManager.deleteFileOrDirectory(((String) request.getSession().getServletContext().getAttribute("file_store_path")).replace("\\", "/") + "/" + user.getDirName());
                userMapper.deleteByName(user.getName());
                sqlSession.commit();
                return true;
            } else {
                sqlSession.commit();
                return false;
            }
        } catch (Exception e){
            sqlSession.rollback();
            System.err.println("DeleteServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
}
