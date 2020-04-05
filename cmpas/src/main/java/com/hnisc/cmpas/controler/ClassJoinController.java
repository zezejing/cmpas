package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.ClassJoin;
import com.hnisc.cmpas.bean.Course;
import com.hnisc.cmpas.bean.CourseClass;
import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.package_object.ResultData;
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
@RequestMapping("/cmpas/classJoin")
public class ClassJoinController {
    @Autowired
    private IClassJoinService iClassJoinService;
    @Autowired
    private ICourseClassService iCourseClassService;
    @Autowired
    private ICourseService iCourseService;
    @Autowired
    private CourseController courseController;
    @Autowired
    private ResultData resultData;
    //加入课程
    @RequestMapping(value = "/joinClass",method = {RequestMethod.GET},params = {"InvitationCode"})
	public ResultData joinClass(HttpSession httpSession,String InvitationCode)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
            courseClassWrapper.eq("invitation_code",InvitationCode);
            CourseClass courseClass=iCourseClassService.selectOne(courseClassWrapper);
            if (courseClass!=null)
            {
                Course course=iCourseService.selectById(courseClass.getCourseId());
                if (course==null)
                {
                    resultData.setResult("课程已被删除");
                    return resultData;
                }
                if (course.getUserId().intValue()==user.getId().intValue())
                {
                    resultData.setResult("不能加入自己创建的课程");
                    return resultData;
                }
                Wrapper<ClassJoin>classJoinWrapper=new EntityWrapper<>();
                classJoinWrapper.eq("course_class_id",courseClass.getId());
                classJoinWrapper.eq("user_id",user.getId());
                ClassJoin classJoin=iClassJoinService.selectOne(classJoinWrapper);
                Wrapper<CourseClass>courseClassWrapper1=new EntityWrapper<>();
                courseClassWrapper1.eq("course_id",courseClass.getCourseId());
                List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper1);
                for (CourseClass courseClass1:courseClassList)
                {
                    Wrapper<ClassJoin>classJoinWrapper1=new EntityWrapper<>();
                    classJoinWrapper1.eq("course_class_id",courseClass1.getId());
                    List<ClassJoin>classJoinList=iClassJoinService.selectList(classJoinWrapper);
                    for (ClassJoin classJoin1:classJoinList)
                        if (classJoin1.getUserId().equals(user.getId()))
                        {
                            resultData.setResult("您已加入本课程下班级，不可再次加入");
                            return resultData;
                        }
                }
                if(classJoin==null)
                {
                    classJoin=new ClassJoin();
                    classJoin.setUserId(user.getId());
                    classJoin.setCourseClassId(courseClass.getId());
                    if (iClassJoinService.insert(classJoin))
                        resultData.setResult("加入课程成功");
                    else
                        resultData.setResult("您已加入本课程");
                }
                else
                    resultData.setResult("您已加入本课程");
            }
            else
                resultData.setResult("邀请码无效");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    //删除学生
    @RequestMapping(value = "/deleteStudentById",method = {RequestMethod.GET},params = {"course_id","course_class_id","user_id"})
    public ResultData deleteStudentById(HttpSession httpSession,String course_id,String course_class_id,String user_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
            {
                Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
                courseClassWrapper.eq("course_id",course_id);
                List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper);
                boolean delete=false;
                if (courseClassList.size()>0)
                {
                    for(int i=0;i<courseClassList.size();i++)
                        if (course_id.equals(""+courseClassList.get(i).getCourseId()))
                        {
                            delete=true;
                            break;
                        }
                }
                if (delete)
                {
                    Wrapper<ClassJoin>wrapper=new EntityWrapper<>();
                    wrapper.eq("user_id",user_id);
                    wrapper.eq("course_class_id",course_class_id);
                    if (iClassJoinService.delete(wrapper))
                        resultData.setResult("删除学生成功");
                    else
                        resultData.setResult("删除学生失败（不存在）");
                }
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    //查询多少学生加入了某个课程
    @RequestMapping(value = "/getStudentNumByCourseId",method = {RequestMethod.GET},params = {"course_id"})
    public int getStudentNumByCourseId(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            //查询出课程下面所有班级
            Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
            courseClassWrapper.eq("course_id",course_id);
            List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper);
            if (courseClassList.size()>0)
            {
                //查询课程下面所有学生
                Integer course_class_id[]=new Integer[courseClassList.size()];
                for (int i=0;i<courseClassList.size();i++)
                    course_class_id[i]=courseClassList.get(i).getId();
                Wrapper<ClassJoin>classJoinWrapper=new EntityWrapper<>();
                classJoinWrapper.in("course_class_id",course_class_id);
                return iClassJoinService.selectCount(classJoinWrapper);
            }
        }
        return 0;
    }
    //查询某个班级下面有多少学生
    @RequestMapping(value = "/getStudentNumByCourseClassId",params = {"course_class_id"})
    public int getStudentNumByCourseClassId(HttpSession httpSession,String course_class_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Wrapper<ClassJoin>wrapper=new EntityWrapper<>();
            wrapper.eq("course_class_id",course_class_id);
            return iClassJoinService.selectCount(wrapper);
        }
        return 0;
    }
}
