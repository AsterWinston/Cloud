package com.aster.cloud.utils;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleanupTasker {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);


    // 定时任务：清理过期内容
    public static void startLoginTokenCleanupTask() {
        Runnable cleanupTask = new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                String sql = "delete from login_token where create_date < ?";
                PreparedStatement preparedStatement = null;
                long currentTimeMillis = System.currentTimeMillis();
                long tenDaysAgoMillis = currentTimeMillis - TimeUnit.DAYS.toMillis(10);
                Date tenDaysAgo = new Date(tenDaysAgoMillis);
                // 将日期格式化为字符串，符合数据库要求的格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String tenDaysAgoFormatted = sdf.format(tenDaysAgo);
                try {
                    conn = DBManager.getConnection();
                    conn.setAutoCommit(false);
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, tenDaysAgoFormatted);
                    int count = preparedStatement.executeUpdate();
                    System.out.println(("清理了" + count + "个过期login token(s)"));
                    conn.commit();
                } catch (SQLException e) {
                    System.err.println("CleanupTasker中出现sql异常");
                    e.printStackTrace();
                    try {
                        conn.rollback();
                        startLoginTokenCleanupTask();
                    } catch (SQLException ex){
                        System.err.println("CleanupTasker中出现rollback异常");
                        e.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                } finally {
                    DBManager.closeConnection(conn);
                }
            }
        };

        scheduler.scheduleAtFixedRate(cleanupTask, 1, 24 * Integer.parseInt(getConfigValue("login_token_clean_task_execution_interval")), TimeUnit.HOURS);
    }
    public static void startLoginLogCleanTask(){
        Runnable cleanupTask = new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                String sql = "delete from login_log";
                PreparedStatement preparedStatement = null;

                try {
                    conn = DBManager.getConnection();
                    conn.setAutoCommit(false);
                    preparedStatement = conn.prepareStatement(sql);
                    int count = preparedStatement.executeUpdate();
                    System.out.println(("清理了" + count + "个login log(s)"));
                    conn.commit();
                } catch (SQLException e) {
                    System.err.println("CleanupTasker中出现sql异常");
                    e.printStackTrace();
                    try {
                        conn.rollback();
                        startLoginTokenCleanupTask();
                    } catch (SQLException ex){
                        System.err.println("CleanupTasker中出现rollback异常");
                        e.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                } finally {
                    DBManager.closeConnection(conn);
                }
            }
        };

        scheduler.scheduleAtFixedRate(cleanupTask, 1, 24 * Integer.parseInt(getConfigValue("login_log_clean_task_execution_interval")), TimeUnit.HOURS);
    }
    // 停止任务
    public static void stopCleanupTasks() {
        scheduler.shutdown();
    }
    private static String getConfigValue(String configKey) {
        Properties properties = new Properties();
        String configFilePath = "conf/cleanuptasker.properties"; // 配置文件路径，放在类路径下

        try (InputStream inputStream =CleanupTasker.class.getClassLoader().getResourceAsStream(configFilePath)) {
            if (inputStream == null) {
                System.err.println("配置文件" + configFilePath + "不存在");
                throw new IllegalArgumentException("Configuration file not found: " + configFilePath);
            }

            // 加载配置文件
            properties.load(inputStream);

            // 根据传入的 key 获取对应的配置值
            String configValue = properties.getProperty(configKey);

            // 如果配置值为空，返回一个默认值或抛出异常
            if (configValue == null) {
                System.err.println("配置文件" + configFilePath + "有误或者不全");
                throw new IllegalArgumentException("Configuration key not found: " + configKey);
            }

            return configValue;

        } catch (IOException e) {
            // 处理读取文件时的异常
            System.err.println("文件" + configFilePath + "读取失败");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
