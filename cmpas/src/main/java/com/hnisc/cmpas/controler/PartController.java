package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
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
@RequestMapping("/cmpas/part")
public class PartController {
	@Autowired
    private IPartService iPartService;
	@Autowired
    private ISectionService iSectionService;
    @Autowired
    private ResultData resultData;

    @Autowired
    private CourseController courseController;
    @Autowired
    private SectionController sectionController;

    @RequestMapping(value = "/create",method = {RequestMethod.GET},params = {"courseId","name","introduction"})
    public ResultData create(HttpSession httpSession,Part part)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        resultData=courseController.isMyCourse_Manage(httpSession,""+part.getCourseId());
        if (resultData.getResult().equals("true"))
        {
            part.setCreateTime(new Date());
            if (iPartService.insert(part))
                resultData.setResult("添加章成功");
            else
                resultData.setResult("已存在同名章");
        }
        return resultData;
    }
    //获取课程下所有的章
    @RequestMapping(value = "/getAllPartByCourseId",method = {RequestMethod.GET},params = {"course_id"})
    public List<Part> getAllPartByCourseId(HttpSession httpSession,String course_id)
    {
        List<Part> list=new ArrayList<>();
        User user=Util.getUserFromHttpSession(httpSession);
        if (courseController.isLinked(httpSession,course_id))
        {
            Wrapper<Part>wrapper=new EntityWrapper<>();
            wrapper.eq("course_id",course_id);
            list=iPartService.selectList(wrapper);
        }
        return list;
    }
    //删除章
    @RequestMapping(value = "/deletePartById",method = {RequestMethod.GET},params = {"part_id"})
    public ResultData deletePartById(HttpSession httpSession,String part_id)
    {
        User user=Util.getUserFromHttpSession(httpSession);
        if (user!=null)
        {
            Part part=iPartService.selectById(Integer.parseInt(part_id));
            if (part!=null&&courseController.isMyCourse_Manage(httpSession,""+part.getCourseId()).getResult().equals("true"))
            {
                Wrapper<Section>sectionWrapper=new EntityWrapper<>();
                sectionWrapper.eq("part_id",part.getId());
                List<Section>sectionList=iSectionService.selectList(sectionWrapper);
                for (Section section:sectionList)
                    sectionController.deleteSectionWithoutVerify(section.getId());
                if (iPartService.deleteById(part.getId()))
                    resultData.setResult("章删除成功");
                else
                    resultData.setResult("章删除失败");
            }
            else
                resultData.setResult("权限不足");
        }
        else
            resultData.setResult("请先登录");
        return resultData;
    }
}
