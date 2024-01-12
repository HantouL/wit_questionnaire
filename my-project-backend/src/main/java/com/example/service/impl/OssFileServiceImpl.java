package com.example.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.config.AliOssConfiguration;
import com.example.entity.dao.Account;
import com.example.mapper.AccountMapper;
import com.example.service.OssFileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class OssFileServiceImpl implements OssFileService {

    @Resource
    AliOssConfiguration ossConfiguration;

    @Resource
    AccountMapper accountMapper;

    @Override
    public String upload(MultipartFile file) {

        String bucketName = ossConfiguration.getBucketName();
        String endPoint = ossConfiguration.getEndPoint();
        String accessKeyId = ossConfiguration.getAccessKeyId();
        String accessKeySecret = ossConfiguration.getAccessKeySecret();

        OSSClient ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);

        String originalFilename = file.getOriginalFilename();
        LocalDateTime time = LocalDateTime.now();
        String folder = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(time);
        String filename = UUID.randomUUID().toString().replaceAll("-","");
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String uploadFileName = "user/"+folder+"/"+filename+extension;

        try {
            PutObjectResult result = ossClient.putObject(bucketName, uploadFileName, file.getInputStream());
        } catch (IOException e) {
            log.error("文件上传失败:{}",e.getMessage());
            throw new RuntimeException(e);
        }finally {
            ossClient.shutdown();//关闭oss服务
        }

        return null;
    }

    @Override
    public String uploadAvatar(MultipartFile file, int id) {

        String bucketName = ossConfiguration.getBucketName();
        String endPoint = ossConfiguration.getEndPoint();
        String accessKeyId = ossConfiguration.getAccessKeyId();
        String accessKeySecret = ossConfiguration.getAccessKeySecret();

        String imageName = UUID.randomUUID().toString().replaceAll("-","");

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        imageName = "avatar/"+imageName+extension;

        OSSClient ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);

        try {
            ossClient.putObject(bucketName, imageName, file.getInputStream());
            if(accountMapper.update(null, Wrappers.<Account>update().eq("id",id).set("avatar",imageName))>0){
                return imageName;
            }else {
                return null;
            }
        } catch (IOException e) {
            log.error("文件上传失败:{}",e.getMessage());
            throw new RuntimeException(e);
        }finally {
            ossClient.shutdown();//关闭oss服务
        }
    }

    @Override
    public void fetchImageFromAliOss(OutputStream stream, String image) {

        String bucketName = ossConfiguration.getBucketName();
        String endPoint = ossConfiguration.getEndPoint();
        String accessKeyId = ossConfiguration.getAccessKeyId();
        String accessKeySecret = ossConfiguration.getAccessKeySecret();

        OSSClient ossClient = new OSSClient(endPoint, accessKeyId, accessKeySecret);
        try{
            OSSObject object = ossClient.getObject(bucketName, image);
            IOUtils.copy(object.getObjectContent(),stream);

            object.close();
        }catch (OSSException e){

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

    }


}
