package com.mldream.service;

import com.mldream.pojo.db.Semester;

import java.util.List;
import java.util.Map;

public interface ClassScheduleService {

    Semester handleClassSchedule(List<String[]> classScheduleList);

    List<Map<String, Object>> getAllClassSchedules(String semester);

}
