package com.aster.cloud.listener;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.SystemMapper;
import com.aster.cloud.mapper.UserMapper;
import com.aster.cloud.utils.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.apache.ibatis.session.SqlSession;
import java.util.Date;

@WebListener
public class SystemInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("SystemInitListener开始执行");
        ServletContext servletContext = sce.getServletContext();
        //创建存储文件的路径
        FileManager.createDirectories((String) servletContext.getAttribute("file_store_path"));
        String admin_name = (String) servletContext.getAttribute("admin_name");
        String admin_password = (String) servletContext.getAttribute("admin_password");
        // 检查配置是否为空
        if (admin_name == null || admin_password == null) {
            System.err.println("管理员用户名或密码未配置");
            throw new RuntimeException();
        }
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            SystemMapper systemMapper = sqlSession.getMapper(SystemMapper.class);
            systemMapper.createTableUser();
            sqlSession.commit();
            SqlSessionUtils.closeSqlSession();
            System.out.println("表user初始化成功");
            sqlSession = SqlSessionUtils.getSqlSession(true);
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.selectByName(admin_name);
            SqlSessionUtils.closeSqlSession();
            if(user != null){
                //更新密码
                if(admin_password.length() > 128){
                    System.err.println("管理员密码超出128位");
                    throw new RuntimeException();
                }
                sqlSession = SqlSessionUtils.getSqlSession(false);
                userMapper = sqlSession.getMapper(UserMapper.class);
                userMapper.updatePasswordByName(admin_name, admin_password);
                sqlSession.commit();
                SqlSessionUtils.closeSqlSession();
                System.out.println("管理员密码更新成功");
                //创建目录
                FileManager.createDirectory((String)servletContext.getAttribute("file_store_path"), user.getDirName());
                System.out.println("目录" + user.getDirName() + "创建成功");
            } else{
                if(admin_name.length()>64 || admin_password.length()>128){
                    System.err.println("管理员名字或者密码超长，名字限长64位，密码限长128位");
                    throw new RuntimeException();
                }
                String dir_name = UUIDGenerator.generateUniqueDirectoryName();
                sqlSession = SqlSessionUtils.getSqlSession(false);
                userMapper = sqlSession.getMapper(UserMapper.class);
                User admin = new User(admin_name, admin_password, 1024L, dir_name, new Date());
                userMapper.insertOne(admin);
                sqlSession.commit();
                SqlSessionUtils.closeSqlSession();
                FileManager.createDirectory((String)servletContext.getAttribute("file_store_path"), dir_name);
                System.out.println("管理员账户已创建");
            }
            sqlSession = SqlSessionUtils.getSqlSession(false);
            systemMapper = sqlSession.getMapper(SystemMapper.class);
            systemMapper.createTableLoginToken();
            System.out.println("表login_tokens初始化成功");
            systemMapper.createTableLoginLog();
            System.out.println("表login_log初始化成功");
            systemMapper.createTableIpBlackList();
            System.out.println("表ip_black_list初始化成功");
            sqlSession.commit();
        } catch (Exception e){
            sqlSession.rollback();
            System.err.println("SystemInitListener中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }

        System.out.println("SystemInitListener执行成功");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 清理资源代码
    }
}
