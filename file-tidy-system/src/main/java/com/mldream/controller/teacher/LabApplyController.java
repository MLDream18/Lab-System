package com.mldream.controller.teacher;

import com.mldream.pojo.db.*;
import com.mldream.pojo.db.Class;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.ClassTimeInfoVO;
import com.mldream.pojo.vo.Result;
import com.mldream.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/teacher")
public class LabApplyController {

    @Autowired
    private LaboratoryService laboratoryService;

    @Autowired
    private ClassTimeService classTimeService;

    @Autowired
    private ApplyFormService applyFormService;

    @Autowired
    private CourseNameService courseNameService;

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/lab/search-lab")
    public Result searchLab(@RequestBody ApplySearchConditionsDTO applySearchConditionsDTO) {
        List<LaboratorySource> labSources = laboratoryService.searchLab(applySearchConditionsDTO);
        return Result.success(labSources);
    }

    @PostMapping("/lab/search-class-time")
    public Result searchClassTime(@RequestBody List<ClassTime> classTimeIds) {
        if(classTimeIds == null || classTimeIds.isEmpty()) {
            return Result.success();
        }
        List<ClassTimeInfoVO> classTimeInfoVOResult = classTimeService.getClassTimeInfo(classTimeIds);
        return Result.success(classTimeInfoVOResult);
    }

    @PostMapping("/lab/search-apply-form")
    public Result searchApplyForm(@RequestBody List<ApplyForm> applyFormIds) {
        System.out.println(applyFormIds.toString());
        if(applyFormIds.isEmpty()) {
            return Result.success();
        }
        List<ApplyFormInfoVO> applyFormsResult = applyFormService.getApplyFormsDetail(applyFormIds);
        return Result.success(applyFormsResult);
    }

    @GetMapping("/lab/apply/getAllCourseName")
    public Result getAllCourseName() {
        List<CourseName> courseNames = courseNameService.getAllCourseNames();
        return Result.success(courseNames);
    }

    @GetMapping("/lab/apply/getClassList")
    public Result getClassList() {
        List<Class> classes = classService.getAllClasses();
        return Result.success(classes);
    }

    @GetMapping("/lab/apply/getTeacherList")
    public Result getTeacherList() {
        List<Teacher> teacherList = teacherService.getAllTeachers();
        return Result.success(teacherList);
    }

    @GetMapping("/lab/apply/getLabList")
    public Result getLabList() {
        List<Laboratory> labList = laboratoryService.getAllLaboratories();
        return Result.success(labList);
    }

    @PostMapping("/lab/apply/submitApplyForm")
    public Result submitApplyForm(@RequestBody ApplyFormDTO applyFormDTO) {
//        System.out.println(applyFormDTO.toString());
        if(applyFormDTO.getApplicantCollege().equals("人工智能学院")) {
            applyFormDTO.setState(1);
        }
//        System.out.println(applyFormDTO.toString());
        return applyFormService.submitApplyForm(applyFormDTO);
    }

    @GetMapping("/lab/apply/classrooms/{place}")
    public Result getClassroomsByPlace(@PathVariable String place) {
        List<Laboratory> labList = laboratoryService.getClassroomsByPlace(place);
        return Result.success(labList);
    }

//    @GetMapping("/apply/get-apply-info-current-account/{submitTeacherId}/{semesterId}")
//    public Result getApplyInfoCurrentAccount(@PathVariable Integer submitTeacherId, @PathVariable Integer semesterId) {
//        List<ApplyFormInfoVO> applyForms = applyFormService.getApplyFormsBySubmitTeacherId(submitTeacherId);
//        return Result.success(applyForms);
//    }
}
