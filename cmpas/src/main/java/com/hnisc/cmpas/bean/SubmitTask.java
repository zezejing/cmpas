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
public class SubmitTask implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("user_id")
	private Integer userId;
	@TableField("section_id")
	private Integer sectionId;
	private String content;
	@TableField("files_url")
	private String filesUrl;
	@TableField("submit_time")
	private Date submitTime;
	private Integer score;
	private String comment;
	@TableField("reviewer_id")
	private Integer reviewerId;
	@TableField("review_time")
	private Date reviewTime;


	public Integer getId() {
		return id;
	}

	public SubmitTask setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public SubmitTask setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public Integer getSectionId() {
		return sectionId;
	}

	public SubmitTask setSectionId(Integer sectionId) {
		this.sectionId = sectionId;
		return this;
	}

	public String getContent() {
		return content;
	}

	public SubmitTask setContent(String content) {
		this.content = content;
		return this;
	}

	public String getFilesUrl() {
		return filesUrl;
	}

	public SubmitTask setFilesUrl(String filesUrl) {
		this.filesUrl = filesUrl;
		return this;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public SubmitTask setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
		return this;
	}

	public Integer getScore() {
		return score;
	}

	public SubmitTask setScore(Integer score) {
		this.score = score;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public SubmitTask setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Integer getReviewerId() {
		return reviewerId;
	}

	public SubmitTask setReviewerId(Integer reviewerId) {
		this.reviewerId = reviewerId;
		return this;
	}

	public Date getReviewTime() {
		return reviewTime;
	}

	public SubmitTask setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
		return this;
	}

}
