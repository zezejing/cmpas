package com.hnisc.cmpas.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * ${table.comment}
 * </p>
 *
 * @author humorchen
 * @since 2019-06-02
 */
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("user_id")
	private Integer userId;
	private String icon;
	private String name;
	private Integer credit;
	private Integer cycle;
	private String introduction;
	private String resource;
	private String instruction;
	private String iced;
	@TableField("create_time")
	private Date createTime;


	public Integer getId() {
		return id;
	}

	public Course setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public Course setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public Course setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public String getName() {
		return name;
	}

	public Course setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getCredit() {
		return credit;
	}

	public Course setCredit(Integer credit) {
		this.credit = credit;
		return this;
	}

	public Integer getCycle() {
		return cycle;
	}

	public Course setCycle(Integer cycle) {
		this.cycle = cycle;
		return this;
	}

	public String getIntroduction() {
		return introduction;
	}

	public Course setIntroduction(String introduction) {
		this.introduction = introduction;
		return this;
	}

	public String getResource() {
		return resource;
	}

	public Course setResource(String resource) {
		this.resource = resource;
		return this;
	}

	public String getInstruction() {
		return instruction;
	}

	public Course setInstruction(String instruction) {
		this.instruction = instruction;
		return this;
	}

	public String getIced() {
		return iced;
	}

	public Course setIced(String iced) {
		this.iced = iced;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Course setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

}
