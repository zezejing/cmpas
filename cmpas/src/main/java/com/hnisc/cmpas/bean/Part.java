package com.hnisc.cmpas.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * <p>
 * ${table.comment}
 * </p>
 *
 * @author humorchen
 * @since 2019-06-02
 */
@Repository
public class Part implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("course_id")
	private Integer courseId;
	private String name;
	private String introduction;
	@TableField("create_time")
	private Date createTime;


	public Integer getId() {
		return id;
	}

	public Part setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public Part setCourseId(Integer courseId) {
		this.courseId = courseId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Part setName(String name) {
		this.name = name;
		return this;
	}

	public String getIntroduction() {
		return introduction;
	}

	public Part setIntroduction(String introduction) {
		this.introduction = introduction;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Part setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	@Override
	public String toString() {
		return "Part{" +
				"id=" + id +
				", courseId=" + courseId +
				", name='" + name + '\'' +
				", introduction='" + introduction + '\'' +
				", createTime=" + createTime +
				'}';
	}
}
