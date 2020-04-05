package com.hnisc.cmpas.bean;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 * ${table.comment}
 * </p>
 *
 * @author humorchen
 * @since 2019-06-02
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	private String phone;
	private String content;
	private Date sendtime;


	public Integer getId() {
		return id;
	}

	public Message setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public Message setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Message setContent(String content) {
		this.content = content;
		return this;
	}

	public Date getSendtime() {
		return sendtime;
	}

	public Message setSendtime(Date sendtime) {
		this.sendtime = sendtime;
		return this;
	}
	public boolean enable()
	{
		final int AliveTime=5*60*1000;
		if (sendtime!=null&&new Date().getTime()-sendtime.getTime()<AliveTime)
			return true;
		return false;
	}
}
