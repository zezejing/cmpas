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
 * @since 2019-06-04
 */
public class UploadFile implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	@TableField("user_id")
	private Integer userId;
	@TableField("file_name")
	private String fileName;
	@TableField("file_path")
	private String filePath;
	private String url;
	@TableField("create_time")
	private Date createTime;


	public Integer getId() {
		return id;
	}

	public UploadFile setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getUserId() {
		return userId;
	}

	public UploadFile setUserId(Integer userId) {
		this.userId = userId;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public UploadFile setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	public String getFilePath() {
		return filePath;
	}

	public UploadFile setFilePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public UploadFile setUrl(String url) {
		this.url = url;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public UploadFile setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	@Override
	public String toString() {
		return "UploadFile{" +
				"id=" + id +
				", userId=" + userId +
				", fileName='" + fileName + '\'' +
				", filePath='" + filePath + '\'' +
				", url='" + url + '\'' +
				", createTime=" + createTime +
				'}';
	}
}
