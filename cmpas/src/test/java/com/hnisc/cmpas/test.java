package com.hnisc.cmpas;

import com.hnisc.cmpas.util.MyExcelFileUtil;
import com.hnisc.cmpas.util.Util;

import java.io.File;
import java.util.List;

public class test {
    public static void main(String[] args) {
        String [][]data=new String[3][2];
        for (int i=0;i<data.length;i++)
            for (int j=0;j<data[i].length;j++)
                data[i][j]="123";
        MyExcelFileUtil.writeDataToFile(data,new File("D:\\测试写入的Excel.xlsx"));
    }
}
