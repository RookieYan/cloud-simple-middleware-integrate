package com.yj.general.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.yj.general.config.AliyunOssConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @ClassName: AliyunOssTemplate
 * @Description:
 * @Author: yanjianxun
 * @Date 2022/2/11
 * @Version 1.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class AliyunOssTemplate {

    /**
     * 存放文件的路径
     */
    private final String prefix = AliyunOssConfig.OSS_OBJECT_NAME_PREFIX;

    /**
     * OSS 设置的 bucket 名称
     */
    private final String bucket = AliyunOssConfig.OSS_BUCKET_IM;

    /**
     * 你的 bucket 外网访问域名，https://oss-cn-guangzhou.aliyuncs.com
     */
    private final String endpoint = AliyunOssConfig.OSS_END_POINT_IM;

    /**
     * 拼接返回url要用的，bucket + 域名 例如： j3-communication.oss-cn-guangzhou.aliyuncs.com
     */
    private final String bucketHost = AliyunOssConfig.OSS_BUCKET_HOST;

    private final String accessKeyId = AliyunOssConfig.OSS_ACCESS_KEY_ID_IM;
    private final String accessKeySecret = AliyunOssConfig.OSS_ACCESS_KEY_SECRET_IM;

    /**
     * 单个文件上传
     *
     * @param file
     * @return
     */
    public String upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String path = String.format("%s%s", prefix, originalFilename);
        try {
            // 上传
            ossUpload(bucket, path, file.getInputStream());
        } catch (IOException e) {
            log.error("文件上传失败！");
//            throw new SysException("文件上传失败！");
        }
        // 因为 oss 不会返回访问 url 所以我们自己可以拼接一个：
        // 协议（https://） + 域名访问地址（j3-communication.oss-cn-guangzhou.aliyuncs.com） + 自己拼接的路径（xxx/xxx/a.jpg）
        return String.format("%s%s%s%s", "https://", bucketHost, "/", path);
    }

    /**
     * 具体上传代码
     *
     * @param bucket      backet名称
     * @param path        路径
     * @param inputStream 文件流
     * @return
     */
    private PutObjectResult ossUpload(String bucket, String path, InputStream inputStream) {

        // 创建OSSClient实例
        PutObjectResult putObjectResult = null;
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, inputStream);
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);
            // 上传文件
            putObjectResult = ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return putObjectResult;
    }
}
