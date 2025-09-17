package com.aster.cloud.beans;
import java.util.List;

public class DirectoryInformation {
    private String current_dir;
    private List<FileOrDirInformation> dir_list;

    public DirectoryInformation() {
    }

    public DirectoryInformation(String current_dir, List<FileOrDirInformation> dir_list) {
        this.current_dir = current_dir;
        this.dir_list = dir_list;
    }

    /**
     * 获取
     * @return current_dir
     */
    public String getCurrent_dir() {
        return current_dir;
    }

    /**
     * 设置
     * @param current_dir
     */
    public void setCurrent_dir(String current_dir) {
        this.current_dir = current_dir;
    }

    /**
     * 获取
     * @return dir_list
     */
    public List<FileOrDirInformation> getDir_list() {
        return dir_list;
    }

    /**
     * 设置
     * @param dir_list
     */
    public void setDir_list(List<FileOrDirInformation> dir_list) {
        this.dir_list = dir_list;
    }

    public String toString() {
        return "DirectoryInformation{current_dir = " + current_dir + ", dir_list = " + dir_list + "}";
    }
}
