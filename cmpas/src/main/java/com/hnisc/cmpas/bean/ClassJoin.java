package com.hnisc.cmpas.bean;

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
public class ClassJoin implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("user_id")
	private Integer userId;
	@TableField("course_class_id")
	private Integer courseClassId;


	public Integer getId() {
		return id;
	}

	public ClassJoin setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public ClassJoin setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getCourseClassId() {
		return courseClassId;
	}

	public ClassJoin setCourseClassId(Integer courseClassId) {
		this.courseClassId = courseClassId;
		return this;
	}

	@Override
	public String toString() {
		return "ClassJoin{" +
				"id=" + id +
				", userId=" + userId +
				", courseClassId=" + courseClassId +
				'}';
	}
}
