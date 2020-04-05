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
public class Section implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("part_id")
	private Integer partId;
	private String name;
	private String instruction;
	private String task;
	@TableField("create_time")
	private Date createTime;


	public Integer getId() {
		return id;
	}

	public Section setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getPartId() {
		return partId;
	}

	public Section setPartId(Integer partId) {
		this.partId = partId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Section setName(String name) {
		this.name = name;
		return this;
	}

	public String getInstruction() {
		return instruction;
	}

	public Section setInstruction(String instruction) {
		this.instruction = instruction;
		return this;
	}

	public String getTask() {
		return task;
	}

	public Section setTask(String task) {
		this.task = task;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Section setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

}
