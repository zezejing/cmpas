package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.Course;
import com.hnisc.cmpas.bean.Part;
import com.hnisc.cmpas.bean.Section;
import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.package_object.ResultData;
import com.hnisc.cmpas.service.IPartService;
import com.hnisc.cmpas.service.ISectionService;
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
@RequestMapping("/cmpas/section")
public class SectionController {
    @Autowired
    private ISectionService iSectionService;
    @Autowired
    private IPartService iPartService;
    @Autowired
    private ResultData resultData;
    @Autowired
    private CourseController courseController;
    @RequestMapping(value = "/create",method = {RequestMethod.GET},params = {"course_id","partId","name","instruction","task"})
    public ResultData create(HttpSession httpSession,Section section,String course_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            if (courseController.isMyCourse_Manage(httpSession,course_id).getResult().equals("true"))
            {
                section.setCreateTime(new Date());
                if (iSectionService.insert(section))
                    resultData.setResult("添加节成功");
                else
                    resultData.setResult("已存在同名节");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/getAllSectionByPartId",method = {RequestMethod.GET},params = {"course_id","part_id"})
    public List<Section>getAllSectionByPartId(HttpSession httpSession,String course_id,String part_id)
    {
        List<Section>list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&courseController.isLinked(httpSession,course_id))
        {
            Wrapper<Section>wrapper=new EntityWrapper<>();
            wrapper.eq("part_id",part_id);
            list=iSectionService.selectList(wrapper);
        }
        return list;
    }
    //通过id删除节
    @RequestMapping(value = "/deleteSectionById",method = {RequestMethod.GET},params = {"id"})
    public ResultData deleteSectionById(HttpSession httpSession,int id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Section section=iSectionService.selectById(id);
            if (section!=null)
            {
                Part part=iPartService.selectById(section.getPartId());
                if (part!=null)
                {
                    //是否是自己创建的课程
                    if (courseController.isMyCourse_Manage(httpSession,""+part.getCourseId()).getResult().equals("true"))
                    {
                        if (iSectionService.deleteById(section.getId()))
                            resultData.setResult("节删除成功");
                        else
                            resultData.setResult("节删除失败");
                    }
                    else
                        resultData.setResult("权限不足");
                }
                else
                    resultData.setResult("所属章不存在");
            }
            else
                resultData.setResult("该节不存在");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
    @RequestMapping(value = "/isLinked",method = {RequestMethod.GET},params = {"section_id"})
    public boolean isLinked(HttpSession httpSession,String section_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&section_id!="")
        {
            Section section=iSectionService.selectById(Integer.parseInt(section_id));
            if (section!=null)
            {
                Part part=iPartService.selectById(section.getPartId());
                return courseController.isLinked(httpSession,""+part.getCourseId());
            }
        }
        return false;
    }
    //查询节详情
    @RequestMapping(value = "/getSectionById",method = {RequestMethod.GET},params = {"section_id"})
    public Section getSectionById(HttpSession httpSession,String section_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null&&isLinked(httpSession,section_id))
            return iSectionService.selectById(Integer.parseInt(section_id));
        return null;
    }
    //通过节的id无鉴权直接删除节（已鉴权后的内部使用）
    protected void deleteSectionWithoutVerify(Integer id)
    {
        iSectionService.deleteById(id);
    }
}
