package com.aster.cloud.beans;

public class IP {
    private String IP;

    public IP() {
    }

    public IP(String IP) {
        this.IP = IP;
    }

    /**
     * 获取
     * @return IP
     */
    public String getIP() {
        return IP;
    }

    /**
     * 设置
     * @param IP
     */
    public void setIP(String IP) {
        this.IP = IP;
    }

    public String toString() {
        return "IP{IP = " + IP + "}";
    }
}
