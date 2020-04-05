package com.hnisc.cmpas.util;

import java.security.MessageDigest;

import org.springframework.stereotype.Component;


/*
 * MD5工具
 * 对传入的字符串使用MD5加密后return
 * Author:HumorChen
 * time:2019.3.22
 * email:humorchen@vip.qq.com
 */
@Component
public class MD5 {
	static MessageDigest md;
	static StringBuffer sb = new StringBuffer();
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String encode(String str) {

		try {
			sb.delete(0, sb.length());
			md.update(str.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					sb.append("0");
				sb.append(Integer.toHexString(i));
			}
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return sb.toString();
	}
}
