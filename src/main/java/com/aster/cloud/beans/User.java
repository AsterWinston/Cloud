package com.aster.cloud.beans;

import java.util.Date;

public class User {
    private String name;
    private String password;
    private long limitVolume;
    private String dirName;
    private Date createDate;


    public User() {
    }

    public User(String name, String password, long limitVolume, String dirName, Date createDate) {
        this.name = name;
        this.password = password;
        this.limitVolume = limitVolume;
        this.dirName = dirName;
        this.createDate = createDate;
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
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取
     * @return limitVolume
     */
    public long getLimitVolume() {
        return limitVolume;
    }

    /**
     * 设置
     * @param limitVolume
     */
    public void setLimitVolume(long limitVolume) {
        this.limitVolume = limitVolume;
    }

    /**
     * 获取
     * @return dirName
     */
    public String getDirName() {
        return dirName;
    }

    /**
     * 设置
     * @param dirName
     */
    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    /**
     * 获取
     * @return createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String toString() {
        return "User{name = " + name + ", password = " + password + ", limitVolume = " + limitVolume + ", dirName = " + dirName + ", createDate = " + createDate + "}";
    }
}
