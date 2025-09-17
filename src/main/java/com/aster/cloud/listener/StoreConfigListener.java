package com.aster.cloud.listener;
import com.aster.cloud.utils.DBManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class StoreConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        InputStream inputStream = null;
        try {
            // 通过类加载器加载配置文件
            inputStream = DBManager.class.getClassLoader().getResourceAsStream("conf/store.properties");
            if (inputStream == null) {
                System.err.println(("配置文件'store.properties'未找到"));
                throw new RuntimeException();
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            // 获取 ServletContext
            ServletContext sc = sce.getServletContext();
            // 将配置属性存储到 ServletContext
            sc.setAttribute("file_store_path", properties.getProperty("file_store_path"));
            System.out.println(("文件存储位置已加载并设置到ServletContext"));

        } catch (IOException e) {
            System.err.println(("StoreConfigListener中加载配置文件失败"));
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 关闭输入流
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.err.println(("StoreConfigListener中关闭配置文件输入流失败"));
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("StoreConfigListener执行成功");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 应用停止时的清理操作，如果有需要释放的资源可以放在这里
    }
}
