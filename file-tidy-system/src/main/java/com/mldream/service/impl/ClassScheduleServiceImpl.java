package com.mldream.service.impl;

import com.mldream.mapper.ClassScheduleMapper;
import com.mldream.pojo.db.*;
import com.mldream.pojo.db.Class;
import com.mldream.service.*;
import com.mldream.utils.SemesterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ClassScheduleServiceImpl implements ClassScheduleService {

    @Autowired
    private CourseNameService courseNameService;

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private LaboratoryService laboratoryService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private ClassTimeService classTimeService;

    @Autowired
    private ClassScheduleMapper classScheduleMapper;

    @Autowired
    private CourseSerialService courseSerialService;

    @Autowired
    private SemesterUtils semesterUtils;

    @Override
    public Semester handleClassSchedule(List<String[]> classScheduleList) {
        /* 判断是否有对应的学期，没有则返回null表示开课计划未上传，不能解析课表 */
        String course_tb_name = classScheduleList.get(0)[0];
        Pattern pattern = Pattern.compile("\\d{4}-\\d{4}-[1-2]");
        Matcher tMatcher = pattern.matcher(course_tb_name);
        Semester semester = new Semester();
        if(tMatcher.find()) {
            String tSemester = tMatcher.group(0);
            semester = semesterUtils.getSemesterId(tSemester);
            if(semester == null) {
                return null;
            }
            List<Map<String, Object>> classScheduleMap = classScheduleMapper.selectAllClassSchedules(tSemester);
            if(!classScheduleMap.isEmpty()) {
                classScheduleMapper.deleteClassSchedules(semester.getId());
            }
        }

        /* 获取所有的课程、班级、老师 */
        List<CourseName> courseList = courseNameService.getAllCourseNames();
        courseList.sort(new Comparator<CourseName>() {
            @Override
            public int compare(CourseName o1, CourseName o2) {
                return o2.getCourseName().length() - o1.getCourseName().length();
            }
        });
//        System.out.println(courseList.toString());
        List<Class> classList = classService.getAllClasses();
        classList.sort(new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
//        System.out.println(classList.toString());
        List<Teacher> teacherList = teacherService.getAllTeachers();
        teacherList.sort(new Comparator<Teacher>() {
            @Override
            public int compare(Teacher o1, Teacher o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
//        System.out.println(teacherList.toString());
        /* 获取所有的实验室 */
        List<Laboratory> laboratoryList = new ArrayList<>();
        for (int i = 3; i < classScheduleList.size() - 1; i++) {
            String classroom = classScheduleList.get(i)[0];
            laboratoryList.add(new Laboratory(null, classroom, null));
        }
        /* 去掉数据库已有的实验室 */
        List<Laboratory> dbLaboratoryList = laboratoryService.getAllLaboratories();
        laboratoryList = laboratoryList.stream().filter(laboratory -> !dbLaboratoryList.stream().map(Laboratory::getName).toList().contains(laboratory.getName())).collect(Collectors.toList());
        if(!laboratoryList.isEmpty()) {
            /* 保存实验室 */
            laboratoryService.addLaboratories(laboratoryList);
        }

        /* 定义正则表达式 */
        StringBuilder regexCourseName = new StringBuilder();
        StringBuilder regexTeacherName = new StringBuilder();
        StringBuilder regexClassName = new StringBuilder();
        String regexSection = "[1-7]0[1-9][0-1][0-9]";
        String regexWeek = "([(（])([1-9]|1[0-9]|2[0-9])-([1-9]|1[0-9]|2[0-9])周([)）])";
        String regexNature = "考试|考查";
        String regexSumTime = "总课时:([1-9]\\d*)";

        String courseName = "";
        for (CourseName c : courseList) {
            String tCourseName = c.getCourseName();
            regexCourseName.append(tCourseName).append("|");
        }
        regexCourseName = new StringBuilder(regexCourseName.substring(0, regexCourseName.length() - 1));

//        String teacherName = "";
        for (Teacher teacher : teacherList) {
            String tTeacherName = teacher.getName();
            regexTeacherName.append(tTeacherName).append("|");
        }
        regexTeacherName = new StringBuilder(regexTeacherName.substring(0, regexTeacherName.length() - 1));

//        String ClassName = "";
        for (Class aClass : classList) {
            String tClassName = aClass.getName();
            tClassName = tClassName.replace("+", "\\+");
            tClassName = tClassName.replace("(", "\\(");
            tClassName = tClassName.replace(")", "\\)");
            regexClassName.append(tClassName).append("|");
        }
        regexClassName = new StringBuilder(regexClassName.substring(0, regexClassName.length() - 1));

//        System.out.println(regexCourseName.toString() + "\n" + regexTeacherName.toString() + "\n" + regexClassName.toString());

        /* 存储时间表 */
        List<ClassTime> classTimeList = new ArrayList<>();
//        List<ClassScheduleCell> classScheduleCellList = new ArrayList<>();
        Pattern courseNamePattern = Pattern.compile(regexCourseName.toString());
        Pattern teacherNamePattern = Pattern.compile(regexTeacherName.toString());
        Pattern classNamePattern = Pattern.compile(regexClassName.toString());
        Pattern sectionPattern = Pattern.compile(regexSection);
        Pattern weekPattern = Pattern.compile(regexWeek);
        Pattern naturePattern = Pattern.compile(regexNature);
        Pattern sumTimePattern = Pattern.compile(regexSumTime);
        /* 解析课表 */
        for (int i = 3; i < classScheduleList.size() - 1; i++) {
            List<String> classroomCourse = new ArrayList<>(Arrays.asList(classScheduleList.get(i)));
            String classroom = classroomCourse.get(0);

            /* 实验室 */
            Laboratory laboratory = laboratoryService.getLaboratoryByName(classroom);
            for (int j = 1; j < classroomCourse.size(); j++) {
                String classInfo = classroomCourse.get(j);
                if(classInfo.trim().isEmpty()) {
                    /* 空格 */
                    continue;
                }
                /* 解析课程名称 JavaEE企业级应用开发刘美玲10304 (1-14周)(03-04节)22软件工程01班考查  总课时:16 */
                classInfo = classInfo.trim();
                /* 正则表达式匹配 */
                Matcher courseNameMatcher = courseNamePattern.matcher(classInfo);
                Matcher teacherNameMatcher = teacherNamePattern.matcher(classInfo);
                Matcher classNameMatcher = classNamePattern.matcher(classInfo);
                Matcher sectionMatcher = sectionPattern.matcher(classInfo);
                Matcher weekMatcher = weekPattern.matcher(classInfo);
                Matcher natureMatcher = naturePattern.matcher(classInfo);
                Matcher sumTimeMatcher = sumTimePattern.matcher(classInfo);

                while(courseNameMatcher.find()) {
                    courseName = courseNameMatcher.group(0);
                    /* 消去课程名称 */
                    classInfo = classInfo.replaceFirst(regexCourseName.toString(), "");

                    /* 课程 */
                    Integer courseNameId = courseNameService.getCourseNameIdByName(courseName);

                    /* 单元格星期节次 */
                    sectionMatcher = sectionPattern.matcher(classInfo);
                    String weekSection = "";
                    int weekday = 0, startIndex = 0;
                    String section = "";

                    /* 单元格老师的id */
                    Set<Integer> teacherIds = new HashSet<>();

                    if(sectionMatcher.find()) {
                        startIndex = sectionMatcher.start();
                        /* 单元格老师, 可能有多个老师，用逗号分隔 xxx,yyy,zzz */
                        String teacherName = classInfo.substring(0, startIndex);
//                        log.info("teacherNameID: {}， startIndex: {}， classInfo: {}", teacherName, startIndex, classInfo);
                        if(teacherName.contains(",") || teacherName.contains("，")) {
                            String[] teacherNames = teacherName.split("[,，]");
                            for (String tName : teacherNames) {
                                Integer tId = teacherService.getTeacherIdByName(tName.trim());
                                if(tId != null) {
                                    teacherIds.add(tId);
                                }
                            }
                        } else {
                            Integer tId = teacherService.getTeacherIdByName(teacherName.trim());
                            if(tId != null) {
                                teacherIds.add(tId);
                            }
                        }

                        classInfo = classInfo.replace(teacherName, "");

                        weekSection = sectionMatcher.group(0);
                        weekday = Integer.parseInt(weekSection.substring(0, 1));
                        section = weekSection.substring(1);
                    }
                    classInfo = classInfo.replaceFirst(regexSection, "");

//                    log.info("classInfo: {}", classInfo);
//                    log.info("regexClassName: {}", regexClassName.toString());
                    /* 单元格班级 */
                    classNameMatcher = classNamePattern.matcher(classInfo);
                    String className = "";
                    if(classNameMatcher.find()) {
                        className = classNameMatcher.group(0);
                        log.info("className: {}", className);
                    }
                    Integer classId = classService.getClassIdByName(className);
                    classInfo = classInfo.replaceFirst(regexClassName.toString(), "");


                    /* 单元格周次 */
                    weekMatcher = weekPattern.matcher(classInfo);
                    String week = "";
                    int startWeek = 0;
                    int endWeek = 0;
                    if(weekMatcher.find()) {
                        week = weekMatcher.group(0);
                        String[] weeks = week.split("-");
                        startWeek = Integer.parseInt(weeks[0].replace("(", "").replace("（", ""));
                        endWeek = Integer.parseInt(weeks[1].replace("周)", "").replace("周）", ""));
                    }
                    classInfo = classInfo.replaceFirst(regexWeek, "");


                    /* 单元格性质 */
                    natureMatcher = naturePattern.matcher(classInfo);
                    String nature = "";
                    if(natureMatcher.find()) {
                        nature = natureMatcher.group(0);
                        classInfo = classInfo.replaceFirst(regexNature, "");
                    }

                    /* 单元格总课时 */
                    sumTimeMatcher = sumTimePattern.matcher(classInfo);
                    String sumTime = "";
                    int courseHour = 0;
                    if(sumTimeMatcher.find()) {
                        sumTime = sumTimeMatcher.group(0);
                        classInfo = classInfo.replaceFirst(regexSumTime, "");
                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(sumTime);
                        if(m.find()) {
                            courseHour = Integer.parseInt(m.group(0));
                        }
                    }

                    /* 存入数据库 */
                    for (Integer teacherId : teacherIds) {
                        classTimeList.add(new ClassTime(null,
                                weekday,
                                section,
                                startWeek,
                                endWeek,
                                nature,
                                courseHour,
                                laboratory.getId(),
                                courseNameId,
                                teacherId,
                                classId,
                                semester.getId())
                        );
                    }
//                    log.info("weekday: {}, section: {}, startWeek: {}, endWeek: {}, nature: {}, courseHour: {}, laboratory: {}, courseName: {}, teacherIds: {}, class: {}, semesterId: {}",
//                            weekday, section, startWeek, endWeek, nature, courseHour, laboratory, courseName, teacherIds, className, semester.getId());
                    /* 继续匹配课程名称 */
                    courseNameMatcher = courseNamePattern.matcher(classInfo);
                }
            }
        }
        /* 保存课表 */
        classTimeService.addClassTimes(classTimeList);

        /* 分配实验室资源 */
        List<LaboratorySource> laboratorySourceList = getLaboratorySources(classTimeList, semester);
        laboratoryService.addLaboratorySources(laboratorySourceList);
        return semester;
    }

    private static List<LaboratorySource> getLaboratorySources(List<ClassTime> classTimeList, Semester semester) {
        List<LaboratorySource> laboratorySourceList = new ArrayList<>();
        for (ClassTime classTime : classTimeList) {
            for (int i = classTime.getStartWeek(); i <= classTime.getEndWeek(); i++) {
                LaboratorySource laboratorySource = new LaboratorySource(null, classTime.getLabId(), i, classTime.getWeekday(), classTime.getSection(), 1, null, classTime.getId(), semester.getId());
                System.out.println(laboratorySource.toString());
                laboratorySourceList.add(laboratorySource);
            }
        }
        return laboratorySourceList;
    }

    @Override
    public List<Map<String, Object>> getAllClassSchedules(String semester) {
        return classScheduleMapper.selectAllClassSchedules(semester);
    }

}
