package com.aster.cloud.mapper;

import com.aster.cloud.beans.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    User selectByName(String name);
    int updatePasswordByName(@Param("name") String name, @Param("password") String password);
    int insertOne(User user);
    int deleteByName(String name);
    User selectByNameAndPassword(@Param("name") String name, @Param("password") String password);
    int updateLimitVolumeByName(@Param("limitVolume") long limitVolume, @Param("name") String name);
    User selectByDirName(String dirName);
    List<User> selectAll();
}
