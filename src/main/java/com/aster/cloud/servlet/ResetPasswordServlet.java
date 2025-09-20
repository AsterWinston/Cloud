package com.aster.cloud.servlet;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.SqlSessionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import java.io.IOException;


@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("经过ResetPasswordServlet的请求" + request.getRequestURI());
        String old_password = request.getParameter("old_password"), new_password = request.getParameter("new_password");
        if(old_password != null && new_password != null){
            if (resetPassword((String) request.getSession().getAttribute("user_name"), old_password, new_password)) {
                request.setAttribute("reset_password_success", "重置密码成功");
                request.getRequestDispatcher("pages/other/reset_password.jsp").forward(request, response);
            } else {
                request.setAttribute("reset_password_fail", "验证旧的密码失败，请检查输入");
                request.getRequestDispatcher("pages/other/reset_password.jsp").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("pages/other/reset_password.jsp").forward(request, response);
        }

    }
    private boolean resetPassword(String userName, String oldPassword, String newPassword) {
        if(newPassword.length() > 128) return false;

        SqlSession sqlSession = null;
        try{
            sqlSession = SqlSessionUtils.getSqlSession(false);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectByNameAndPassword(userName, oldPassword);
            if(user == null){
                System.err.println("验证原账号密码失败");
                sqlSession.commit();
                return false;
            }
            int count = userMapper.updatePasswordByName(userName, newPassword);
            sqlSession.commit();
            return count == 1;
        } catch (Exception e){
            sqlSession.rollback();
            System.err.println("ResetPasswordServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
}
