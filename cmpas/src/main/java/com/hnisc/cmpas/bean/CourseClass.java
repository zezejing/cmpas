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
public class CourseClass implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("course_id")
	private Integer courseId;
	private String name;
	@TableField("invitation_code")
	private String invitationCode;


	public Integer getId() {
		return id;
	}

	public CourseClass setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public CourseClass setCourseId(Integer courseId) {
		this.courseId = courseId;
		return this;
	}

	public String getName() {
		return name;
	}

	public CourseClass setName(String name) {
		this.name = name;
		return this;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public CourseClass setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
		return this;
	}

}
