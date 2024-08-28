package com.mldream.controller.admin;

import com.mldream.pojo.db.ClassTime;
import com.mldream.pojo.db.CourseSerial;
import com.mldream.pojo.db.Semester;
import com.mldream.pojo.dto.ClassTimeInsertDTO;
import com.mldream.pojo.vo.ClassTimeInfoVO;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ClassScheduleService;
import com.mldream.service.ClassTimeService;
import com.mldream.service.CommencementPlanService;
import com.mldream.service.CourseSerialService;
import com.mldream.utils.ExcelUtils;
import com.mldream.utils.SemesterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin/class-time")
public class ClassTimeController {

    @Autowired
    private CommencementPlanService commencementPlanService;

    @Autowired
    private ClassScheduleService classScheduleService;

    @Autowired
    private ClassTimeService classTimeService;

    @Autowired
    private SemesterUtils semesterUtils;

    @Autowired
    private CourseSerialService courseSerialService;

    @GetMapping("/hello")
    public Result hello() {
        return Result.success("hello admin");
    }

    @PostMapping("/uploadCommencementPlan")
    public Result uploadCommencementPlan(@RequestParam("commencementPlan") MultipartFile commencementPlan, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) throws IOException {
        System.out.println("uploadCommencementPlan进来了");
        List<String[]> commencementPlanList = new ArrayList<>();
        if(Objects.requireNonNull(commencementPlan.getOriginalFilename()).endsWith(".xls")) {
            commencementPlanList = ExcelUtils.XLSHandle(commencementPlan);
        } else if(commencementPlan.getOriginalFilename().endsWith(".xlsx")) {
            commencementPlanList = ExcelUtils.XLSXHandle(commencementPlan);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDateLDT = LocalDate.parse(startDate, formatter);
        LocalDate endDateLDT = LocalDate.parse(endDate, formatter);
        boolean result = commencementPlanService.handleCommencementPlan(commencementPlanList, startDateLDT, endDateLDT);
        if(!result) {
            return Result.error("开课计划已存在！");
        }
        return Result.success("上传成功！");
    }

    @PostMapping("/uploadClassSchedule")
    public Result uploadClassSchedule(@RequestParam("classSchedule")MultipartFile classSchedule) throws IOException {
        System.out.println("uploadClassSchedule进来了");
        List<String[]> classScheduleList = new ArrayList<>();
        if (Objects.requireNonNull(classSchedule.getOriginalFilename()).endsWith(".xls")) {
            classScheduleList = ExcelUtils.XLSHandle(classSchedule);
        } else if (classSchedule.getOriginalFilename().endsWith(".xlsx")) {
            classScheduleList = ExcelUtils.XLSXHandle(classSchedule);
        }

        Semester isHaveClassSchedule = classScheduleService.handleClassSchedule(classScheduleList);
        if(isHaveClassSchedule == null) {
            return Result.error("未上传该学期的开课计划！");
        }
        List<Map<String, Object>> resultList = classScheduleService.getAllClassSchedules(isHaveClassSchedule.getStartYear() + "-" + isHaveClassSchedule.getEndYear() + "-" + isHaveClassSchedule.getStage());
        Map<String, Object> semesterMap = new HashMap<>();
        semesterMap.put("semester", isHaveClassSchedule);
        resultList.add(semesterMap);
        return Result.success(resultList);
    }

//    @PostMapping("/insertClassTime/{semester}")
//    public Result insertClassTime(@PathVariable String semester, @RequestBody List<ClassTimeInsertDTO> insertClassTimeList) {
//        Semester tSemester = semesterUtils.getSemesterId(semester);
////        System.out.println(semester);
////        System.out.println(updateClassTimeList.toString());
//        List<ClassTime> classTimeList = new ArrayList<>();
//        for (ClassTimeInsertDTO classTimeInsertDto : insertClassTimeList) {
////            System.out.println(obj.toString());
////            Integer courseNameId = obj.getClassId();
//            CourseSerial courseSerial = new CourseSerial(null, null, (Integer) classTimeInsertDto.getCourseNameId(), classTimeInsertDto.getTeacherId(), classTimeInsertDto.getClassId(), tSemester.getId());
//            courseSerialService.insertACourseSerial(courseSerial);
//            Integer courseSerialId = courseSerial.getId();
//            System.out.println(courseSerialId);
//            //classTimeInsertDto.setCourseSerialId(courseSerialId);
//            classTimeList.add(new ClassTime(null, classTimeInsertDto.getWeekday(), classTimeInsertDto.getSection(), classTimeInsertDto.getStartWeek(), classTimeInsertDto.getEndWeek(), classTimeInsertDto.getNature(), classTimeInsertDto.getSumTime(), classTimeInsertDto.getLabId(), courseSerialId));
//        }
//        classTimeService.addClassTimes(classTimeList);
//        List<ClassTimeInfoVO> classTimeInfoVOList = classTimeService.getClassTimeInfo(classTimeList);
//        return Result.success(classTimeInfoVOList);
//    }

//    @PutMapping("/updateClassTime/{semester}")
//    public Result updateClassTime(@PathVariable String semester, @RequestBody List<Object> updateClassTimeList) {
//        Semester tSemester = semesterUtils.getSemesterId(semester);
////        System.out.println(semester);
////        System.out.println(updateClassTimeList.toString());
//        classTimeService.updateClassTime(updateClassTimeList, tSemester.getId());
//        return Result.success("更新成功！");
//    }
//
//    @DeleteMapping("/deleteClassTime")
//    public Result deleteClassTime(@RequestBody List<Integer> classTimeIds) {
//        // 在这里处理删除逻辑
//        classTimeService.deleteClassTime(classTimeIds);
//        return Result.success("删除成功！");
//    }

}
