package com.aster.cloud.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtils {

    // 声明数据源
    private static HikariDataSource dataSource;

    // 私有化构造函数，防止外部实例化
    private DBUtils() {}

    // 静态代码块：在类加载时进行配置
    static {
        InputStream inputStream = null;
        try {
            // 读取 db.properties 配置文件
            inputStream = DBUtils.class.getClassLoader().getResourceAsStream("conf/db.properties");
            if (inputStream == null) {
                System.out.println("DBUtiles中配置文件'db.properties'未找到");
                throw new IOException();
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            // 配置 HikariCP 数据源
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.username"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driverClassName"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.maxPoolSize")));

            // 额外的优化配置
            config.setConnectionTimeout(30000);  // 设置连接获取超时为 30 秒
            config.setIdleTimeout(600000);  // 设置空闲连接超时为 10 分钟
            config.setMaxLifetime(1800000);  // 设置最大生命周期为 30 分钟


            // 初始化数据源
            dataSource = new HikariDataSource(config);
            System.out.println("数据库连接池初始化成功");

        } catch (IOException e) {
            System.out.println("DBUtils中加载配置文件失败");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("DBUtils中关闭配置文件输入流失败");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            System.out.println("数据库连接池未初始化");
            throw new SQLException();
        }
        return dataSource.getConnection();
    }

    // 关闭连接（连接池会自动管理连接）
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();  // 归还连接池
            } catch (SQLException e) {
                System.out.println("关闭数据库连接失败");
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
