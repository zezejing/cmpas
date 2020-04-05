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
 * @since 2019-07-14
 */
public class DependencyScore implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("dependency_id")
	private Integer dependencyId;
	@TableField("user_id")
	private Integer userId;
	@TableField("self_score")
	private Integer selfScore;
	@TableField("final_score")
	private Integer finalScore;


	public Integer getId() {
		return id;
	}

	public DependencyScore setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDependencyId() {
		return dependencyId;
	}

	public DependencyScore setDependencyId(Integer dependencyId) {
		this.dependencyId = dependencyId;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public DependencyScore setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getSelfScore() {
		return selfScore;
	}

	public DependencyScore setSelfScore(Integer selfScore) {
		this.selfScore = selfScore;
		return this;
	}

	public Integer getFinalScore() {
		return finalScore;
	}

	public DependencyScore setFinalScore(Integer finalScore) {
		this.finalScore = finalScore;
		return this;
	}

}
