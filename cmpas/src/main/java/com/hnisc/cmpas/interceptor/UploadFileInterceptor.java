package com.hnisc.cmpas.interceptor;

import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.util.DDOSWall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

/**
 * Author:humorchen
 * 拦截未登录的上传操作
 */
public class UploadFileInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if(request.getRequestURL().indexOf("cmpas/uploadFile/upload")>-1)
//        {
//            HttpSession httpSession=request.getSession();
//            if(httpSession.getAttribute("LoginedUser")==null)
//                return false;
//        }
        //设置五分钟自动退出(单位秒)
//        request.getSession().setMaxInactiveInterval(5*60);

        System.out.println("拦截器-请求信息");
        System.out.println(new Date().toString());
        System.out.println("SessionId:"+request.getSession().getId());
        System.out.println(request.getRequestURL());
        System.out.println("请求语句"+request.getQueryString());
        System.out.println("\n");

//        return ddosWall.filt(ddosWall.getIpAddress(request));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {


    }
}
