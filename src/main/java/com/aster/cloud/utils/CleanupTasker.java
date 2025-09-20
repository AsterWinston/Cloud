package com.aster.cloud.utils;

import com.aster.cloud.mapper.LoginLogMapper;
import com.aster.cloud.mapper.LoginTokenMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleanupTasker {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static final Logger log = LoggerFactory.getLogger(CleanupTasker.class);
    // 定时任务：清理过期内容
    public static void startLoginTokenCleanupTask() {
        Runnable cleanupTask = new Runnable() {
            @Override
            public void run() {

                SqlSession sqlSession = null;
                try {
                    sqlSession = SqlSessionUtils.getSqlSession(false);
                    Date timeDaysAgo = new Date((new Date().getTime() - 10L * 24 * 60 * 60 * 1000));
                    LoginTokenMapper loginTokenMapper = sqlSession.getMapper(LoginTokenMapper.class);
                    int count = loginTokenMapper.deleteByCreateDate(timeDaysAgo);
                    System.out.println(("清理了" + count + "个过期login token(s)"));
                    sqlSession.commit();
                } catch (Exception e){
                    sqlSession.rollback();
                    System.err.println("CleanupTasker中出现异常");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    SqlSessionUtils.closeSqlSession();
                }
            }
        };

        scheduler.scheduleAtFixedRate(cleanupTask, 1, 24 * Integer.parseInt(getConfigValue("login_token_clean_task_execution_interval")), TimeUnit.HOURS);
    }
    public static void startLoginLogCleanTask(){
        Runnable cleanupTask = new Runnable() {
            @Override
            public void run() {
                SqlSession sqlSession = null;
                try {
                    sqlSession = SqlSessionUtils.getSqlSession(false);
                    LoginLogMapper loginLogMapper = sqlSession.getMapper(LoginLogMapper.class);
                    loginLogMapper.truncate();
                    sqlSession.commit();
                } catch (Exception e){
                    sqlSession.rollback();
                    System.err.println("CleanupTasker中出现异常");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    SqlSessionUtils.closeSqlSession();
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
