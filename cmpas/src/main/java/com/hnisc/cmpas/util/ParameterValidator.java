package com.hnisc.cmpas.util;

import org.springframework.stereotype.Component;

/*
 * 参数验证器
 * Author:wzw
 * time:2019.3.22
 * email:3301633914@qq.com
 */
@Component
public class ParameterValidator extends AbstractParameterValidator{
	public String  phone(String str)
	{
		String back="手机号码不合法";
		String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
		return str.matches(regex)?SUCESS:back;
	}
	public String nickname(String str)
	{
		String back="昵称不合法(1-20位数字、字母、下划线、中文)";
		String regex = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]{1,20}$";
		return str.matches(regex)?SUCESS:back;
	}
	public  String password(String str)
	{
		String back="密码不合法(8-20位数字、字母)";
		String regex = "^[0-9a-zA-Z]{8,20}$";
		return str.matches(regex)?SUCESS:back;
	}

	public String realname(String str)
	{
		String back="姓名不合法(2-4位中文)";
		String regex = "^[\\u4e00-\\u9fa5]{2,4}$";
		return str.matches(regex)?SUCESS:back;
	}

	public String gender(String str)
	{
		String back="性别不合法(男|女)";
		String regex = "^[男女]$";
		return str.matches(regex)?SUCESS:back;
	}
	public String stuid(String str)
	{
		String back="学号不合法（20位以内数字）";
		String regex = "^[0-9]{1,20}$";
		return str.matches(regex)?SUCESS:back;
	}

//	public static void main(String[] args) {
//		System.out.println(new ParameterValidator().gender("男"));
//	}
}
