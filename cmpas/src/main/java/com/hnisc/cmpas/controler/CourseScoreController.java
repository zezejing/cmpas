package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.*;
import com.hnisc.cmpas.package_object.CourseScoreDetail;
import com.hnisc.cmpas.package_object.CourseScore_Show;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.package_object.Student_Show;
import com.hnisc.cmpas.service.*;
import com.hnisc.cmpas.util.MyExcelFileUtil;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author humorchen
 * @since 2019-07-13
 */
@RestController
@RequestMapping("/cmpas/courseScore")
public class CourseScoreController {
    @Autowired
	private IDependencyScoreService iDependencyScoreService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICourseClassService iCourseClassService;
    @Autowired
    private IClassJoinService iClassJoinService;
    @Autowired
    private ICourseScoreService iCourseScoreService;
    @Autowired
    private IUploadFileService iUploadFileService;
    @Autowired
    private IDependencyService iDependencyService;
    @Autowired
    private CourseController courseController;
    @Autowired
    private CourseClassController courseClassController;
    @Autowired
    private UploadFileController uploadFileController;
    @Autowired
    private ResultData resultData;
    @RequestMapping(value = "/publishCourseScore",method = {RequestMethod.GET},params = {"course_id"})
    public ResultData publishCourseScore(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
            {
                //检测有没有结算过成绩了
                Wrapper<CourseScore>courseScoreWrapper=new EntityWrapper<>();
                courseScoreWrapper.eq("course_id",course_id);
                List<CourseScore> courseScoreList=iCourseScoreService.selectList(courseScoreWrapper);
                //如果已经发布过成绩了就清空成绩
                if (courseScoreList.size()>0)
                {
                    resetCourseScore(httpSession,course_id);
                }
                Wrapper<Dependency>dependencyWrapper=new EntityWrapper<>();
                dependencyWrapper.eq("course_id",course_id);
                List<Dependency>dependencyList=iDependencyService.selectList(dependencyWrapper);
                if (dependencyList.size()>0)
                {
                    List<DependencyScore> dependencyScoreList=new ArrayList<>();
                    for (Dependency dependency:dependencyList)
                    {
                        Wrapper<DependencyScore>dependencyScoreWrapper=new EntityWrapper<>();
                        dependencyScoreWrapper.eq("dependency_id",dependency.getId());
                        List<DependencyScore>dependencyScoreList1=iDependencyScoreService.selectList(dependencyScoreWrapper);
                        for (DependencyScore dependencyScore:dependencyScoreList1)
                            dependencyScoreList.add(dependencyScore);
                    }
                    List<Student_Show>student_showList=courseClassController.getAllCourseClassStudent(httpSession,course_id);
                    Map<Integer,Integer>UserId_Score_Map=new HashMap<>();
                    for(int i=0;i<student_showList.size();i++)
                        UserId_Score_Map.put(student_showList.get(i).getId(),0);
                    for (DependencyScore dependencyScore:dependencyScoreList)
                    {
                        Integer value=UserId_Score_Map.get(dependencyScore.getUserId());
                        if (value==null)
                            UserId_Score_Map.put(dependencyScore.getUserId(),dependencyScore.getFinalScore());
                        else
                            UserId_Score_Map.put(dependencyScore.getUserId(),value+dependencyScore.getFinalScore());
                    }
                    for (Map.Entry<Integer,Integer>entry:UserId_Score_Map.entrySet())
                    {
                        CourseScore courseScore=new CourseScore();
                        courseScore.setCourseId(Integer.parseInt(course_id));
                        courseScore.setUserId(entry.getKey());
                        courseScore.setScore(entry.getValue());
                        iCourseScoreService.insert(courseScore);
                    }
                    resultData.setResult("成绩发布成功");
                }
                else
                    resultData.setResult("请先添加成绩依据后再发布成绩");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/resetCourseScore",method = {RequestMethod.GET},params = {"course_id"})
    public ResultData resetCourseScore(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
            {
                Wrapper<CourseScore>wrapper=new EntityWrapper<>();
                wrapper.eq("course_id",course_id);
                iCourseScoreService.delete(wrapper);
                resultData.setResult("重置成绩成功，可重新发布成绩");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/getCourseScore",method = {RequestMethod.GET},params = {"course_id"})
    public List<CourseScore_Show> getCourseScore(HttpSession httpSession,String course_id)
    {
        List<CourseScore_Show>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isLinked(httpSession,course_id))
        {
            Wrapper<CourseScore>wrapper=new EntityWrapper<>();
            wrapper.eq("course_id",course_id);
            List<CourseScore>courseScoreList=iCourseScoreService.selectList(wrapper);
            for (CourseScore courseScore:courseScoreList)
            {
                User user1=iUserService.selectById(courseScore.getUserId());
                CourseScore_Show courseScore_show=new CourseScore_Show();
                courseScore_show.setUser_id(user1.getId());
                courseScore_show.setStuid(user1.getStuid());
                Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
                courseClassWrapper.eq("course_id",course_id);
                List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper);
                if (courseClassList.size()>0)
                {
                    String []classes=new String[courseClassList.size()];
                    for (int i=0;i<classes.length;i++)
                        classes[i]=courseClassList.get(i).getId().intValue()+"";
                    Wrapper<ClassJoin>classJoinWrapper=new EntityWrapper<>();
                    classJoinWrapper.eq("user_id",user1.getId());
                    classJoinWrapper.in("course_class_id",classes);
                    ClassJoin classJoin=iClassJoinService.selectOne(classJoinWrapper);
                    if (classJoin!=null)
                    {
                        for(int i=0;i<courseClassList.size();i++)
                            if (courseClassList.get(i).getId().intValue()==classJoin.getCourseClassId().intValue())
                            {
                                courseScore_show.setClassname(courseClassList.get(i).getName());
                                break;
                            }
                        courseScore_show.setName(user1.getRealname());
                        courseScore_show.setScore(courseScore.getScore());
                        list.add(courseScore_show);
                    }
                }
            }
        }
        return list;
    }
    @RequestMapping(value = "/getCourseScoreDetail",method = {RequestMethod.GET},params = {"course_id","user_id"})
    public List<CourseScoreDetail> getCourseScoreDetail(HttpSession httpSession, String course_id, String user_id)
    {
        List<CourseScoreDetail>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isLinked(httpSession,course_id))
        {
            Wrapper<DependencyScore>wrapper=new EntityWrapper<>();
            wrapper.eq("user_id",user_id);
            List<DependencyScore>dependencyScoreList=iDependencyScoreService.selectList(wrapper);
            for (DependencyScore dependencyScore:dependencyScoreList)
            {
                Dependency dependency=iDependencyService.selectById(dependencyScore.getDependencyId());
                CourseScoreDetail courseScoreDetail=new CourseScoreDetail();
                courseScoreDetail.setName(dependency.getName());
                courseScoreDetail.setFinal_score(dependencyScore.getFinalScore());
                courseScoreDetail.setFull_score(dependency.getFullScore());
                courseScoreDetail.setSelf_score(dependencyScore.getSelfScore());
                courseScoreDetail.setWeight(dependency.getWeight());
                list.add(courseScoreDetail);
            }
        }
        return list;
    }
    @RequestMapping(value = "/isPublished",method = {RequestMethod.GET},params = {"course_id"})
    public boolean isPublished(HttpSession httpSession,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Wrapper<CourseScore>wrapper=new EntityWrapper<>();
            wrapper.eq("course_id",course_id);
            List<CourseScore>list=iCourseScoreService.selectList(wrapper);
            if (list.size()>0)
                return true;
        }
        return false;
    }
    @RequestMapping(value = "/getCourseScoreExcel",method = {RequestMethod.GET},params = {"course_id"})
    public  void getCourseScoreExcel(HttpSession httpSession, String course_id, HttpServletResponse httpResponse)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
        {
            Course course=courseController.getCourseById(httpSession,course_id,null);
            StringBuilder path=new StringBuilder();
            path.append("C:\\Storage\\cmpas\\"+user.getId().intValue());
            path.append("\\"+course.getName()+"_"+course.getId().intValue()+"_成绩表_"+Util.getDate()+".xlsx");
            File file=new File(path.toString());

            List<CourseScore_Show>list=getCourseScore(httpSession,course_id);
            String [][]data=new String[list.size()+1][5];
            String []header={"序号","学号","姓名","班级","成绩"};
            for (int i=0;i<header.length;i++)
                data[0][i]=header[i];
            for (int i=1;i<=list.size();i++)
            {
                data[i][0]=i+"";
                data[i][1]=list.get(i-1).getStuid();
                data[i][2]=list.get(i-1).getName();
                data[i][3]=list.get(i-1).getClassname();
                data[i][4]=list.get(i-1).getScore().intValue()+"";
            }
            //写入文件
            MyExcelFileUtil.writeDataToFile(data,file);
            //创建上传文件bean
            String url=Util.mkURL(file.getAbsolutePath());
            UploadFile uploadFile=new UploadFile();
            uploadFile.setFilePath(file.getAbsolutePath());
            uploadFile.setUrl(url);
            uploadFile.setUserId(user.getId());
            uploadFile.setCreateTime(new Date());
            uploadFile.setFileName(file.getName());
            System.out.println(uploadFile);
            //存入数据库
            if (iUploadFileService.insert(uploadFile))
            {
                uploadFileController.downloadByURL(url,httpResponse);
            }
        }
    }
}
