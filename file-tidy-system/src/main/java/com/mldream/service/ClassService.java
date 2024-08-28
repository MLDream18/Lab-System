package com.mldream.service;

import com.mldream.pojo.db.Class;

import java.util.List;
import java.util.Set;

public interface ClassService {

    List<Class> getAllClasses();

    void addClasses(List<Class> classList);

    Integer getClassIdByName(String name);

}
