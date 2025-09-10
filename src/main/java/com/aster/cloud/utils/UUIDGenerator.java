package com.aster.cloud.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDGenerator {

    // 生成 32 位不带连字符的 UUID
    public static String generateUniqueDirectoryName() {
        // 生成唯一 UUID
        boolean flag = true;
        String uuid = null;
        while(flag){
            uuid = UUID.randomUUID().toString().replace("-", "");
            if(uuid_is_not_repetitive(uuid))flag = false;
        }
        return uuid;
    }
    private static boolean uuid_is_not_repetitive(String uuid){
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String sql = "select * from user where dir_name = ?";
        ResultSet rs = null;
        try{
            conn = DBUtils.getConnection();
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, uuid);
            rs = preparedStatement.executeQuery();
            if(rs.next())return false;
        } catch (SQLException e) {
            System.out.println("UUIDGenerator中出现异常");
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.closeConnection(conn);
        }
        return true;
    }
}
