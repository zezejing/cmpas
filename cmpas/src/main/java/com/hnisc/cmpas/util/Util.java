package com.hnisc.cmpas.util;

import com.hnisc.cmpas.bean.Message;
import com.hnisc.cmpas.bean.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static String getRandomIdentityCode(int len)
    {
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<len;i++)
            stringBuilder.append((char)('0'+Math.floor(Math.random()*10)));
        return stringBuilder.toString();
    }
    public static void mkdir(String dir)
    {
        File file=new File(dir);
        if(!file.exists())
        {
            if(!file.getParentFile().exists())
                mkdir(file.getParent());
            file.mkdir();
        }
    }
    public static void main(String[]args)
    {

        mkdir("C:\\HUMOR123\\123\\123");
    }
    public static String mkURL(String filename)
    {
        return MD5.encode(filename+new Date().toString());
    }
    public static String mkToekn(String phone)
    {
        return MD5.encode(phone+new Date().toString()+Util.getRandomIdentityCode(6));
    }
    public static void addToekn(HttpServletResponse response,String token)
    {
        response.addCookie(new Cookie("token",token));
    }
    public static boolean isNullStr(String str)
    {
        if (str==null||str.equals(""))
            return true;
        if (str.length()>0)
            return false;
        return true;
    }
    public static String getDate()
    {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        return simpleDateFormat.format(new Date());
    }
    public static String getRandomString(int len)
    {
        StringBuilder stringBuilder=new StringBuilder();
        for (int i=0;i<10;i++)
            stringBuilder.append(""+i);
        for (int i='a';i<'z';i++)
            stringBuilder.append(((char)i));
        for (int i='A';i<'Z';i++)
            stringBuilder.append(((char)i));
        StringBuilder stringBuilder1=new StringBuilder();
        for (int i=0;i<len;i++)
            stringBuilder1.append(stringBuilder.charAt((int)Math.floor(Math.random()*stringBuilder.length())));
        return stringBuilder1.toString();
    }
    public static User getUserFromHttpSession(HttpSession httpSession)
    {
        return (User)httpSession.getAttribute("LoginedUser");
    }
    public static Message getMessageFromHttpSession(HttpSession httpSession)
    {
        return (Message)httpSession.getAttribute("IdentityMessage");
    }
    public static void removeMessageFromHttpSession(HttpSession httpSession)
    {
        httpSession.removeAttribute("IdentityMessage");
    }
}
