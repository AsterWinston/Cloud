package com.aster.cloud.beans;

public class LoginLog {
    private String id;
    private String name;
    private String loginTime;
    private String loginIP;

    public LoginLog() {
    }

    public LoginLog(String id, String name, String loginTime, String loginIP) {
        this.id = id;
        this.name = name;
        this.loginTime = loginTime;
        this.loginIP = loginIP;
    }

    /**
     * 获取
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置
     * @param id
     */
    public void setId(String id) {
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
    public String getLoginTime() {
        return loginTime;
    }

    /**
     * 设置
     * @param loginTime
     */
    public void setLoginTime(String loginTime) {
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
