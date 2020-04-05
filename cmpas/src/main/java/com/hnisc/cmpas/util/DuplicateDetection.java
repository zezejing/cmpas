package com.hnisc.cmpas.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DuplicateDetection {
    private static final String html_seperator="<br>";
    private static final String sentence_seperator="[,.;!:，。；!：]";
    public static String[] split(String src)
    {
        List<String> result=new ArrayList<>();
        String []htmls=src.split(html_seperator);
        for (String s:htmls)
            for (String s2:s.split(sentence_seperator))
                result.add(s2);
        String []results=new String[result.size()];
        result.toArray(results);
        return results;
    }
    public static String[] clean(String []src)
    {
        for (int i=0;i<src.length;i++)
            src[i]=clean(src[i]);
        return src;
    }
    public static String clean(String src)
    {
        return src.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]","");
    }
    //检测两个作业内容的相似率
    public static float detect(String des,String src)
    {
        float resultFloat=0.00f;
        //分语义行切割作业内容
        String desArray[]=split(des);
        String srcArray[]=split(src);
        //对作业内容中非自然语言进行清洗除杂
        desArray=clean(desArray);
        srcArray=clean(srcArray);
        for (String s:desArray)
            resultFloat+=checkSingleLineWithSrcArray(s,srcArray);
        resultFloat/=desArray.length;
        return resultFloat;
    }
    //将一行和所有可能行进行比较得出最大的相似率
    private static float checkSingleLineWithSrcArray(String line, String []srcArray)
    {
        float result=0.00f;
        for (String s:srcArray)
        {
            //基于找公共子串进行相似度计算
            float temp1=checkSingleLineWithSingleLine(line,s);
            if (temp1>result)
                result=temp1;
            //基于找公共字符数进行相似度计算
            float temo2=checkDuplicationWithMatrix(line,s);
            if (temo2>result)
                result=temo2;
        }
        return result;
    }
    //将一行和一行比较，求出公共子序列，以此得出两字符串的相似度
    private static float checkSingleLineWithSingleLine(String line,String src)
    {
        float result;
        String s1 = line;
        String s2 = src;
        String max = s1.length() >= s2.length()?s1:s2;
        String min = s1.length() >= s2.length()?s2:s1;
        int l = 0;
        String s ="";
        for(int i=0;i<min.length();i++){
            for(int j=i+1;j<=min.length();j++){
                if(max.contains(min.substring(i,j)) && j-i>l){
                    l=j-i;
                    s=min.substring(i,j);
                }
            }
        }
        result=s.length();
        result/=line.length();
        return result;
    }
    //将一行和一行比较，使用集合法求出相似度
    private static float checkDuplicationWithMatrix(String s1,String s2)
    {
        float result;
        int count=0;
        for (int i=0;i<s1.length();i++)
            for (int j=0;j<s2.length();j++)
                if (s1.charAt(i)==s2.charAt(j))
                    count++;
        result=count;
        result/=((s1.length()+s2.length())/2);
        return result;
    }
    public static String transferFloatToPersentString(float f)
    {
        String result;
        f*=10000;
        int t1=(int)f;
       result=(((float)t1)/100)+"%";
        return result;
    }
    public static void main(String[] args) {
        Date start=new Date();
        String tar="public class AAAA {<br> public static void main(String[] args) {<br> <br>  nineSort();<br>       <br> }<br><br> // 打印九九乘法表<br> public static void nineSort(){<br>  for(int i = 1; i <= 9; i++) {<br>        for (int j = 1; j <= i; j++) {<br>          System.out.print(j+\"*\"+i+\"=\"+j*i+\"\\t\");<br>        }<br>        System.out.println();<br>      }<br>    }<br>}";
        String src="public class AAAA {<br> // 打印九九乘法表<br>  public static void nineSortTest(){<br>   for(int index1 = 1; index1 <= 9; index1++) {<br>         for (int index2 = 1; index2 <= index1; index2++) {<br>           System.out.print(index2+\"*\"+index1+\"=\"+index2*index1+\"\\t\");<br>         }<br>         System.out.println();<br>       }<br>     }<br> public static void main(String[] args) {<br> <br>  nineSortTest();<br>       <br> }<br>}";
        System.out.println(detect(src,tar));
        Date end=new Date();
        System.out.println("花费时间："+(end.getTime()-start.getTime())+"毫秒");
    }
}
