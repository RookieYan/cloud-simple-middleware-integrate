package com.yj.general.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: AliyunOssConfig
 * @Description:
 * @Author: yanjianxun
 * @Date 2022/2/11
 * @Version 1.0
 */
@Configuration
public class AliyunOssConfig implements InitializingBean {

    @Value("${aliyun.oss.file.endPoint}")
    private String endPoint;

    @Value("${aliyun.oss.file.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.file.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.file.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.file.objectNamePrefix}")
    private String objectNamePrefix;


    public static String OSS_END_POINT_IM;
    public static String OSS_BUCKET_IM;
    public static String OSS_ACCESS_KEY_ID_IM;
    public static String OSS_ACCESS_KEY_SECRET_IM;
    public static String OSS_OBJECT_NAME_PREFIX;
    public static String OSS_BUCKET_HOST;

    @Override
    public void afterPropertiesSet() throws Exception {
        OSS_END_POINT_IM = endPoint;
        OSS_BUCKET_IM = bucketName;
        OSS_ACCESS_KEY_ID_IM = accessKeyId;
        OSS_ACCESS_KEY_SECRET_IM = accessKeySecret;
        OSS_OBJECT_NAME_PREFIX = objectNamePrefix;
        OSS_BUCKET_HOST = String.format("%s.%s", bucketName, endPoint);
    }
}
