package com.aster.cloud.utils;

import com.aster.cloud.beans.User;
import com.aster.cloud.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;

public class UserManager {
    public static boolean isUserExists(String userName){
        SqlSession sqlSession = SqlSessionUtils.getSqlSession(true);
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = userMapper.selectByName(userName);
        SqlSessionUtils.closeSqlSession();
        return user != null;
    }
}
