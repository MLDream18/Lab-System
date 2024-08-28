package com.mldream.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mldream.pojo.vo.ExperimentProjectVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ExperimentProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController("adminExperimentProjectController")
@RequestMapping("/admin")
public class ExperimentProjectController {

    @Autowired
    private ExperimentProjectService experimentProjectService;

    @PostMapping("/experiment-project/select-project-data/{pageSize}/{currentPage}")
    public Result selectProjectData(@PathVariable("pageSize") Integer pageSize, @PathVariable("currentPage") Integer currentPage, @RequestBody Map<String, Object> params) {
        String courseName = (String) params.get("courseName");
        StringBuilder cn = new StringBuilder();
        cn.append("%");
        for (int i = 0; i < courseName.length(); i++) {
            cn.append(courseName.charAt(i)).append("%");
        }
        params.put("courseName", cn.toString());

        String teacherName = (String) params.get("teacherName");
        StringBuilder tn = new StringBuilder();
        tn.append("%");
        for (int i = 0; i < teacherName.length(); i++) {
            tn.append(teacherName.charAt(i)).append("%");
        }
        params.put("teacherName", tn.toString());

        String classId = params.get("classId").toString();
        StringBuilder ci = new StringBuilder();
        ci.append("%");
        for (int i = 0; i < classId.length(); i++) {
            ci.append(classId.charAt(i)).append("%");
        }
        params.put("classId", ci.toString());

        PageBean result = experimentProjectService.selectProjectData(pageSize, currentPage, params);
        return Result.success(result);
    }

    @GetMapping("/experiment-project/select-project-all/{semester}")
    public Result selectProjectAll(@PathVariable String semester) {
        Map<String, Object> params = new ConcurrentHashMap<>();
        params.put("semester", semester);
        return Result.success(experimentProjectService.selectProjectAll(params));
    }

}
