package com.hnisc.cmpas.controler;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hnisc.cmpas.bean.UploadFile;
import com.hnisc.cmpas.bean.User;
import com.hnisc.cmpas.service.IUploadFileService;
import com.hnisc.cmpas.service.IUserService;
import com.hnisc.cmpas.util.ParameterValidator;
import com.hnisc.cmpas.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
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
@RequestMapping("/cmpas/uploadFile")
public class UploadFileController {
    @Autowired
    private ParameterValidator parameterValidator;
    @Autowired
    private IUploadFileService iUploadFileService;
    @Autowired
    private IUserService iUserService;
    //项目文件存储目录
    private String filrdir="C:\\Storage\\cmpas";
    //单个文件大小限制
    private long limitSize=1024*1024*500;
    //上传多个文件
    @RequestMapping(value = "/uploadFiles",method = {RequestMethod.POST},params = {"user_id"})//,headers = {"enctype=multipart/form-data"}
    public String uploadFiles(String user_id,@RequestParam("files") List<MultipartFile> files)
    {
        StringBuilder stringBuilder=new StringBuilder();
//        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("files");
        System.out.println("文件数量:"+files.size());
        for (MultipartFile file:files)
            stringBuilder.append(saveFile(user_id,file)+";");
        return stringBuilder.toString();
    }

    //上传一个文件
    @RequestMapping(value = "/uploadFile",method = {RequestMethod.POST},params = {"user_id"})
    public String uploadFile(String user_id,@RequestParam("file") MultipartFile file,HttpSession httpSession)
    {
        System.out.println("收到请求"+file.isEmpty()+file.getOriginalFilename());
        return saveFile(user_id,file);
    }
    private String saveFile(String user_id, MultipartFile file)
    {
        if (user_id==null)
            return "请先登录";
        if(file.isEmpty())
            return "文件为空";
        if (file.getSize()>limitSize)
            return "文件超过大小限制";
        String filename=file.getOriginalFilename();
        long size=file.getSize();
        System.out.println("正在上传:"+filename+"-->"+((double)size/1024/1024));
        StringBuilder destpath=new StringBuilder(filrdir+File.separator+user_id+File.separator+Util.getDate()+"_"+filename);
        File dest=new File(destpath.toString());
        //保证目录存在
        Util.mkdir(dest.getParent());
        try {
            //生成URL
            String url=Util.mkURL(dest.getAbsolutePath());
            UploadFile uploadFile=new UploadFile();
            uploadFile.setFilePath(dest.getAbsolutePath());
            uploadFile.setUrl(url);
            uploadFile.setUserId(Integer.parseInt(user_id));
            uploadFile.setCreateTime(new Date());
            uploadFile.setFileName(filename);
            System.out.println(uploadFile);
            //存入数据库
            if (iUploadFileService.insert(uploadFile))
            {
                //将文件存储到本地
                file.transferTo(dest);
                //将URL返回
                return url;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传出错";
    }
    @RequestMapping(value = "/downloadByURL",params = {"url"})
    public void downloadByURL(String url, HttpServletResponse response)
    {
        Wrapper<UploadFile> wrapper=new EntityWrapper<>();
        wrapper.eq("url",url);
        UploadFile uploadFile=iUploadFileService.selectOne(wrapper);
        if(uploadFile!=null)
            try {
                response.reset();
                response.setContentType("multipart/form-data");
                response.setContentType("multipart/form-data;charset=UTF-8");//也可以明确的设置一下UTF-8，测试中不设置也可以。
                response.setHeader("Content-Disposition", "attachment; fileName="+  uploadFile.getFileName() +";filename*=utf-8''"+URLEncoder.encode(uploadFile.getFileName(),"UTF-8"));
                InputStream inStream = new FileInputStream(uploadFile.getFilePath());
                OutputStream os = response.getOutputStream();
                byte[] buff = new byte[1024];
                int len = -1;
                while ((len = inStream.read(buff)) > 0) {
                    os.write(buff, 0, len);
                }
                os.flush();
                os.close();
                inStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
