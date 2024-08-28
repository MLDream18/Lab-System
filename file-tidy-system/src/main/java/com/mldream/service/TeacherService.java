package com.mldream.service;

import com.mldream.pojo.db.Teacher;
import com.mldream.pojo.db.TeacherSuggestion;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.pojo.vo.TeacherSuggestionVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherService {

    List<Teacher> getAllTeachers();

    void addTeachers(List<Teacher> teacherList);

    Integer getTeacherIdByName(String name);

    Teacher login(Teacher teacher);

    Result register(Teacher teacher);

//    PageBean getRepliedTeacherSuggestions(Integer pageSize, Integer currentPage, Integer teacherId);

    void addSuggestion(TeacherSuggestion teacherSuggestion);

    PageBean getNotReplyTeacherSuggestions(Integer pageSize, Integer currentPage);

//    void replyTeacherSuggestion(Integer suggestionId, String replyContent);

    Result uploadFile(MultipartFile file, Integer zoneNowIndex, Integer zoneTotalCount, Long zoneTotalSize, String fileUUID) throws Exception;
}
