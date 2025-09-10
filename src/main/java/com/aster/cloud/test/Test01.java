package com.aster.cloud.test;

import com.aster.cloud.beans.FileOrDirInformation;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.PathManager;

import java.util.List;

public class Test01 {
    public static void main(String[] args) {
        System.out.println("/" + PathManager.getRelativePath("D:/test", "D:/test/test/test").replace("\\", "/"));
    }
}
