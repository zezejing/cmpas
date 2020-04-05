package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.*;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.package_object.TeacherInfo;
import com.hnisc.cmpas.service.*;
import com.hnisc.cmpas.util.ParameterValidator;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/cmpas/course")
public class CourseController {
    @Autowired
    private ICourseService iCourseService;
    @Autowired
    private ICourseClassService iCourseClassService;
    @Autowired
    private IUploadFileService iUploadFileService;
    @Autowired
    private IClassJoinService iClassJoinService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ParameterValidator parameterValidator;
    @Autowired
    private TeacherInfo teacherInfo;
    @Autowired
    private ResultData resultData;
    //新建课程
    @RequestMapping(value = "/create",method = {RequestMethod.GET},params = {"icon","name","credit","cycle","introduction","instruction"})
    public ResultData create(HttpSession httpSession, Course course)
    {
        //验证用户是否登陆、是否为教师
        User user=Util.getUserFromHttpSession(httpSession);
        if (user==null)
        {
            resultData.setResult("请先登录");
        }
        else if (user.getTitle()==null)
            resultData.setResult("请先完善账号信息后方可新建课程");
        else
        {
            resultData.setResult(parameterValidator.validate(course));
            if (parameterValidator.checkResult(resultData.getResult()))
            {
                //设置创建时间
                course.setCreateTime(new Date());
                course.setResource("");
                course.setUserId(user.getId());
                if (iCourseService.insert(course))
                    resultData.setResult("新建课程成功");
                else
                    resultData.setResult("已存在同名课程");
            }
        }
        return resultData;
    }
    //查询是否是自己学习的课程
    @RequestMapping(value = "/isMyCourse_Study",method = {RequestMethod.GET},params = {"course_id"})
    public ResultData isMyCourse_Study(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if(user!=null)
        {
            Wrapper<CourseClass> courseClassWrapper=new EntityWrapper<>();
            courseClassWrapper.eq("course_id",course_id);
            List<CourseClass> courseClassList=iCourseClassService.selectList(courseClassWrapper);
            if (courseClassList.size()>0)
            {
                for (CourseClass courseClass:courseClassList)
                {
                    Wrapper<ClassJoin> classJoinWrapper=new EntityWrapper<>();
                    classJoinWrapper.eq("course_class_id",courseClass.getId());
                    classJoinWrapper.eq("user_id",user.getId());
                    ClassJoin classJoin=iClassJoinService.selectOne(classJoinWrapper);
                    if (classJoin!=null)
                    {
                        resultData.setResult("true");
                        return resultData;
                    }
                }
            }
            resultData.setResult("false");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    //查询是否是自己创建的课程
    @RequestMapping(value = "/isMyCourse_Manage",method = {RequestMethod.GET},params = {"course_id"})
    public ResultData  isMyCourse_Manage(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&user.getId()!=null)
        {
            Wrapper<Course>wrapper=new EntityWrapper<>();
            wrapper.eq("user_id",user.getId());
            wrapper.eq("id",course_id);
            Course course=iCourseService.selectOne(wrapper);
            if (course!=null)
                resultData.setResult("true");
            else
                resultData.setResult("false");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    //获取课程教师和学校
    @RequestMapping(value = "/getTeacherInfoByCourseId",method = {RequestMethod.GET},params = {"course_id"})
    public TeacherInfo getTeacherInfoByCourseId(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        Course course=iCourseService.selectById(Integer.parseInt(course_id));
        if (user!=null&&course!=null&&isLinked(httpSession,course_id))
        {
            User teacher=iUserService.selectById(course.getUserId());
            teacherInfo.setName(teacher.getRealname());
            teacherInfo.setSchool(teacher.getSchool());
        }
        return teacherInfo;
    }
    //获取课程资源
    @RequestMapping(value = "/getCourseResource",method = {RequestMethod.GET},params = {"course_id"})
    public List<UploadFile> getCourseResourse(HttpSession httpSession,String course_id)
    {
        List<UploadFile>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (isLinked(httpSession,course_id))
            {
                Course course=iCourseService.selectById(Integer.parseInt(course_id));
                if (course!=null)
                {
                    String urls[]=course.getResource().split(";");
                    if (urls!=null&&urls.length>0)
                    {
                        Wrapper<UploadFile>uploadFileWrapper=new EntityWrapper<>();
                        uploadFileWrapper.in("url",urls);
//                        uploadFileWrapper.setSqlSelect("file_name,url");
                        list=iUploadFileService.selectList(uploadFileWrapper);
                    }

                }
            }
        }
        return list;
    }
    //删除课程(实际上是将iced字段设置为是表示该课程无效了)
    @RequestMapping(value = "/deleteByCourseId",method = {RequestMethod.GET},params = {"course_id"})
    public ResultData deleteByCourseId(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if(user!=null&&isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
        {
            Wrapper<Course>wrapper=new EntityWrapper<>();
            wrapper.eq("id",course_id);
            wrapper.eq("iced","否");
            Course course=iCourseService.selectOne(wrapper);
            if (course!=null)
            {
                //防止删除后使用该名字创建课程时显示课程已存在，对名字重命名
                course.setName(course.getName()+"_deleted_"+Util.getDate());
                course.setIced("是");
                if (iCourseService.updateById(course))
                    resultData.setResult("删除成功");
                else
                    resultData.setResult("删除失败");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登陆");
        return resultData;
    }
    @RequestMapping(value = "/selectALL_Manage",method = {RequestMethod.GET})
    public List<Course> selectAll_Manage(HttpSession httpSession)
    {
        List<Course>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&user.getId()!=null)
        {
            Wrapper<Course>wrapper=new EntityWrapper<>();
            wrapper.eq("user_id",user.getId());
            wrapper.eq("iced","否");
            wrapper.orderBy("id",false);
            list=iCourseService.selectList(wrapper);
        }
        return list;
    }
    @RequestMapping(value = "/selectALL_Study",method = {RequestMethod.GET})
    public List<Course> selectALL_Study(HttpSession httpSession)
    {
        List<Course>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
                //查询出所有课程加入记录
               Wrapper<ClassJoin>classJoinWrapper=new EntityWrapper<>();
               classJoinWrapper.eq("user_id",user.getId());
               List<ClassJoin>classJoinList=iClassJoinService.selectList(classJoinWrapper);
               Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
               //查询出自己加入的所有班级
               Integer course_class_id[]=new Integer[classJoinList.size()];
               if (classJoinList.size()>0)
               {
                   for (int i=0;i<classJoinList.size();i++)
                       course_class_id[i]=classJoinList.get(i).getCourseClassId();
                   courseClassWrapper.in("id",course_class_id);
                   List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper);
                   //查询出班级对应的课程
                   Integer course_id[]=new Integer[courseClassList.size()];
                   for (int i=0;i<courseClassList.size();i++)
                       course_id[i]=courseClassList.get(i).getCourseId();
                   Wrapper<Course>courseWrapper=new EntityWrapper<>();
                   courseWrapper.in("id",course_id);
                   courseWrapper.eq("iced","否");
                   courseWrapper.orderBy("id",false);
                   list=iCourseService.selectList(courseWrapper);
               }
        }
        return list;
    }
    private boolean isIced(String course_id)
    {
        Wrapper<Course>wrapper=new EntityWrapper<>();
        wrapper.eq("id",course_id);
        wrapper.eq("iced","否");
        if (iCourseService.selectOne(wrapper)==null)
            return true;
        return false;
    }
    //上传课程资源
    @RequestMapping(value = "/addResource",method = {RequestMethod.GET},params = {"course_id","resourse_url"})
    public ResultData addResource(HttpSession httpSession,String course_id,String resourse_url,Course course)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        System.out.println(user);
        if (user!=null)
        {
            Wrapper<Course>wrapper=new EntityWrapper<>();
            wrapper.eq("id",course_id);
            wrapper.eq("user_id",user.getId());
            course=iCourseService.selectOne(wrapper);
            if(course!=null)
            {
                course.setResource(course.getResource()+resourse_url);
                if (iCourseService.updateById(course))
                    resultData.setResult("添加成功");
                else
                    resultData.setResult("添加失败");
            }
            else
                resultData.setResult("课程不存在");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    //删除课程资源
    @RequestMapping(value = "/delResource",method = {RequestMethod.GET},params = {"course_id","url"})
    public boolean delResource(HttpSession httpSession,int course_id,String url)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        System.out.println("1");
        if (user!=null)
        {System.out.println("2");
            Wrapper<UploadFile>wrapper=new EntityWrapper<>();
            wrapper.eq("url",url);
            wrapper.eq("user_id",user.getId());
            UploadFile uploadFile=iUploadFileService.selectOne(wrapper);
            if (uploadFile!=null)
            {System.out.println("3");
                //删除课程资源
                Course course=iCourseService.selectById(course_id);
                if (course!=null&&course.getUserId().intValue()==user.getId().intValue())
                {System.out.println("4");
                    String resourse=course.getResource();
                    System.out.println(resourse);
                    System.out.println(Util.isNullStr(resourse));
                    if (!Util.isNullStr(resourse))
                    {System.out.println("5");
                        //存在该课程资源
                        if(resourse.indexOf(url)>-1)
                        {System.out.println("6");
                            resourse=resourse.replace(url+";","");
                            course.setResource(resourse);
                            System.out.println("7");
                            if (iCourseService.updateById(course))
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    @RequestMapping(value = "/getResource",method = {RequestMethod.GET},params = {"course_id"})
    public ResultData getResource(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (isLinked(httpSession,course_id))
            {
                Wrapper<Course>wrapper=new EntityWrapper<>();
                wrapper.eq("id",course_id);
                wrapper.eq("user_id",user.getId());
                Course course=iCourseService.selectOne(wrapper);
                if (course!=null)
                    resultData.setResult(course.getResource());
                else
                    resultData.setResult("课程不存在");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/getCourseById",method = {RequestMethod.GET},params = {"course_id"})
    public Course getCourseById(HttpSession httpSession,String course_id,Course course)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if(user!=null&&isLinked(httpSession,course_id))
        {
            Wrapper<Course>wrapper=new EntityWrapper<>();
            wrapper.eq("id",course_id);
            course=iCourseService.selectOne(wrapper);
        }
        return course;
    }
    @RequestMapping(value = "/getAllLatest8Course",method = {RequestMethod.GET})
    public List<Course> getAllLatest8Course()
    {
        List<Course>list;
        EntityWrapper<Course>wrapper=new EntityWrapper<>();
        wrapper.eq("iced","否");
        wrapper.setSqlSelect("top 8 id,icon,name");
        wrapper.orderBy("create_time",false);
        list=iCourseService.selectList(wrapper);
        return list;
    }
    @RequestMapping(value = "/isLinked",method = {RequestMethod.GET},params = {"course_id"})
    public  boolean isLinked(HttpSession httpSession,String course_id)
    {
        return isMyCourse_Manage(httpSession,course_id).getResult().equals("true")||isMyCourse_Study(httpSession,course_id).getResult().equals("true");
    }
}
