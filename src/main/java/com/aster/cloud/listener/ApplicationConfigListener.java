package com.aster.cloud.listener;

import com.aster.cloud.utils.CleanupTasker;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        CleanupTasker.startLoginTokenCleanupTask();//定期清理数据库中过期的token
        CleanupTasker.startLoginLogCleanTask();
        System.out.println("ApplicationConfigListener执行成功");
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        CleanupTasker.stopCleanupTasks();
    }
}
