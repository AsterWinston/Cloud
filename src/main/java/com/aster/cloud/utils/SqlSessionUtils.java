package com.aster.cloud.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.sql.SQLException;

public class SqlSessionUtils {
    private static final ThreadLocal<SqlSession> threadLocal = new ThreadLocal<>();
    private static final SqlSessionFactory sqlSessionFactory;
    static {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private SqlSessionUtils(){}
    public static SqlSession getSqlSession(boolean autoCommit) {
        SqlSession sqlSession = threadLocal.get();
        if(sqlSession == null){
            sqlSession = sqlSessionFactory.openSession(autoCommit);
            threadLocal.set(sqlSession);
        } else {
            try {
                boolean oldAutoCommit = sqlSession.getConnection().getAutoCommit();
                if (oldAutoCommit != autoCommit) {
                    sqlSession.close();
                    threadLocal.remove();
                    sqlSession = sqlSessionFactory.openSession(autoCommit);
                    threadLocal.set(sqlSession);
                }
            } catch (SQLException e) {
                System.err.println("SqlSessionUtils中出现sql异常");
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        return sqlSession;
    }
    public static void closeSqlSession(){
        SqlSession sqlSession = threadLocal.get();
        if(sqlSession != null) {
            threadLocal.remove();
            sqlSession.close();
        }
    }
}
