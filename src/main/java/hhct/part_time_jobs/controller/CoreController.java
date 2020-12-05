package hhct.part_time_jobs.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author:liamg
 * @Date:2020/12/5
 */
@Controller
@RequestMapping(value = "/Core")
public class CoreController {
    @Resource
    private Producer producer;
    /**
     * 返回验证码到Session
     * 并且使用流方式响应验证码给客户端
     * @param session
     * @param response
     * @return void
     * @Author Ryo
     * @create 2020/12/5 17:24
    */
    @GetMapping(value = "/ValidateCode/Get")
    public void validateCode(HttpSession session, HttpServletResponse response) {
        //获取验证码字符串
        String kaptcha = producer.createText();
        //保留验证码，存储在session中
        session.setAttribute("kaptcha", kaptcha);
        //把字符串加干扰线装进图片
        BufferedImage image = producer.createImage(kaptcha);
        //然后用流的方式把图片响应给请求端
        try {
            ImageIO.write(image, "jpg", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 返回验证码到Session
     * 并且使用流方式响应验证码给客户端
     * @param request
     * @param folder
     * @param multipartFiles
     * @return Integer
     * @Author Ryo
     * @create 2020/12/5 17:24
     */
    @ResponseBody
    @PostMapping(value = "/File/Uploading")
    public Integer FileUploading(HttpServletRequest request, @RequestParam(value = "folder")String folder,@RequestParam(value = "multipartFiles") List<MultipartFile> multipartFiles) {
        //        文件夹名称
        String realPath = request.getServletContext().getRealPath(folder);
        File file = new File(realPath);
        if(!file.exists())
        {
//            创建目录
            file.mkdirs();
        }
        for(MultipartFile m:multipartFiles){
//            开始在文件下读写文件
            try {
                m.transferTo(new File(realPath+m.getOriginalFilename()));
            } catch (IOException e) {
                e.printStackTrace();
                return 500;
            }
        }
        return 200;
    }

    @ResponseBody
    @GetMapping(value = "/File/Drop")
    public void FileDrop(@RequestParam String folder,@RequestParam List<String> names,HttpServletRequest request,HttpSession session){
        String realPath = request.getServletContext().getRealPath(folder);
        File file = new File(realPath);
        if(!file.exists()){
//          不存在当前目录
            session.setAttribute("ret",400);
            return;
        }
        for(String s :names){
//            删除当前文件夹下的文件
            File file1 = new File(realPath + s);
            if(file1.isFile()){
                file1.delete();
            }
        }
        session.setAttribute("ret",200);
    }
}
