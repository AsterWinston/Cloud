package com.aster.cloud.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    public static boolean isUserExists(String userName){
        Connection conn = null;
        PreparedStatement preparedStatement;
        String sql = "select * from user where name = ?";
        try{
            conn = DBManager.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, userName);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        } catch (SQLException e){
            System.err.println("UserManager中出现sql异常");
            e.printStackTrace();
            try {
                conn.rollback();
                return isUserExists(userName);
            } catch (SQLException ex){
                System.err.println("UserManager中出现rollback异常");
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        } finally {
            DBManager.closeConnection(conn);
        }
    }
}
