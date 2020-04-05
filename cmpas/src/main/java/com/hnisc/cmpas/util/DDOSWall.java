package com.hnisc.cmpas.util;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
@Component
public class DDOSWall {
	/*
	 * 目前DDOSS防护的配置为：一分钟访问60数据库封IP
	 */
	private final int maxsize=1000;
	private final int thresholdTime=1000*60;
	private final int thresholdTimes=60;
	private List<Item> user=new ArrayList<Item>(maxsize);
	private Set<String> ban=new HashSet<String>();
	public DDOSWall()
	{
		System.out.println("DDOS防火墙已启动\n当前配置:\n在"+thresholdTime/1000+"秒内访问接口"+thresholdTimes+"次封IP");
	}
	public  String getIpAddress(HttpServletRequest request) {
		HttpSession session=request.getSession();
		if(session.getAttribute("ip")!=null)
			return (String)session.getAttribute("ip");
		String ip =request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		session.setAttribute("ip", ip);
		return ip;
	}
	public synchronized boolean filt(String ipstr)
	{
		if(ban.contains(ipstr))
			return false;
		if(user.size()==maxsize)
			user.clear();
		user.add(new Item(ipstr));
		int log=0;
		Date d=new Date();
		for(int i=0;i<user.size();i++)
			if(user.get(i).getIp().equals(ipstr)&&(d.getTime()-user.get(i).getDate().getTime()<=thresholdTime))
			{
				if(++log==thresholdTimes)
				{
					ban.add(ipstr);
					user.clear();
					System.out.println("DDOSWall已经禁止 IP:"+ipstr);
					return false;
				}
			}
		System.out.println("DDOSWall-Log: IP:"+ipstr+" LogTimes:"+log+" ListSize:"+user.size());
		return true;
	}
	class Item{
		private String ip;
		private Date date;
		public Item(String ip)
		{
			this.ip=ip;
			this.date=new Date();
		}
		public String getIp()
		{
			return ip;
		}
		public Date getDate()
		{
			return date;
		}
	}
}
