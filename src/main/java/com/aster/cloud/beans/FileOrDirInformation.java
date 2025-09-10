package com.aster.cloud.beans;

public class FileOrDirInformation {
    public enum Type {
        FILE, DIRECTORY
    }

    private String name;
    private String time;
    private int size; // 单位MB
    private Type type;

    public FileOrDirInformation() {
    }

    public FileOrDirInformation(String name, String time, int size, Type type) {
        this.name = name;
        this.time = time;
        this.size = size;
        this.type = type;
    }

    /**
     * 获取文件/文件夹名
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取最后修改时间
     */
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 获取大小(MB)
     */
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 获取类型
     */
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileOrDirInformation{name=" + name + ", time=" + time + ", size=" + size + ", type=" + type + "}";
    }
    public String getTypeStr() {
        return type.name(); // 返回 "FILE" 或 "DIRECTORY"
    }

}
