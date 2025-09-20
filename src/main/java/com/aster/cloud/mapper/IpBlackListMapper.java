package com.aster.cloud.mapper;

import com.aster.cloud.beans.IP;

import java.util.List;

public interface IpBlackListMapper {
    List<IP> selectAll();
    int deleteAll();
    IP selectByIP(String IP);
    int insertOne(IP ip);
}
