package com.hnisc.cmpas.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
public class AliCloudMessage {
	//是否真的发送短信的开关
	static final boolean Switch=true;
	static final String product = "Dysmsapi";
	static final String domain = "dysmsapi.aliyuncs.com";
	static final String accessKeyId = "LTAIcpAA8tIg601I";
	static final String accessKeySecret = "RblUWn3hHybCCKrtViGYE7Pb1BJmfP";
	static IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
			accessKeySecret);
	static IAcsClient acsClient = new DefaultAcsClient(profile);
	static SendSmsRequest smsrequest = new SendSmsRequest();
	static{
		try{
			System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
			System.setProperty("sun.net.client.defaultReadTimeout", "10000");
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
			smsrequest.setMethod(MethodType.POST);
			smsrequest.setSignName("陈福星");

		}catch(Exception e){
			e.printStackTrace();
		}
	}
public static  boolean sendVerificationMessage(String phone,String code)
{
	boolean callback=false;
	   try{
		//设置短信模版
		smsrequest.setTemplateCode("SMS_115395674");
		System.out.println("手机号码为："+phone+"验证码为:"+code);
		callback=true;
		if(Switch)
		{
			smsrequest.setPhoneNumbers(phone);
			smsrequest.setTemplateParam("{\"code\":\""+code+"\"}");
			acsClient.doAction(smsrequest);
			SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(smsrequest);
			if(sendSmsResponse.getCode() == null && (!(sendSmsResponse.getCode().equals("OK"))))
				System.out.println("短信验证码暂时停止服务!请稍后重试...");
			else
			{
				System.out.println("短信验证码发送成功!");
				callback=true;
			}
		}
		}
	   catch(Exception e)
	   {
		   System.out.println("短信发送失败");
		   e.printStackTrace();
		}
	   return callback;
}
public static boolean sendNoticeMessage(String phone,String name,String notice)
{
	boolean callback=false;
	try{
		//设置短信模版
		smsrequest.setTemplateCode("SMS_161592093");
		callback=true;
		/*
		 * 
		 */
		smsrequest.setPhoneNumbers(phone);
		smsrequest.setTemplateParam("{\"name\":\""+name+"\",\"notice\":\""+notice+"\"}");
		acsClient.doAction(smsrequest);
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(smsrequest);
		if(sendSmsResponse.getCode() == null && (!(sendSmsResponse.getCode().equals("OK"))))
			System.out.println("短信验证码暂时停止服务!请稍后重试...");
		else
		{
			System.out.println("短信验证码发送成功!");
			callback=true;
		}
		}
	   catch(Exception e)
	   {
		   System.out.println("短信发送失败");
		   e.printStackTrace();
		}
	   return callback;
}
}
