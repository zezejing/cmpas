package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.Dependency;
import com.hnisc.cmpas.bean.DependencyScore;
import com.hnisc.cmpas.bean.UploadFile;
import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.package_object.Student_Show;
import com.hnisc.cmpas.service.IDependencyScoreService;
import com.hnisc.cmpas.service.IDependencyService;
import com.hnisc.cmpas.service.IUploadFileService;
import com.hnisc.cmpas.util.MyExcelFileUtil;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * ${table.comment} 前端控制器
 * </p>
 *
 * @author humorchen
 * @since 2019-07-13
 */
@RestController
@RequestMapping("/cmpas/dependency")
public class DependencyController {
    @Autowired
	private IDependencyService iDependencyService;
    @Autowired
    private IUploadFileService iUploadFileService;
    @Autowired
	private ResultData resultData;
    @Autowired
    private CourseController courseController;
    @Autowired
    private CourseClassController courseClassController;
    @Autowired
    private DependencyScoreController dependencyScoreController;
    @Autowired
    private IDependencyScoreService iDependencyScoreService;
	@RequestMapping(value = "/addScoreDependency",method = {RequestMethod.GET},params = {"courseId","name","fullScore","weight","dataFile"})
	public ResultData addScoreDependency(HttpSession httpSession,Dependency dependency)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,dependency.getCourseId()+"").getResult().equals("true"))
            {
                try {
                    if (iDependencyService.insert(dependency))
                    {
                        //将成绩依据中的成绩添加到成绩依据表中
                        Wrapper<Dependency>dependencyWrapper=new EntityWrapper<>();
                        dependencyWrapper.eq("course_id",dependency.getCourseId());
                        dependencyWrapper.eq("name",dependency.getName());
                        Dependency dependency1=iDependencyService.selectOne(dependencyWrapper);
                        Wrapper<UploadFile>wrapper=new EntityWrapper<>();
                        wrapper.eq("url",dependency.getDataFile());
                        UploadFile uploadFile=iUploadFileService.selectOne(wrapper);
                        if (uploadFile!=null)
                        {
                            //读取文件解析
                            File file=new File(uploadFile.getFilePath());
                            try {
                                List <String>stuid=MyExcelFileUtil.readColumn(file,0);
                                stuid.remove(0);
                                List<String>mark=MyExcelFileUtil.readColumn(file,1);
                                mark.remove(0);
                                List<Student_Show>student_showList=courseClassController.getAllCourseClassStudent(httpSession,dependency.getCourseId()+"");
                                System.out.println(stuid.size()+" "+student_showList.size());
                                if (stuid.size()==student_showList.size())
                                {


                                    List<DependencyScore> dependencyScoreArrayList=new ArrayList<>();
                                    for (Student_Show student_show:student_showList)
                                    {
                                        int index=stuid.indexOf(student_show.getStuid());
                                        if (index>-1)
                                        {
                                            DependencyScore dependencyScore=new DependencyScore();
                                            dependencyScore.setDependencyId(dependency1.getId());
                                            dependencyScore.setUserId(student_show.getId());
                                            float stuscore=Float.parseFloat(mark.get(index));
                                            //得分不得高于总分
                                            if (stuscore>dependency.getFullScore())
                                            {
                                                resultData.setResult("学号:"+student_show.getStuid()+"得分:"+stuscore+"大于总分:"+dependency.getFullScore()+"！请检查后重试");
                                                return resultData;
                                            }
                                            float score=stuscore/dependency.getFullScore()*dependency.getWeight();
                                            dependencyScore.setSelfScore(Math.round(stuscore));
                                            dependencyScore.setFinalScore(Math.round(score));
                                            dependencyScoreArrayList.add(dependencyScore);
                                        }
                                        else
                                        {
                                            resultData.setResult("学号"+student_show.getStuid()+"不匹配,请检查后重试");
                                            return resultData;
                                        }
                                    }
                                    for (DependencyScore dependencyScore:dependencyScoreArrayList)
                                        dependencyScoreController.addDependencyScore(dependencyScore);
                                    resultData.setResult("添加成绩依据成功");
                                }
                                else
                                    resultData.setResult("学号不完全");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            resultData.setResult("数据文件不存在");
                        if(!(resultData.getResult().indexOf("成功")>-1))
                            iDependencyService.deleteById(dependency1.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resultData.setResult("不能存在同名成绩依据");
                }
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }

    @RequestMapping(value = "/delScoreDependency",method = {RequestMethod.GET},params = {"course_id","dependency_id"})
    public ResultData delScoreDependency(HttpSession httpSession,int course_id ,int dependency_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if(courseController.isMyCourse_Manage(httpSession,course_id+"").getResult().equals("true"))
            {
                Dependency dependency=iDependencyService.selectById(dependency_id);
                if (dependency!=null&&dependency.getCourseId().intValue()==course_id)
                {
                    Wrapper<DependencyScore>wrapper=new EntityWrapper<>();
                    wrapper.eq("dependency_id",dependency_id);
                    iDependencyScoreService.delete(wrapper);
                    iDependencyService.deleteById(dependency_id);
                    resultData.setResult("删除成功");
                }
                else
                    resultData.setResult("课程号不匹配");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;

    }
    @RequestMapping(value = "/getAllScoreDependency",method = {RequestMethod.GET},params = {"course_id"})
    public List<Dependency> getAllScoreDependency(HttpSession httpSession,int course_id)
    {
        List<Dependency> list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,course_id+"").getResult().equals("true"))
            {
                Wrapper<Dependency>wrapper=new EntityWrapper<>();
                wrapper.eq("course_id",course_id);
                list=iDependencyService.selectList(wrapper);
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return list;
    }
}
