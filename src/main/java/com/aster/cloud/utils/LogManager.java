package com.aster.cloud.utils;

import com.aster.cloud.beans.LoginLog;
import com.aster.cloud.mapper.LoginLogMapper;
import org.apache.ibatis.session.SqlSession;

public class LogManager {
    public static void loginLogInsert(String name, java.util.Date loginTime, String ip){
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            LoginLogMapper loginLogMapper = sqlSession.getMapper(LoginLogMapper.class);
            LoginLog loginLog = new LoginLog(name, loginTime, ip);
            loginLogMapper.insertOne(loginLog);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("LogManager中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
    public static void clearLog(){
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            LoginLogMapper loginLogMapper = sqlSession.getMapper(LoginLogMapper.class);
            loginLogMapper.truncate();
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("LogManager中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
    public static void deleteLogById(long id){
        SqlSession sqlSession = null;
        try {
            sqlSession = SqlSessionUtils.getSqlSession(false);
            LoginLogMapper loginLogMapper = sqlSession.getMapper(LoginLogMapper.class);
            loginLogMapper.deleteByID(id);
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            System.err.println("LogManager中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            SqlSessionUtils.closeSqlSession();
        }
    }
}
