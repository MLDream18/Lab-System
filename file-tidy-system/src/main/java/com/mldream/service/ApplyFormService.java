package com.mldream.service;

import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

public interface ApplyFormService {


    List<ApplyFormInfoVO> getApplyFormsDetail(List<ApplyForm> applyFormIds);

    Result submitApplyForm(ApplyFormDTO applyFormDTO);

    List<ApplyFormInfoVO> getApplyFormsBySubmitTeacherId(Integer submitTeacherId);

    PageBean getApplyFormInfoSp(Integer pageSize, Integer currentPage);

    void submitApproval(ApplyFormDTO applyFormDTO);

    PageBean submitApprovalSelectForm(Integer pageSize, Integer currentPage, Map<String, Object> params);

    List<ApplyFormInfoVO> getLabRegisterData(Integer semesterId);

    PageBean getApplyFormsAppliedBySubmitTeacherId(Integer pageSize, Integer currentPage, Integer teacherId);
}
