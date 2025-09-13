package com.aster.cloud.beans;

import java.util.List;

public class LoginLogPageResult {
    private int totalCount;               // 总条数
    private List<LoginLog> loginLogs;     // 当前页数据

    public LoginLogPageResult(int totalCount, List<LoginLog> loginLogs) {
        this.totalCount = totalCount;
        this.loginLogs = loginLogs;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<LoginLog> getLoginLogs() {
        return loginLogs;
    }
}
