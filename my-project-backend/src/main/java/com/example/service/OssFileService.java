package com.example.service;


import com.aliyun.oss.OSSClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;

public interface OssFileService {

    String upload(MultipartFile file);

    String uploadAvatar(MultipartFile file,int id);

    void fetchImageFromAliOss(OutputStream stream,String image) throws IOException;
}
