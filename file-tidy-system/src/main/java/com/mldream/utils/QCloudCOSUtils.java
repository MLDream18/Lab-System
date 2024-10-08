package com.mldream.utils;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Component
public class QCloudCOSUtils {

    @Autowired
    private QCloudCOSUtilsProperties properties;

    public String upload(File file) throws Exception {
        String endpoint = properties.getEndpoint();
        String bucketName = properties.getBucketName();

//        InputStream inputStream = file.getInputStream();

        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
        COSClient cosClient = createCOSClient();

        // 获取文件原始名称
        String originalFilename = file.getName();

        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        // 对象键(Key)是对象在存储桶中的唯一标识。
        String key = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        // 本地文件路径
//        String localFilePath = "D:\\Download\\datagrip-2023.3.4.exe";
//        File localFile = new File(localFilePath);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);

        // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
        // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
        putObjectRequest.setStorageClass(StorageClass.Standard_IA);

        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

        String url = endpoint + "/" + key;

//        System.out.println(putObjectResult.getRequestId());
        cosClient.shutdown();
        return url;
    }

    // 创建 COSClient 实例，这个实例用来后续调用请求
    COSClient createCOSClient() {
        // 设置用户身份信息。
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        String secretId = System.getenv("COS_SECRET_ID");//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        String secretKey = System.getenv("COS_SECRET_KEY");//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        ClientConfig clientConfig = new ClientConfig();

        // 设置 bucket 的地域
        // COS_REGION 请参见 https://cloud.tencent.com/document/product/436/6224
        clientConfig.setRegion(new Region("ap-beijing"));

        // 以下的设置，是可选的：

        // 设置 socket 读取超时，默认 30s
        // clientConfig.setSocketTimeout(30*1000);
        // 设置建立连接超时，默认 30s
        // clientConfig.setConnectionTimeout(30*1000);

        // 如果需要的话，设置 http 代理，ip 以及 port
        // clientConfig.setHttpProxyIp("httpProxyIp");
        // clientConfig.setHttpProxyPort(80);

        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }
}
