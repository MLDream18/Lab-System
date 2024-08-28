package com.mldream.mapper;

import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ApplyFormMapper {

    List<ApplyFormInfoVO> selectApplyFormsDetail(List<ApplyForm> applyFormIds);

    void insertApplyForm(ApplyForm applyForm);

    @Select("select * from apply_form_view where submit_teacher_id = #{submitTeacherId}")
    List<ApplyFormInfoVO> getApplyFormsBySubmitTeacherId(Integer submitTeacherId);

    @Select("select * from apply_form_view")
    List<ApplyFormInfoVO> selectApplyFormInfoSp();

    @Update("update apply_form set state = #{state}, unit_opinion = #{unitOpinion}, " +
            "unit_opinion_date = #{unitOpinionDate}, unit_opinion_name = #{unitOpinionName}, " +
            "approval_opinion = #{approvalOpinion}, approval_opinion_date = #{approvalOpinionDate}, " +
            "approval_opinion_name = #{approvalOpinionName} where id = #{applyFormId}")
    void updateApplyFormState(ApplyFormDTO applyFormDTO);

    @Update("update apply_form set state = -3 where used_start_date < date(NOW()) and (state between 0 and 1)")
    void checkApplyForm();

    List<ApplyFormInfoVO> selectApplyFormInfoByCondition(Map<String, Object> condition);

    @Select("select * from apply_form_view where semester_id = #{semesterId} and state = 2 order by used_start_date")
    List<ApplyFormInfoVO> selectLabRegisterData(Integer semesterId);

    @Select("select * from apply_form_view where submit_teacher_id = #{teacherId} and state != 0 and state != 1  order by submit_date")
    List<ApplyFormInfoVO> selectApplyFormsAppliedBySubmitTeacherId(Integer teacherId);
}
