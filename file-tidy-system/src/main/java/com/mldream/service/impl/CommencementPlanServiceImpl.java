package com.mldream.service.impl;

import com.mldream.pojo.db.*;
import com.mldream.pojo.db.Class;
import com.mldream.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CommencementPlanServiceImpl implements CommencementPlanService {

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private CourseNameService courseNameService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassService classService;

    @Autowired
    private CourseSerialService courseSerialService;

    @Override
    public boolean handleCommencementPlan(List<String[]> commencementPlanList, LocalDate startDate, LocalDate endDate) {
        Semester semester = new Semester();
        String s = commencementPlanList.get(0)[0];
        Pattern pattern = Pattern.compile("\\d{4}-\\d{4}-[1-2]");
        Matcher tMatcher = pattern.matcher(s);
        String startYear_endYear_stage = "";
        if(tMatcher.find()) {
            startYear_endYear_stage = tMatcher.group(0);
        }
        String[] startYear_endYear_stage_arr = startYear_endYear_stage.split("-");
        semester.setStartYear(Integer.parseInt(startYear_endYear_stage_arr[0]));
        semester.setEndYear(Integer.parseInt(startYear_endYear_stage_arr[1]));
        semester.setStage(Integer.parseInt(startYear_endYear_stage_arr[2]));

//        System.out.println(semesterService.getSemester(semester).toString());

        /* 判断日期开课计划是否存在 */
        if (semesterService.getSemester(semester) == null) {
            semester.setStartDate(startDate);
            semester.setEndDate(endDate);
            semesterService.addSemester(semester);
            String[] fields = commencementPlanList.get(1); // 字段名
            int serialIndex = -1; // 课程编号
            int courseNameIndex =  -1; // 课程名称
            int teacherIndex =  -1; // 授课教师
            int classIndex = -1; // 班级名称
            int classPeopleIndex = -1; // 选课人数
            for (int i = 0; i < fields.length; i++) {
                switch (fields[i]) {
                    case "课程编号" -> serialIndex = i;
                    case "课程名称" -> courseNameIndex = i;
                    case "授课教师" -> teacherIndex = i;
                    case "班级名称" -> classIndex = i;
                    case "选课人数" -> classPeopleIndex = i;
                }
            }

            Set<CourseName> courseNameSet = new HashSet<>();
            Set<String> coursenameSet = new HashSet<>();
            for (int i = 2; i < commencementPlanList.size(); i++) {
                String[] courseInfo = commencementPlanList.get(i);
                if(courseInfo[courseNameIndex].isEmpty() || coursenameSet.contains(courseInfo[courseNameIndex])) {
                    continue;
                }
                coursenameSet.add(courseInfo[courseNameIndex]);
                courseNameSet.add(new CourseName(null, courseInfo[courseNameIndex], "0809"));
            }

            Set<Teacher> teacherSet = new HashSet<>();
            Set<String> teacherNameSet = new HashSet<>();
            for (int i = 2; i < commencementPlanList.size(); i++) {
                String[] courseInfo = commencementPlanList.get(i);
                if(courseInfo[teacherIndex].isEmpty() || teacherNameSet.contains(courseInfo[teacherIndex])) {
                    continue;
                }
                String teacherName = "";
                int index = courseInfo[teacherIndex].indexOf('（');
                if(index == -1) index = courseInfo[teacherIndex].indexOf('(');
                if(index != -1) {
                    courseInfo[teacherIndex] = courseInfo[teacherIndex].substring(0, index);
                    index = courseInfo[teacherIndex].indexOf('、');
                    teacherName = courseInfo[teacherIndex];
                    if(index != -1) {
                        String[] teacherNames = teacherName.split("、");
                        for (String tN : teacherNames) {
                            if(teacherNameSet.contains(tN)) {
                                continue;
                            }
                            teacherNameSet.add(tN);
                            teacherSet.add(new Teacher(tN));
                        }
                    } else {
                        teacherNameSet.add(teacherName);
                        teacherSet.add(new Teacher(teacherName));
                    }
                }
                else {
                    index = courseInfo[teacherIndex].indexOf('、');
                    teacherName = courseInfo[teacherIndex];
                    if(index != -1) {
                        String[] teacherNames = teacherName.split("、");
                        for (String tN : teacherNames) {
                            if(teacherNameSet.contains(tN)) {
                                continue;
                            }
                            teacherNameSet.add(tN);
                            teacherSet.add(new Teacher(tN));
                        }
                    } else {
                        teacherNameSet.add(teacherName);
                        teacherSet.add(new Teacher(teacherName));
                    }
                }
            }

            Set<Class> classSet = new HashSet<>();
            Set<String> classNameSet = new HashSet<>();
            for (int i = 2; i < commencementPlanList.size(); i++) {
                String[] courseInfo = commencementPlanList.get(i);
                if(courseInfo[classIndex].isEmpty() || classNameSet.contains(courseInfo[classIndex])) {
                    continue;
                }
                String className = courseInfo[classIndex];
                int classPeople = 0;
                if(!courseInfo[classPeopleIndex].isEmpty()) {
                    classPeople = Integer.parseInt(courseInfo[classPeopleIndex].replace(".0", ""));
                }
                classNameSet.add(className);
                classSet.add(new Class(null, className, classPeople));
            }

            /* 将课程、班级、老师上传数据库 */
            List<CourseName> courseListInDb = courseNameService.getAllCourseNames();
            List<CourseName> courseNameList = new ArrayList<>(courseNameSet);
            /* 过滤掉数据库中名字已存在的课程名 */
            courseNameList = courseNameList.stream().filter(courseName -> !courseListInDb.stream().map(CourseName::getCourseName).toList().contains(courseName.getCourseName())).toList();
            if(!courseNameList.isEmpty()) {
                courseNameService.addCourseNames(courseNameList);
            }

            List<Teacher> teacherListInDb = teacherService.getAllTeachers();
            List<Teacher> teacherList = new ArrayList<>(teacherSet);
            /* 过滤掉数据库中已存在的老师 */
            teacherList = teacherList.stream().filter(teacher -> !teacherListInDb.stream().map(Teacher::getName).toList().contains(teacher.getName())).toList();
            if(!teacherList.isEmpty()) {
                teacherService.addTeachers(teacherList);
            }

            List<Class> classListInDb = classService.getAllClasses();
            List<Class> classList = new ArrayList<>(classSet);
            /* 过滤掉数据库中已存在的班级 */
            classList = classList.stream().filter(class1 -> !classListInDb.stream().map(Class::getName).toList().contains(class1.getName())).collect(Collectors.toList());
            if(!classList.isEmpty()) {
                classService.addClasses(classList);
            }

            /* 存储课程编号 */
            List<CourseSerial> courseSerialList = new ArrayList<>();
            for (int i = 2; i < commencementPlanList.size(); i++) {
                String[] courseInfo = commencementPlanList.get(i);
                Integer courseNameId = null;
                if(!courseInfo[courseNameIndex].isEmpty()) {
                    courseNameId = courseNameService.getCourseNameIdByName(courseInfo[courseNameIndex]);
                }
                CourseSerial courseSerial = new CourseSerial(null, courseInfo[serialIndex], courseNameId, semester.getId());
                courseSerialList.add(courseSerial);
            }
            courseSerialService.addCourseSerial(courseSerialList);
            return true;
        }
        /* 开课计划已存在 */
        return false;
    }
}
