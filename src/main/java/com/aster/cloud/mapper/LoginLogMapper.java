package com.aster.cloud.mapper;

import com.aster.cloud.beans.LoginLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoginLogMapper {
    int selectCountOfLoginLog();
    List<LoginLog> selectLoginLogByPage(@Param("offset") int offset, @Param("itemCountEveryPage") int itemCountEveryPage);
    int deleteByName(String name);
    int truncate();
    int insertOne(LoginLog loginLog);
    int deleteByID(long id);
}
