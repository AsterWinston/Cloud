package com.aster.cloud.beans;

import java.util.Date;

public class LoginLog {
    private long id;
    private String name;
    private Date loginTime;
    private String loginIP;


    public LoginLog() {
    }

    public LoginLog(String name, Date loginTime, String loginIP) {
        this.name = name;
        this.loginTime = loginTime;
        this.loginIP = loginIP;
    }

    /**
     * 获取
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 获取
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取
     * @return loginTime
     */
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置
     * @param loginTime
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * 获取
     * @return loginIP
     */
    public String getLoginIP() {
        return loginIP;
    }

    /**
     * 设置
     * @param loginIP
     */
    public void setLoginIP(String loginIP) {
        this.loginIP = loginIP;
    }

    public String toString() {
        return "LoginLog{id = " + id + ", name = " + name + ", loginTime = " + loginTime + ", loginIP = " + loginIP + "}";
    }
}
