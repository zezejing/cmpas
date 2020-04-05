package com.hnisc.cmpas.bean;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
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
@TableName("[User]")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	private String phone;
	private String password;
	private String nickname;
	private String iced;
	@TableField("register_time")
	private Date registerTime;
	private String realname;
	private String stuid;
	private String gender;
	private String email;
	private String career;
	private String title;
	private String school;
	private String institude;


	public Integer getId() {
		return id;
	}

	public User setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public User setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getNickname() {
		return nickname;
	}

	public User setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public String getIced() {
		return iced;
	}

	public User setIced(String iced) {
		this.iced = iced;
		return this;
	}

	public Date getRegisterTime() {
		return registerTime;
	}

	public User setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
		return this;
	}

	public String getRealname() {
		return realname;
	}

	public User setRealname(String realname) {
		this.realname = realname;
		return this;
	}

	public String getStuid() {
		return stuid;
	}

	public User setStuid(String stuid) {
		this.stuid = stuid;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public User setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getCareer() {
		return career;
	}

	public User setCareer(String career) {
		this.career = career;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public User setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getSchool() {
		return school;
	}

	public User setSchool(String school) {
		this.school = school;
		return this;
	}

	public String getInstitude() {
		return institude;
	}

	public User setInstitude(String institude) {
		this.institude = institude;
		return this;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", phone='" + phone + '\'' +
				", password='" + password + '\'' +
				", nickname='" + nickname + '\'' +
				", iced='" + iced + '\'' +
				", registerTime=" + registerTime +
				", realname='" + realname + '\'' +
				", stuid='" + stuid + '\'' +
				", gender='" + gender + '\'' +
				", email='" + email + '\'' +
				", career='" + career + '\'' +
				", title='" + title + '\'' +
				", school='" + school + '\'' +
				", institude='" + institude + '\'' +
				'}';
	}
}
