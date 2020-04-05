package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.Message;
import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.service.IMessageService;
import com.hnisc.cmpas.service.IUserService;
import com.hnisc.cmpas.util.ParameterValidator;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author humorchen
 * @since 2019-06-01
 */
@RestController
@RequestMapping("/cmpas/user")
public class UserController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ParameterValidator parameterValidator;
    @Autowired
    private ResultData resultData;
    @Autowired
    private IMessageService iMessageService;
    @RequestMapping(value = "/register",method = {RequestMethod.GET},params = {"phone","password","nickname","stuid","realname","gender","VerificationCode"})
    public ResultData register(HttpSession httpSession,User user, String VerificationCode)
    {
        resultData.setResult(parameterValidator.validate(user));
        if(parameterValidator.checkResult(resultData.getResult()))
        {
            //校验验证码
            Wrapper<Message>wrapper=new EntityWrapper<>();
            wrapper.where("phone={0} and content={1}",new Object[]{user.getPhone(),VerificationCode}).orderBy("sendtime",false);
            Message message=iMessageService.selectOne(wrapper);
            if(message!=null&&message.getPhone().equals(user.getPhone())&&message.getContent().equals(VerificationCode)&&message.enable())
            {
                //设置注册时间
                user.setRegisterTime(new Date());
                try {
                    if (iUserService.insert(user))
                        resultData.setResult("注册成功");
                    Util.removeMessageFromHttpSession(httpSession);
                }catch (Exception e){
                    resultData.setResult("账号已注册");
//                    e.printStackTrace();
                }
            }
            else
                resultData.setResult("短信验证码无效");
        }
        //将错误原因返回
        return resultData;
    }
    @RequestMapping(value = "/login",method = {RequestMethod.GET},params = {"phone","password"})
    public ResultData login(HttpSession httpSession,User user)
    {
        resultData.setResult(parameterValidator.validate(user));
        if(parameterValidator.checkResult(resultData.getResult()))
        {
            Wrapper<User>wrapper=new EntityWrapper<>();
            wrapper.where("phone={0} and password={1} ",new Object[]{user.getPhone(),user.getPassword()});
            User dbUser=iUserService.selectOne(wrapper);
            if(dbUser!=null)
            {
                if(dbUser.getIced().equals("是"))
                    resultData.setResult("账号已冻结");
                else
                {
                    //将登录了的用户放入session
                    httpSession.setAttribute("LoginedUser",dbUser);
                    System.out.println("登录成功已经将User放入到"+httpSession.getId());
                    resultData.setResult("登录成功");
                }
            }
            else
                resultData.setResult("账号或者密码错误");
        }
        return resultData;
    }
    @RequestMapping(value = "/retrieveByMessage",method = {RequestMethod.GET},params = {"phone","VerificationCode","newpassword"})
    public ResultData retrieveByMessage(HttpSession httpSession,String phone,String VerificationCode,String newpassword)
    {
        if (parameterValidator.checkResult(parameterValidator.phone(phone))&&parameterValidator.checkResult(parameterValidator.password(newpassword)))
        {
            Wrapper<Message>messageWrapper=new EntityWrapper<>();
            messageWrapper.eq("phone",phone);
            messageWrapper.setSqlSelect("top 1 *");
            messageWrapper.orderBy("id",false);
            Message message=iMessageService.selectOne(messageWrapper);
            if (message!=null)
            {
                if (message.getContent().equals(VerificationCode))
                {
                    Wrapper<User> wrapper=new EntityWrapper<>();
                    wrapper.eq("phone",phone);
                    wrapper.eq("iced","否");
                    User user=iUserService.selectOne(wrapper);
                    if (user!=null)
                    {
                        user.setPassword(newpassword);
                        if (iUserService.updateById(user))
                            resultData.setResult("密码修改成功");
                        else
                            resultData.setResult("密码修改失败");
                    }
                    else
                        resultData.setResult("账户不存在或已被封禁");
                }
                else
                    resultData.setResult("验证码不正确");
            }
            else
                resultData.setResult("请先获取短信");
        }
        else
            resultData.setResult("手机号码或密码不合法");
        return resultData;
    }
    @RequestMapping(value = "/retrieveByOldPassword",method = {RequestMethod.GET},params = {"phone","password","newpassword"})
    public ResultData retrieveByOldPassword(User user,String newpassword)
    {
        resultData.setResult(parameterValidator.validate(user));
        if(parameterValidator.checkResult(resultData.getResult()))
        {
            Wrapper<User>wrapper=new EntityWrapper<>();
            wrapper.where("phone={0}",new Object[]{user.getPhone()});
            User dbUser=iUserService.selectOne(wrapper);
            //是否有这个账号
            if(dbUser!=null)
            {
                //原密码是否正确
                if(dbUser.getPassword().equals(user.getPassword()))
                {
                    //新密码是否合法
                    if(parameterValidator.checkResult(parameterValidator.password(newpassword))) {
                        if (dbUser.getPassword().equals(newpassword))
                            resultData.setResult("原密码与新密码相同");
                        else
                            {
                            dbUser.setPassword(newpassword);
                            if(iUserService.updateById(dbUser))
                                resultData.setResult("密码修改成功");
                            else
                                resultData.setResult("密码修改失败");
                        }
                    }
                    else
                        resultData.setResult(parameterValidator.password(newpassword));
                }
                else
                    resultData.setResult("原密码错误");
            }
            else
                resultData.setResult("账号不存在");
        }
        return resultData;
    }
    @RequestMapping(value = "/improveInformation",method = {RequestMethod.GET},params = {"email","career","title","school","institude"})
    public ResultData improveInformation( HttpSession httpSession, User user)
    {
        resultData.setResult(parameterValidator.validate(user));
        if(parameterValidator.checkResult(resultData.getResult()))
        {
            User sessionUser=Util.getUserFromHttpSession(httpSession);
            if(sessionUser!=null)
            {
                if (sessionUser.getTitle()==null||sessionUser.getTitle().equals(""))
                {
                    sessionUser.setEmail(user.getEmail());
                    sessionUser.setCareer(user.getCareer());
                    sessionUser.setTitle(user.getTitle());
                    sessionUser.setSchool(user.getSchool());
                    sessionUser.setInstitude(user.getInstitude());
                    if(iUserService.updateById(sessionUser))
                    {
                        httpSession.setAttribute("LoginedUser",sessionUser);
                        resultData.setResult("信息完善成功");
                    }
                    else
                        resultData.setResult("信息完善失败");
                }
                else
                    resultData.setResult("您已完善过信息了");
            }
            else
                resultData.setResult("请先登录");
        }
        return resultData;
    }
    @RequestMapping(value ="/isLogined",method = {RequestMethod.GET})
    public ResultData isLogined(HttpSession httpSession)
    {
        User user=(User)httpSession.getAttribute("LoginedUser");
        if (user!=null)
            resultData.setResult("true");
        else
            resultData.setResult("false");
        return resultData;
    }
    @RequestMapping(value ="/isTeacher",method = {RequestMethod.GET})
    public ResultData isTeacher(HttpSession httpSession)
    {
        User user=(User)httpSession.getAttribute("LoginedUser");
        if (user!=null&&(!(user.getTitle()==null|| user.getTitle().equals(""))))
            resultData.setResult("true");
        else
            resultData.setResult("false");
        return resultData;
    }
    @RequestMapping(value = "/logout")
    public void  logout(HttpSession httpSession)
    {
        httpSession.removeAttribute("LoginedUser");
    }
    @RequestMapping(value = "/getMyInfo",method = {RequestMethod.GET})
    public User getMyInfo(HttpSession httpSession)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        user.setPassword("");
        return user;
    }
}
