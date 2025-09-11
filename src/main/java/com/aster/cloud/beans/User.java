package com.aster.cloud.beans;

public class User {
    String userName;
    String createDate;
    String limitVolume;

    public User() {
    }

    public User(String userName, String createDate, String limitVolume) {
        this.userName = userName;
        this.createDate = createDate;
        this.limitVolume = limitVolume;
    }

    /**
     * 获取
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取
     * @return createDate
     */
    public String getCreateDate() {
        return createDate;
    }

    /**
     * 设置
     * @param createDate
     */
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取
     * @return limitVolume
     */
    public String getLimitVolume() {
        return limitVolume;
    }

    /**
     * 设置
     * @param limitVolume
     */
    public void setLimitVolume(String limitVolume) {
        this.limitVolume = limitVolume;
    }

    public String toString() {
        return "User{userName = " + userName + ", createDate = " + createDate + ", limitVolume = " + limitVolume + "}";
    }
}
