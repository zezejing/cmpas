package com.hnisc.cmpas.package_object;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * description:班级下学生列表的包装类（学号、姓名、班级名字）
 */
@Component
@Scope("prototype")
public class Student_Show {
    private Integer id;
    private String stuid;
    private String realname;
    private Integer course_class_id;
    private String course_class_name;

    public Integer getCourse_class_id() {
        return course_class_id;
    }

    public void setCourse_class_id(Integer course_class_id) {
        this.course_class_id = course_class_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStuid() {
        return stuid;
    }

    public void setStuid(String stuid) {
        this.stuid = stuid;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getCourse_class_name() {
        return course_class_name;
    }

    public void setCourse_class_name(String course_class_name) {
        this.course_class_name = course_class_name;
    }

    @Override
    public String toString() {
        return "Student_Show{" +
                "stuid='" + stuid + '\'' +
                ", realname='" + realname + '\'' +
                ", course_class_name='" + course_class_name + '\'' +
                '}';
    }
}
