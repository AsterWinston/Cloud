package com.aster.cloud.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogManager {
    public static void loginLogInsert(String name, String loginTime, String ip){
        Connection conn = null;
        try{
            PreparedStatement preparedStatement = null;
            String sql = "insert into login_log (name, login_time, login_ip) values (?, ?, ?)";
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, loginTime);
            preparedStatement.setString(3, ip);
            int count = preparedStatement.executeUpdate();
            if(count == 1){
                System.out.println("LoginFilter中记录登陆成功");
            } else{
                System.err.println("LoginFilter中记录登陆失败");
            }

        } catch (SQLException e){
            System.err.println("LoginFilter中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                loginLogInsert(name, loginTime, ip);
            } catch (SQLException ex){
                System.err.println("LogManager中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
    public static void clearLog(){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "truncate table login_log";
        try {
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.err.println("LogManager中出现了sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                clearLog();
            } catch (SQLException ex){
                System.err.println("LogManager中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }

    }
    public static void deleteLogById(String id){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "delete from login_log where id = ?";
        try {
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(id));
            preparedStatement.executeUpdate();
        } catch(SQLException e){
            System.err.println("LogManager中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                deleteLogById(id);
            } catch (SQLException ex){
                System.err.println("LogManager中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
}
