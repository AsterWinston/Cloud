package com.aster.cloud.beans;

import java.util.Date;

/**
 * LoginToken 实体类，对应数据库中的 login_token 表
 */
public class LoginToken {
    // 对应表中的 name 字段
    private String name;

    // 对应表中的 login_token 字段（主键）
    private String loginToken;

    // 对应表中的 create_date 字段
    private Date createDate;

    // 无参构造方法（MyBatis 等框架反射时需要）
    public LoginToken() {
    }

    // 全参构造方法
    public LoginToken(String name, String loginToken, Date createDate) {
        this.name = name;
        this.loginToken = loginToken;
        this.createDate = createDate;
    }

    // getter 和 setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    // 可选：重写 toString 方法，方便日志输出和调试
    @Override
    public String toString() {
        return "LoginToken{" +
                "name='" + name + '\'' +
                ", loginToken='" + loginToken + '\'' +
                ", createDate=" + createDate +
                '}';
    }
}