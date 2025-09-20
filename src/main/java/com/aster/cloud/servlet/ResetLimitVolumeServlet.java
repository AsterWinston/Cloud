package com.aster.cloud.servlet;

import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.SqlSessionUtils;
import com.aster.cloud.utils.UserManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import java.io.IOException;

@WebServlet("/resetLimitVolume")
public class ResetLimitVolumeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = request.getParameter("user_name");
        String userNewLimitVolume = request.getParameter("user_new_limit_volume");

        response.setContentType("application/json");
        JSONObject result = new JSONObject();

        if (resetLimitVolume(userName, userNewLimitVolume)) {
            result.put("success", true);
            result.put("message", "重置限制容量成功");
        } else {
            result.put("success", false);
            result.put("message", "重置限制容量失败");
        }

        response.getWriter().write(result.toString());
    }
    private boolean resetLimitVolume(String userName, String userLimitVolume) {
        if (!UserManager.isUserExists(userName)) return false;
        if (Integer.parseInt(userLimitVolume) < 0) return false;

        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            int count = userMapper.updateLimitVolumeByName(Long.parseLong(userLimitVolume), userName);
            sqlSession.commit();
            return count == 1;
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("ResetLimitVolumeServlet中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
}
