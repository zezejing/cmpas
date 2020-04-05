package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.Message;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.service.IMessageService;
import com.hnisc.cmpas.util.AliCloudMessage;
import com.hnisc.cmpas.util.ParameterValidator;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/cmpas/message")
public class MessageController {
    @Autowired
    private IMessageService iMessageService;
    @Autowired
    public ResultData resultData;
    @Autowired
    private ParameterValidator parameterValidator;

    @RequestMapping(value = "/sendVerificationMessage",method = {RequestMethod.GET},params = {"phone"})
    public ResultData sendVerificationMessage(HttpSession httpSession, Message message)
    {
        resultData.setResult(parameterValidator.validate(message));
        if(parameterValidator.checkResult(resultData.getResult()))
        {
            //限制每日短信条数
//            Wrapper<Message>wrapper=new EntityWrapper<>();

            //如果短信发送成功则将短信写入数据库
            String VerificationCode=Util.getRandomIdentityCode(6);
            String success="验证码发送成功!";
            String failure="验证码发送失败!";
            message.setSendtime(new Date());
            message.setContent(VerificationCode);
            if(AliCloudMessage.sendVerificationMessage(message.getPhone(),VerificationCode)&& iMessageService.insert(message))
            {
                //将Message对象放入session
                httpSession.setAttribute("IdentityMessage",message);
                resultData.setResult(success);
            }
            else
                resultData.setResult(failure);
        }
        return resultData;
    }
}
