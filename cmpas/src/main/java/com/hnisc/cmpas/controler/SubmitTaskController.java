package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.*;
import com.hnisc.cmpas.mapper.SubmitTaskMapper;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.package_object.Student_Show;
import com.hnisc.cmpas.package_object.SubmitTask_Show;
import com.hnisc.cmpas.service.*;
import com.hnisc.cmpas.util.DuplicateDetection;
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
 * @since 2019-06-01
 */
@RestController
@RequestMapping("/cmpas/submitTask")
public class SubmitTaskController {
    @Autowired
	private ISubmitTaskService iSubmitTaskService;
    @Autowired
    private ICourseService iCourseService;
    @Autowired
    private ICourseClassService iCourseClassService;
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IClassJoinService iClassJoinService;
    @Autowired
    private IUploadFileService iUploadFileService;
    @Autowired
    private IPartService iPartService;
    @Autowired
    private ISectionService iSectionService;
    @Autowired
    private CourseController courseController;
    @Autowired
    private PartController partController;
    @Autowired
    private SectionController sectionController;
    @Autowired
    private CourseClassController courseClassController;
    @Autowired
    private UploadFileController uploadFileController;

    @Autowired
    private ResultData resultData;
    @RequestMapping(value = "/submitTask",params = {"sectionId","content"})
    public ResultData submitTask(HttpSession httpSession, SubmitTask submitTask)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Section section=iSectionService.selectById(submitTask.getSectionId());
            if (section!=null)
            {
                Part part=iPartService.selectById(section.getPartId());
                if (part!=null)
                {
                    if (courseController.isMyCourse_Study(httpSession,""+part.getCourseId()).getResult().equals("true"))
                    {
                        submitTask.setUserId(user.getId());
                        submitTask.setSubmitTime(new Date());
                        System.out.println("要插入的数据:");
                        System.out.println(submitTask.getContent());
                        try {
                            if (iSubmitTaskService.insert(submitTask))
                                resultData.setResult("作业提交成功");
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                            resultData.setResult("您已提交过作业了，不能重复提交");
                        }
                    }
                    else
                        resultData.setResult("权限不足");
                }
                else
                    resultData.setResult("该节所属章已被删除");
            }
            else
                resultData.setResult("该节已被删除");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/isSubmited",method = {RequestMethod.GET},params = {"section_id"})
    public boolean isSubmited(HttpSession httpSession,String section_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        System.out.println(user==null);
        if (user!=null)
        {
            Wrapper<SubmitTask>wrapper=new EntityWrapper<>();
            wrapper.eq("section_id",section_id);
            wrapper.eq("user_id",user.getId());
            System.out.println(user.getId()+" "+section_id);
            SubmitTask submitTask=iSubmitTaskService.selectOne(wrapper);
            if (submitTask!=null)
                return true;
        }
        return false;
    }
    @RequestMapping(value = "/getSubmitTaskScoreExcel",method = {RequestMethod.GET},params = {"course_id"})
    public  void getSubmitTaskScoreExcel(HttpSession httpSession, String course_id, HttpServletResponse httpResponse)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
        {
            Course course=courseController.getCourseById(httpSession,course_id,null);
            StringBuilder path=new StringBuilder();
            path.append("C:\\Storage\\cmpas\\"+user.getId().intValue());
            path.append("\\"+course.getName()+"_"+course.getId().intValue()+"_实训任务_成绩表_"+Util.getDate()+".xlsx");
            File file=new File(path.toString());
//            if (file.exists())
//            {
//                //存在直接返回
//                Wrapper<UploadFile>uploadFileWrapper=new EntityWrapper<>();
//                uploadFileWrapper.eq("file_path",file.getAbsolutePath());
//                UploadFile uploadFile=iUploadFileService.selectOne(uploadFileWrapper);
//                if (uploadFile!=null)
//                {
//                    uploadFileController.downloadByURL(uploadFile.getUrl(),httpResponse);
//                    return;
//                }
//            }
            List<Part>partList=partController.getAllPartByCourseId(httpSession,course_id);
            List<Section> sectionList=new ArrayList<>();
            if (partList.size()>0)
            {
                for (Part part:partList)
                    for (Section section:sectionController.getAllSectionByPartId(httpSession,course_id,part.getId().intValue()+""))
                        sectionList.add(section);
                if (sectionList.size()>0)
                {
                    List<Student_Show> student_showList=courseClassController.getAllCourseClassStudent(httpSession,course_id);
                    if (student_showList.size()>0)
                    {
                        Map<Integer,Float>stuid_score_map=new HashMap<>();
                        //初始化哈希表
                        for (int i=0;i<student_showList.size();i++)
                            stuid_score_map.put(student_showList.get(i).getId(),0.0f);
                        for (Section section:sectionList)
                        {
                            Wrapper<SubmitTask> submitTaskWrapper=new EntityWrapper<>();
                            submitTaskWrapper.eq("section_id",section.getId());
                            List<SubmitTask>submitTaskList=iSubmitTaskService.selectList(submitTaskWrapper);
                            for (SubmitTask submitTask:submitTaskList)
                                stuid_score_map.put(submitTask.getUserId(),stuid_score_map.get(submitTask.getUserId())+submitTask.getScore());
                        }
                        for (Map.Entry<Integer,Float> entry:stuid_score_map.entrySet())
                            stuid_score_map.put(entry.getKey(),entry.getValue()/sectionList.size());
                        String[][] data=new String [stuid_score_map.size()+1][2];
                        data[0][0]="学号";
                        data[0][1]="得分（各实训任务的平均分）";
                        for(int i=1;i<=stuid_score_map.size();i++)
                        {
                            Student_Show student_show=student_showList.get(i-1);
                            data[i][0]=student_show.getStuid();
                            data[i][1]=stuid_score_map.get(student_show.getId()).floatValue()+"";
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
        }
    }
    @RequestMapping(value = "/getSubmitTaskBySectionId",method = {RequestMethod.GET},params = {"user_id","section_id"})
    public SubmitTask getSubmitTaskBySectionId(HttpSession httpSession,String user_id,String section_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Wrapper<SubmitTask>wrapper=new EntityWrapper<>();
            wrapper.eq("user_id",user_id);
            wrapper.eq("section_id",section_id);
            return iSubmitTaskService.selectOne(wrapper);
        }
        return null;
    }
    @RequestMapping(value = "/getSubmitTaskListByClassId",method = {RequestMethod.GET},params = {"class_id","section_id"})
    public List<SubmitTask_Show> getSubmitTaskListByClassId(HttpSession httpSession,String class_id,String section_id)
    {
        List<SubmitTask_Show>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
               CourseClass courseClass=iCourseClassService.selectById(class_id);
               if (courseClass!=null&&courseController.isMyCourse_Manage(httpSession,courseClass.getCourseId()+"").getResult().equals("true"))
               {
                   Wrapper<ClassJoin>classJoinWrapper=new EntityWrapper<>();
                   classJoinWrapper.eq("course_class_id",courseClass.getId());
                   List<ClassJoin>classJoinList=iClassJoinService.selectList(classJoinWrapper);
                   if (classJoinList.size()>0)
                   {
                       Integer []user_id_array=new Integer[classJoinList.size()];
                       for (int i=0;i<classJoinList.size();i++)
                           user_id_array[i]=classJoinList.get(i).getUserId();
                       Wrapper<User>userWrapper=new EntityWrapper<>();
                       userWrapper.in("id",user_id_array);
                       List<User>userList=iUserService.selectList(userWrapper);
                       for (int i=0;i<userList.size();i++)
                       {
                           SubmitTask_Show submitTask_show=new SubmitTask_Show();
                           submitTask_show.setClassname(courseClass.getName());
                           submitTask_show.setName(userList.get(i).getRealname());
                           submitTask_show.setUser_id(userList.get(i).getId());
                           submitTask_show.setStuid(userList.get(i).getStuid());
                           Wrapper<SubmitTask>submitTaskWrapper=new EntityWrapper<>();
                           submitTaskWrapper.eq("user_id",userList.get(i).getId());
                           submitTaskWrapper.eq("section_id",section_id);
                           SubmitTask submitTask=iSubmitTaskService.selectOne(submitTaskWrapper);
                           if (submitTask==null)
                           {
                               submitTask_show.setStatus(false);
                               submitTask_show.setScore(0);
                           }
                           else
                           {
                               submitTask_show.setStatus(true);
                               submitTask_show.setScore(submitTask.getScore());
                           }
                           list.add(submitTask_show);
                       }
                   }
               }
        }
        return list;
    }
    @RequestMapping(value = "/getAllSubmitTaskByCourseId",method = {RequestMethod.GET},params = {"course_id","section_id"})
    public List<SubmitTask_Show> getAllSubmitTaskByCourseId(HttpSession httpSession,String course_id,String section_id)
    {
        List<SubmitTask_Show> list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if(user!=null&&courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
        {
            Wrapper<CourseClass>courseClassWrapper=new EntityWrapper<>();
            courseClassWrapper.eq("course_id",course_id);
            List<CourseClass>courseClassList=iCourseClassService.selectList(courseClassWrapper);
            for (CourseClass courseClass:courseClassList)
                for (SubmitTask_Show submitTask_show:getSubmitTaskListByClassId(httpSession,courseClass.getId()+"",section_id))
                    list.add(submitTask_show);
        }
        return list;
    }
    @RequestMapping(value = "/reviewSubmitTask",method = {RequestMethod.GET},params = {"user_id","section_id","score"})
    public ResultData reviewSubmitTask(HttpSession httpSession,String user_id,String section_id,Integer score)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            try {
                if (courseController.isMyCourse_Manage(httpSession,""+iPartService.selectById(iSectionService.selectById(section_id).getPartId()).getCourseId()).getResult().equals("true"))
                {
                    Wrapper<SubmitTask>wrapper=new EntityWrapper<>();
                    wrapper.eq("user_id",user_id);
                    wrapper.eq("section_id",section_id);
                    SubmitTask submitTask=iSubmitTaskService.selectOne(wrapper);
                    if (submitTask!=null)
                    {
                        submitTask.setScore(score);
                        submitTask.setReviewerId(user.getId());
                        submitTask.setReviewTime(new Date());
                        if (iSubmitTaskService.updateById(submitTask))
                            resultData.setResult("批阅成功");
                        else
                            resultData.setResult("批阅失败");
                    }else
                        resultData.setResult("该生作业未提交，无法打分");
                }
            }catch (Exception e)
            {
                resultData.setResult("权限不足");
            }
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/duplicateDetection",method = {RequestMethod.GET},params = {"course_id","section_id","user_id"})
    public String duplicateDetection(HttpSession httpSession,String course_id,String section_id,String user_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
        {
            Section section=sectionController.getSectionById(httpSession,section_id);
            if (section!=null)
            {
                Part part=iPartService.selectById(section.getPartId());
                if (part!=null)
                {
                    if (course_id.equals(part.getCourseId().intValue()+""))
                    {
                        Wrapper<SubmitTask>submitTaskWrapper=new EntityWrapper<>();
                        submitTaskWrapper.eq("section_id",section_id);
                        List<SubmitTask> list=iSubmitTaskService.selectList(submitTaskWrapper);
                        if (list.size()>1)
                        {
                            float max=0.00f;
                            String user_content="";
                            String []others=new String[list.size()-1];
                            int otherindex=0;
                            for (SubmitTask submitTask:list)
                                if (user_id.equals(submitTask.getUserId().intValue()+""))
                                    user_content=submitTask.getContent();
                                else
                                {
                                    others[otherindex]=submitTask.getContent();
                                    otherindex++;
                                }
                            for (int i=0;i<others.length;i++)
                            {
                                float now=DuplicateDetection.detect(user_content,others[i]);
                                if (now>max)
                                    max=now;
                            }
                             return DuplicateDetection.transferFloatToPersentString(max);
                        }

                    }
                }
            }
        }
        return "0.00%";
    }
}
