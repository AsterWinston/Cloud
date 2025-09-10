package com.aster.cloud.listener;

import com.aster.cloud.utils.DBUtils;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.UUIDGenerator;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.annotation.WebServlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
            System.out.println("管理员用户名或密码未配置");
            throw new RuntimeException();
        }
        Connection conn = null;
        try {
            PreparedStatement preparedStatement = null;
            ResultSet rs = null;
            System.out.println("正在获取数据库连接...");
            conn = DBUtils.getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("数据库连接失败");
                throw new SQLException();
            }
            System.out.println("数据库连接成功");
            //初始化表user
            String sql = "CREATE TABLE IF NOT EXISTS user (" +
                    "name VARCHAR(64) PRIMARY KEY," +
                    "password VARCHAR(128)," +
                    "limit_volume INT," +
                    "dir_name CHAR(32)," +
                    "create_date DATETIME" +
                    ");";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            System.out.println("表user初始化成功");

            // 查找 admin 用户
            sql = "SELECT * FROM user WHERE name = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, admin_name);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                // 更新密码
                if(admin_password.length() > 128){
                    System.out.println("管理员密码超出128位");
                    throw new RuntimeException();
                }
                sql = "UPDATE user SET password = ? WHERE name = ?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, admin_password);
                preparedStatement.setString(2, admin_name);
                preparedStatement.executeUpdate();
                System.out.println("管理员密码已更新");
                sql = "select dir_name from user where name = ?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, admin_name);
                rs = preparedStatement.executeQuery();
                rs.next();
                String dir_name = rs.getString("dir_name");
                FileManager.createDirectory((String)servletContext.getAttribute("file_store_path"), dir_name);
            } else {
                // 插入 admin 用户
                if(admin_name.length()>64 || admin_password.length()>128){
                    System.out.println("管理员名字或者密码超长，名字限长64位，密码限长128位");
                    throw new RuntimeException();
                }
                sql = "INSERT INTO user (name, password, limit_volume, dir_name, create_date) VALUES (?, ?, ?, ?, ?)";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, admin_name);
                preparedStatement.setString(2, admin_password);
                preparedStatement.setInt(3, 1024); // 默认限制量
                String dir_name = UUIDGenerator.generateUniqueDirectoryName();
                preparedStatement.setString(4, dir_name); // 默认目录名
                FileManager.createDirectory((String)servletContext.getAttribute("file_store_path"), dir_name);
                preparedStatement.setString(5, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                preparedStatement.executeUpdate();
                System.out.println("管理员账户已创建");
            }

            //初始化login_tokens
            sql = "CREATE TABLE if not exists login_tokens (\n" +
                    "    name VARCHAR(64),  \n" +
                    "    login_token CHAR(255) PRIMARY KEY,  \n" +
                    "    create_date DATETIME,  \n" +
                    "    FOREIGN KEY (name) REFERENCES user(name)  \n" +
                    ");";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            System.out.println("表login_tokens初始化成功");

            //初始化表ip_black_list
            sql = "create table if not exists ip_black_list(\n" +
                    "\tip varchar(39) primary key\n" +
                    ");";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            System.out.println("表ip_black_list初始化成功");

            //初始化表login_log
            sql = "CREATE TABLE if not exists login_log (\n" +
                    "   id INT AUTO_INCREMENT PRIMARY KEY,   \n" +
                    "   name VARCHAR(64),                 \n" +
                    "   login_time DATETIME,              \n" +
                    "   login_ip VARCHAR(39),          \n" +
                    "   FOREIGN KEY (name) REFERENCES user(name)    \n" +
                    ");";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
            System.out.println("表login_log初始化成功");


        } catch (SQLException e) {
            System.out.println("SystemInitListener中出现sql异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }
        System.out.println("SystemInitListener执行成功");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 清理资源代码
    }
}
