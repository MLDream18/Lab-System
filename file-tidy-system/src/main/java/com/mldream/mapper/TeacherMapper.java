package com.mldream.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mldream.pojo.db.Teacher;
import com.mldream.pojo.db.TeacherSuggestion;
import com.mldream.pojo.dto.User;
import com.mldream.pojo.vo.TeacherSuggestionVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    @Select("select * from teacher")
    List<Teacher> getAllTeachers();

    void addTeachers(List<Teacher> teacherList);

    @Select("select id from teacher where name = #{name}")
    Integer getTeacherIdByName(String name);

    @Select("select * from teacher where username = #{username} and password = #{password}")
    Teacher selectByUsernameAndPassword(Teacher teacher);

    @Select("select id from teacher where username = #{username}")
    Integer getTeacherByUsername(String username);

    @Insert("insert into teacher(name, username, password) values(#{name}, #{username}, #{password})")
    int insert(Teacher teacher);

    @Update("update teacher set username = #{username}, password = #{password} where name = #{name}")
    void setDefaultAccount(Teacher teacher);

//    @Select("select * from teacher_suggest_view where submit_teacher_id = #{teacherId} and admin_reply is not null order by admin_reply_date desc")
//    List<TeacherSuggestionVO> selectRepliedTeacherSuggestions(Integer teacherId);

    void insertSuggestion(TeacherSuggestion teacherSuggestion);

    @Select("select * from teacher_suggest_view where admin_reply is null order by submit_date desc")
    List<TeacherSuggestionVO> selectNotReplyTeacherSuggestions();

//    void insertUser(User user);
//    @Update("update teacher_suggest set admin_reply = #{replyContent}, admin_reply_date = now() where id = #{suggestionId}")
//    void updateReplyContent(Integer suggestionId, String replyContent);
}
