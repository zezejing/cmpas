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
 * @since 2019-07-13
 */
public class CourseScore implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("course_id")
	private Integer courseId;
	@TableField("user_id")
	private Integer userId;
	private Integer score;


	public Integer getId() {
		return id;
	}

	public CourseScore setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public CourseScore setCourseId(Integer courseId) {
		this.courseId = courseId;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public CourseScore setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getScore() {
		return score;
	}

	public CourseScore setScore(Integer score) {
		this.score = score;
		return this;
	}

}
