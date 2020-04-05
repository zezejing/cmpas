package com.hnisc.cmpas;

import java.io.*;

public class BeanAnnotationAdder {
    public static void addAnnotation(String dir)
    {
        File file=new File(dir);
        if(file.isDirectory())
        {
            File[]files=file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".java");
                }
            });
            for (File f:files)
                add(file);
        }
    }
    //为java bean文件添加注解
    private static void add(File file)
    {
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String s;
            StringBuilder stringBuilder=new StringBuilder();
            while ((s=bufferedReader.readLine())!=null)
            {
                System.out.println(s);
//                if (s.indexOf("*/")>-1)

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        add(new File("D:\\com\\hnisc\\cmpas\\bean\\User.java"));
    }
}
