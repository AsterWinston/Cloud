package com.aster.cloud.servlet;

import com.aster.cloud.mapper.LoginTokenMapper;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.HttpSessionManager;
import com.aster.cloud.utils.SqlSessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import java.io.IOException;

import static com.aster.cloud.utils.UserManager.isUserExists;

@WebServlet("/resetUserPassword")
public class ResetUserPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过ResetUserPasswordServlet的请求 = " + request.getRequestURI());
        String userName = request.getParameter("user_name");
        String userNewPassword = request.getParameter("user_new_password");

        response.setContentType("application/json");
        JSONObject result = new JSONObject();
        if (resetUserPassword(userName, userNewPassword)) {
            result.put("success", true);
            result.put("message", "重置密码成功");
        } else {
            result.put("success", false);
            result.put("message", "重置密码失败");
        }
        response.getWriter().write(result.toString());
    }
    private boolean resetUserPassword(String userName, String userNewPassword){
        if(!isUserExists(userName)) return false;
        //重设密码
        if (!resetPassword(userName, userNewPassword)) {
            return false;
        }
        //删除token
        if(deleteLoginToken(userName)){
            System.out.println("删除用户loginToken成功");
        } else {
            System.out.println("用户没有loginToken");
        }
        //重置session
        HttpSessionManager.invalidateSession(userName);
        return true;
    }
    private boolean resetPassword(String userName, String userNewPassword){

        SqlSession sqlSession = null;
        try{
            sqlSession = SqlSessionUtils.getSqlSession(false);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int count = userMapper.updatePasswordByName(userName, userNewPassword);
            sqlSession.commit();
            return count == 1;
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("ResetUserPasswordServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
    private boolean deleteLoginToken(String userName) {
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
            int count = loginTokenMapper.deleteByName(userName);
            sqlSession.commit();
            return count == 1;
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("ResetUserPasswordServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
}
