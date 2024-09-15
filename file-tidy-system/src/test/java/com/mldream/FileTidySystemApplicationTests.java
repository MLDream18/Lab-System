package com.mldream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mldream.mapper.ExperimentProjectMapper;
import com.mldream.mapper.TeacherMapper;
import com.mldream.pojo.db.Admin;
import com.mldream.pojo.db.Teacher;
import com.mldream.pojo.dto.User;
import com.mldream.pojo.vo.ExperimentProjectVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.utils.ExcelUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
class FileTidySystemApplicationTests {

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ExperimentProjectMapper experimentProjectMapper;

    @Test
    public void test() throws IOException {
        String filePath = "D:\\大二下课设\\人工智能学院教职工工号2024年4月24日84人.xls";

        // 创建MockMultipartFile对象
        File file = new File(filePath);
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/vnd.ms-excel", input);


        List<String[]> list = ExcelUtils.XLSHandle(multipartFile);
        List<String> teachers = teacherMapper.getAllTeachers().stream().map(Teacher::getName).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        List<Teacher> newTeachers = new ArrayList<>();
        list.remove(0); // 移除标题行
        list.remove(0); // 移除标题行
        for (String[] arr : list) {
            Teacher teacher = new Teacher(arr[1].replaceAll(" ", ""));
            if(!teachers.contains(teacher.getName())) {
                newTeachers.add(teacher);
            }
//            int account = Integer.parseInt(arr[2]);
//            teacher.setUsername(arr[2]);
//            teacherMapper.setDefaultAccount(teacher);
        }
        if(!newTeachers.isEmpty()) {
            teacherMapper.addTeachers(newTeachers);
        }
        for (String[] arr : list) {
            Teacher teacher = new Teacher(arr[1].replaceAll(" ", ""));
//            int account = Integer.parseInt(arr[2]);
            teacher.setUsername(arr[2]);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = encoder.encode("123456");
            teacher.setPassword(password);
//            log.info("teacher: {}", teacher);
            teacherMapper.setDefaultAccount(teacher);
//            log.info("teacherPassword: {}", encoder.matches("123456",teacher.getPassword()));
//            System.out.println(teacher.getName() + " " + arr[2]);
        }
    }

//    @Autowired
//    private BaseMapper<Admin> adminMapper;
//
//    @Test
//    public void test1() throws Exception {
//        List<Admin> admins = adminMapper.selectList(null);
//        for (Admin admin : admins) {
//            teacherMapper.insertUser(User.builder().username(admin.getUsername()).password(admin.getPassword()).role("admin").userId(admin.getId()).build());
//        }
//        teacherMapper.selectList(null).forEach(item -> {
//            if(item.getUsername() != null && !item.getUsername().isEmpty()) {
//                teacherMapper.insertUser(User.builder().username(item.getUsername()).password(item.getPassword()).role("teacher").userId(item.getId()).build());
//            }
//        });
//    }

//    @Test
//    public void test2() throws Exception {
//        // 调用 COS 接口之前必须保证本进程存在一个 COSClient 实例，如果没有则创建
//        COSClient cosClient = createCOSClient();
//
//        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
//        String bucketName = "file-tidy-1328756109";
//        // 对象键(Key)是对象在存储桶中的唯一标识。
//        String key = "datagrip-2023.3.4.exe";
//        // 本地文件路径
//        String localFilePath = "D:\\Download\\datagrip-2023.3.4.exe";
//        File localFile = new File(localFilePath);
//
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
//
//        // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
//        // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
//        putObjectRequest.setStorageClass(StorageClass.Standard_IA);
//
//        try {
//            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
//            System.out.println(putObjectResult.getRequestId());
//        } catch (CosClientException e) {
//            e.printStackTrace();
//        } finally {
//            // 确认本进程不再使用 cosClient 实例之后，关闭即可
//            cosClient.shutdown();
//        }
//    }
//
//    // 创建 COSClient 实例，这个实例用来后续调用请求
//    COSClient createCOSClient() {
//        // 设置用户身份信息。
//        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
//        String secretId = System.getenv("COS_SECRET_ID");//用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
//        String secretKey = System.getenv("COS_SECRET_KEY");//用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
//        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
//
//        // ClientConfig 中包含了后续请求 COS 的客户端设置：
//        ClientConfig clientConfig = new ClientConfig();
//
//        // 设置 bucket 的地域
//        // COS_REGION 请参见 https://cloud.tencent.com/document/product/436/6224
//        clientConfig.setRegion(new Region("ap-beijing"));
//
//        // 以下的设置，是可选的：
//
//        // 设置 socket 读取超时，默认 30s
//        // clientConfig.setSocketTimeout(30*1000);
//        // 设置建立连接超时，默认 30s
//        // clientConfig.setConnectionTimeout(30*1000);
//
//        // 如果需要的话，设置 http 代理，ip 以及 port
//        // clientConfig.setHttpProxyIp("httpProxyIp");
//        // clientConfig.setHttpProxyPort(80);
//
//        // 生成 cos 客户端。
//        return new COSClient(cred, clientConfig);
//    }

//    @Test
//    public void test3() throws Exception {
//        // 分页测试
//        int pageSize = 10;
//        int pageNum = 1;
//        Page<ExperimentProjectVO> pageParam = new Page<>(pageNum, pageSize);
//        QueryWrapper<ExperimentProjectVO> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("semester", "2024-2025-1");
//        queryWrapper.eq("classId", "%");
//        queryWrapper.eq("teacherName", "%");
//        queryWrapper.eq("courseName", "%");
//
//        Page<ExperimentProjectVO> page = experimentProjectMapper.selectPage(pageParam, queryWrapper);
//        PageBean pageBean = new PageBean(page.getTotal(), page.getRecords());
//
//        System.out.println(pageBean.toString());
//    }

}
