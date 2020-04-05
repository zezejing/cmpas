package com.hnisc.cmpas.bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component
public class Marker {

	private final int threshold=4500;
	private Map<String,Info> user=new HashMap<String,Info>();
	public void mark(String phone,String token)//在线登记
	{
		user.put(phone,new Info(token));
	}
	public boolean isOnline(String phone,String token)//是否在线
	{
		if(user.containsKey(phone)&&user.get(phone).rightToken(URLDecoder.decode(token)))
			return true;
		return false;
	}
	public int getOnlinePeople()//获取在线人数
	{
		int result=0;
		for(String s:user.keySet())
			if(new Date().getTime()-user.get(s).getTime()<=threshold)
				result++;
		return result;
	}
	class Info{
		private String token;
		private Date date;
		public Info(String token)
		{
			this.token=token;
			this.date=new Date();
		}
		public long getTime()
		{
			return this.date.getTime();
		}
		public boolean rightToken(String token)
		{
			if(this.token.equals(token))
			{
				System.out.println("鉴权成功");
				return true;
			}
			System.out.println("鉴权失败");
			return false;
		}
	}
	public void handle(HttpServletRequest request, HttpServletResponse response)
	{
		String phone=request.getParameter("phone"),token=request.getParameter("token");
		mark(phone,token);
		PrintWriter pw;
		try {
			pw = response.getWriter();
			pw.print("Online="+getOnlinePeople());
			pw.flush();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
