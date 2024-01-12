package com.example.controller;

import com.aliyun.oss.OSSException;
import com.example.entity.RestBean;
import com.example.service.OssFileService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class OssFileController {

    @Resource
    OssFileService ossFileService;

    @PostMapping("/avatar")
    public RestBean<String> uploadAvatar(@RequestParam("file")MultipartFile file,
                                         @RequestAttribute("id") int id){
        if(file.getSize()>1024*100)//文件大小需要小于100kb
            return RestBean.failure(400,"头像图片大小过大,不能超过100kb");
        log.info("正在进行头像上传操作...");
        String url = ossFileService.uploadAvatar(file,id);
        if(url!=null){
            log.info("头像上传成功!");
            return RestBean.success(url);
        }else{
            log.info("头像上传失败");
            return RestBean.failure(400,"头像上传失败,请联系管理员");
        }
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file){
        String imgFileStr = ossFileService.upload(file);
        return buildResult(imgFileStr);
    }

    private Map<String,Object> buildResult(String str){
        Map<String, Object> result = new HashMap<>();
        //判断字符串用lang3下的StringUtils去判断，这块我就不引入新的依赖了
        if(str== null || "".equals(str)){
            result.put("code",10000);
            result.put("msg","图片上传失败");
            result.put("data",null);
        }else{
            result.put("code",200);
            result.put("msg","图片上传成功");
            result.put("data",str);
        }
        return result;
    }

    @GetMapping("/getFile/image/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.fetchImages(request,response);
    }

    private void fetchImages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(24);
        ServletOutputStream outputStream = response.getOutputStream();
        if(imagePath.length()<13){
            outputStream.println(RestBean.failure(404,"File Not Found").toString());
        }else{
            try {
                ossFileService.fetchImageFromAliOss(outputStream,imagePath);
                response.setHeader("Cache-control","max-age=2592000");//设置浏览器最大缓存时间
            }catch (OSSException e){
                if(e.getErrorCode()=="404"){
                    response.setStatus(404);
                    outputStream.println(RestBean.failure(404,"File Not Found").toString());
                }else{
                    log.error("从AliOss获取图片出现异常: "+ e.getMessage(),e);
                }
            }

        }
    }




}
