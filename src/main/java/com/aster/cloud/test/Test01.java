package com.aster.cloud.test;

import com.aster.cloud.beans.FileOrDirInformation;
import com.aster.cloud.utils.FileManager;
import com.aster.cloud.utils.IPManager;
import com.aster.cloud.utils.PathManager;
import com.aster.cloud.utils.UUIDGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Test01 {
    public static void main(String[] args) throws IOException {
//        test01();
        test02();
    }

    private static void test01(){
        System.out.println(IPManager.isIPv4("111.111.111.11"));
    }
    private static void test02() {
        for (int i = 0; i < 10; i++){
            System.out.println(UUIDGenerator.generateUniqueDirectoryName().length());
        }

    }
}
