package com.aster.cloud.mapper;

import com.aster.cloud.beans.LoginToken;

import java.util.Date;

public interface LoginTokenMapper {
    LoginToken selectByLoginToken(String loginToken);
    int deleteByLoginToken(String loginToken);
    int deleteByName(String name);
    int insertOne(LoginToken loginToken);
    int deleteByCreateDate(Date createDate);
}
