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
public class Dependency implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("course_id")
	private Integer courseId;
	private String name;
	@TableField("full_score")
	private Integer fullScore;
	private Integer weight;
	@TableField("data_file")
	private String dataFile;


	public Integer getId() {
		return id;
	}

	public Dependency setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public Dependency setCourseId(Integer courseId) {
		this.courseId = courseId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Dependency setName(String name) {
		this.name = name;
		return this;
	}

	public Integer getFullScore() {
		return fullScore;
	}

	public Dependency setFullScore(Integer fullScore) {
		this.fullScore = fullScore;
		return this;
	}

	public Integer getWeight() {
		return weight;
	}

	public Dependency setWeight(Integer weight) {
		this.weight = weight;
		return this;
	}

	public String getDataFile() {
		return dataFile;
	}

	public Dependency setDataFile(String dataFile) {
		this.dataFile = dataFile;
		return this;
	}

}
