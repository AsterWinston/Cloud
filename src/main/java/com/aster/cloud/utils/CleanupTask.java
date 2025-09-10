package com.aster.cloud.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.HTMLDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CleanupTask {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    // 定时任务：清理过期内容
    public static void startCleanupTask() {
        Runnable cleanupTask = new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                String sql = "delete from login_tokens where create_date < ?";
                PreparedStatement preparedStatement = null;
                long currentTimeMillis = System.currentTimeMillis();
                long tenDaysAgoMillis = currentTimeMillis - TimeUnit.DAYS.toMillis(10);
                Date tenDaysAgo = new Date(tenDaysAgoMillis);

                // 将日期格式化为字符串，符合数据库要求的格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String tenDaysAgoFormatted = sdf.format(tenDaysAgo);
                try {
                    conn = DBUtils.getConnection();
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, tenDaysAgoFormatted);
                    int count = preparedStatement.executeUpdate();
                    System.out.println(("清理了" + count + "个过期login_token(s)"));
                } catch (SQLException e) {
                    System.out.println("CleanupTask中出现sql异常");
                    e.printStackTrace();
                    throw new RuntimeException(e);
                } finally {
                    DBUtils.closeConnection(conn);
                }
            }
        };

        scheduler.scheduleAtFixedRate(cleanupTask, 1, 24, TimeUnit.HOURS);
    }

    // 停止任务
    public static void stopCleanupTask() {
        scheduler.shutdown();
    }
}
