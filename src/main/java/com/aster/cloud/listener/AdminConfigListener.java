package com.aster.cloud.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AdminConfigListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        InputStream inputStream = null;
        try {
            // 通过类加载器加载配置文件
            inputStream = AdminConfigListener.class.getClassLoader().getResourceAsStream("conf/admin.properties");
            if (inputStream == null) {
                System.err.println(("配置文件'admin.properties'未找到"));
                throw new RuntimeException();
            }

            // 加载属性文件
            Properties properties = new Properties();
            properties.load(inputStream);

            // 获取配置值
            String name = properties.getProperty("name");
            String password = properties.getProperty("password");
            if(name != null && password !=null){

            }
            // 获取 ServletContext
            ServletContext sc = sce.getServletContext();

            // 设置到 ServletContext 中
            sc.setAttribute("admin_name", name);
            sc.setAttribute("admin_password", password);
            System.out.println(("管理员配置已加载并设置到ServletContext"));

        } catch (IOException e) {
            System.err.println(("加载配置文件失败"));
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println(("AdminConfigListener中关闭配置文件输入流失败"));
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("AdminConfigListener执行成功");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 应用停止时的清理操作

    }
}
