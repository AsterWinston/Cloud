package com.aster.cloud.utils;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
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
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByDirName(uuid);
        SqlSessionUtils.closeSqlSession();;
        return user == null;
    }
}
