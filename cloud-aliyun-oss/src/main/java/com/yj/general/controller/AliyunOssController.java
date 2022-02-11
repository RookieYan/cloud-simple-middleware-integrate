package com.yj.general.controller;

import com.yj.general.utils.AliyunOssTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

/**
 * @ClassName: AliyunOssController
 * @Description:
 * @Author: yanjianxun
 * @Date 2022/2/11
 * @Version 1.0
 */
@RequestMapping("/oss")
@RestController
public class AliyunOssController {

    @Autowired
    private AliyunOssTemplate aliyunOssTemplate;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public String upload(MultipartFile file) {
        return aliyunOssTemplate.upload(file);
    }
}
