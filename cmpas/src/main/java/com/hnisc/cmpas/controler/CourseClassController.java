package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.ClassJoin;
import com.hnisc.cmpas.bean.Course;
import com.hnisc.cmpas.bean.CourseClass;
import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.package_object.Student_Show;
import com.hnisc.cmpas.service.IClassJoinService;
import com.hnisc.cmpas.service.ICourseClassService;
import com.hnisc.cmpas.service.ICourseService;
import com.hnisc.cmpas.service.IUserService;
import com.hnisc.cmpas.util.ParameterValidator;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author humorchen
 * @since 2019-06-01
 */
@RestController
@RequestMapping("/cmpas/courseClass")
public class CourseClassController {
    @Autowired
	private ICourseClassService iCourseClassService;
    @Autowired
    private ICourseService iCourseService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IClassJoinService iClassJoinService;
    @Autowired
    private ParameterValidator parameterValidator;
    @Autowired
    private CourseController courseController;
    @Autowired
    private ResultData resultData;
    @RequestMapping(value = "create",method = {RequestMethod.GET},params = {"courseId","name"})
    public ResultData create(HttpSession httpSession,CourseClass courseClass)
    {
        resultData.setResult(parameterValidator.validate(courseClass));
        if(parameterValidator.checkResult(resultData.getResult()))
        {
            User user=Util.getUserFromHttpSession(httpSession);
            if (user!=null)
            {
                if (user.getTitle()!=null)
                {
                    Course course=iCourseService.selectById(courseClass.getCourseId());
                    if (course!=null&&course.getUserId().equals(user.getId()))
                    {
                        courseClass.setInvitationCode(getInvitationCode());
                        if (iCourseClassService.insert(courseClass))
                            resultData.setResult("新建班级成功");
                        else
                            resultData.setResult("已存在同名班级");
                    }
                    else
                        resultData.setResult("您账号下不存在该课程");
                }
                else
                    resultData.setResult("请先完善信息");
            }
            else
                resultData.setResult("请先登录");
        }
        return resultData;
    }
    //删除班级(先删除ClassJogin再删除班级CourseClass)
    @RequestMapping(value = "/deleteCourseClassById",method = {RequestMethod.GET},params = {"course_id","course_class_id"})
    public ResultData deleteCourseClassById(HttpSession httpSession,String course_id,String course_class_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
            {
                Wrapper<ClassJoin>wrapper=new EntityWrapper<>();
                wrapper.eq("course_class_id",course_class_id);
                iClassJoinService.delete(wrapper);
                if (iCourseClassService.deleteById(Integer.parseInt(course_class_id)))
                    resultData.setResult("班级删除成功");
                else
                    resultData.setResult("班级删除失败");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    //查询某课程下全部班级
    @RequestMapping(value = "/getAllClassByCourseId",method = {RequestMethod.GET},params = {"course_id"})
    public List<CourseClass> getAllClassByCourseId(HttpSession httpSession,String course_id)
    {
        List<CourseClass>list=new LinkedList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isLinked(httpSession,course_id))
        {
            Wrapper<CourseClass>wrapper=new EntityWrapper<>();
            wrapper.eq("course_id",course_id);
            list=iCourseClassService.selectList(wrapper);
        }
        return list;
    }
    //查询课程下某班级所有学生的学号、姓名、班级
    @RequestMapping(value = "/getCourseClassStudent",method = {RequestMethod.GET},params = {"course_id","course_class_id"})
    public List<Student_Show> getCourseClassStudent(HttpSession httpSession, String course_id, String course_class_id)
    {
        List<Student_Show>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isLinked(httpSession,course_id))
        {
            CourseClass courseClass=iCourseClassService.selectById(Integer.parseInt(course_class_id));
            if (courseClass!=null)
            {
                Wrapper<ClassJoin>wrapper=new EntityWrapper<>();
                wrapper.eq("course_class_id",course_class_id);
                List<ClassJoin>classJoinList=iClassJoinService.selectList(wrapper);
                if (classJoinList.size()==0)
                    return list;
                Integer user_id[]=new Integer[classJoinList.size()];
                for (int i=0;i<user_id.length;i++)
                    user_id[i]=classJoinList.get(i).getUserId();
                Wrapper<User>userWrapper=new EntityWrapper<>();
                userWrapper.in("id",user_id);
                userWrapper.orderBy("register_time",false);
                userWrapper.setSqlSelect("id,stuid,realname");
                List<User>userList=iUserService.selectList(userWrapper);
                for (User user1:userList)
                {
                    Student_Show student_show=new Student_Show();
                    student_show.setId(user1.getId());
                    student_show.setCourse_class_id(Integer.parseInt(course_class_id));
                    student_show.setStuid(user1.getStuid());
                    student_show.setCourse_class_name(courseClass.getName());
                    student_show.setRealname(user1.getRealname());
                    list.add(student_show);
                }
            }

        }
        return list;
    }
    //查询课程下面所有班级的所有学生
    @RequestMapping(value = "/getAllCourseClassStudent",method = {RequestMethod.GET},params = {"course_id"})
    public List<Student_Show> getAllCourseClassStudent(HttpSession httpSession,String course_id)
    {
        List<Student_Show>list=new ArrayList<>();
        Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
        courseClassWrapper.eq("course_id",course_id);
        List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper);
        for (CourseClass courseClass:courseClassList)
            for (Student_Show student_show:getCourseClassStudent(httpSession,course_id,""+courseClass.getId()))
                list.add(student_show);
        return list;
    }
    //获取课程下班级个数
    @RequestMapping(value = "/getClassNumByCourseId",method = {RequestMethod.GET},params = {"course_id"})
    public int getClassNumByCourseId(HttpSession httpSession,String course_id)
    {
        if (courseController.isLinked(httpSession,course_id))
        {
            Wrapper<CourseClass>wrapper=new EntityWrapper<>();
            wrapper.eq("course_id",course_id);
            return iCourseClassService.selectCount(wrapper);
        }
        return -1;
    }
    //生成课程班级邀请码
    private String getInvitationCode()
    {
        String callback=Util.getRandomString(6);
        Wrapper<CourseClass>wrapper=new EntityWrapper<>();
        wrapper.eq("invitation_code",callback);
        CourseClass courseClass=iCourseClassService.selectOne(wrapper);
        if(courseClass==null)
            return callback;
        return getInvitationCode();
    }
}
