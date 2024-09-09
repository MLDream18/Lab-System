package com.mldream.ws.handler.admin;

import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.ApplyFormMapper;
import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.db.ClassTime;
import com.mldream.pojo.db.LaboratorySource;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.service.ClassTimeService;
import com.mldream.service.LaboratoryService;
import com.mldream.utils.JwtUtils;
import com.mldream.ws.config.WsSessionManager;
import com.mldream.ws.handler.teacher.TeacherApplyHandler;
import com.mldream.ws.handler.teacher.TeacherApplyHistoryApplyHandler;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdminApprovalHandler extends TextWebSocketHandler {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "待审批";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }
        URI uri = session.getUri();
        assert uri != null;
        Claims claims = JwtUtils.parseJwt((String) token);
        Integer role = (Integer) claims.get("role");
//        Integer semesterId = (Integer) claims.get("semesterId");
        List<ApplyFormInfoVO> result = new ArrayList<>();
        int state = -1;
        if(role == 1) {
            state = 0;
        } else {
            state = 1;
        }
        Map<String, Object> resultMap = new HashMap<>();

        ApplyFormMapper applyFormMapper = applicationContext.getBean(ApplyFormMapper.class);

        result = applyFormMapper.selectApplyFormInfoSp();
        resultMap.put("result", result);
        resultMap.put("role", role);
        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(resultMap))));
//        System.out.println(type);
    }

    public LocalDate toLocalDate(String dateString) {
        // 尝试使用 ISO 日期格式解析
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
            return LocalDate.parse(dateString, isoFormatter);
        } catch (DateTimeParseException e) {
            // 如果 ISO 格式解析失败，尝试使用自定义格式解析
        }

        // 使用自定义格式解析日期字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        Instant instant = null;
        try {
            instant = Instant.from(formatter.parse(dateString));
        } catch (DateTimeParseException e) {
            // 如果自定义格式解析失败，返回原始字符串
            return null;
        }

        // 将 Instant 转换为 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

        // 提取 LocalDate
        return zonedDateTime.toLocalDate();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();

        if(payload.equals("heartbeatAsk")) {
            // 心跳包
            return;
        }

        Map<String, Object> data = JSON.parseObject(payload);
        ApplyFormDTO applyFormDTO = ApplyFormDTO.builder()
                .applyFormId((Integer) data.get("applyFormId"))
                .unitOpinion(data.containsKey("unitOpinion") ? (String) data.get("unitOpinion") : null)
                .unitOpinionDate(data.containsKey("unitOpinionDate") ?  toLocalDate((String) data.get("unitOpinionDate")) : null)
                .unitOpinionName(data.containsKey("unitOpinionName") ? (String) data.get("unitOpinionName") : null)
                .approvalOpinion(data.containsKey("approvalOpinion") ? (String) data.get("approvalOpinion") : null)
                .approvalOpinionDate(data.containsKey("approvalOpinionDate") ? toLocalDate((String) data.get("approvalOpinionDate")) : null)
                .approvalOpinionName(data.containsKey("approvalOpinionName") ? (String) data.get("approvalOpinionName") : null)
                .state((Integer) data.get("state"))
                .build();
        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);
        applyFormService.submitApproval(applyFormDTO);

        Integer state = (Integer) data.get("state");

        ApplyFormMapper applyFormMapper = applicationContext.getBean(ApplyFormMapper.class);
        List<ApplyFormInfoVO> result = new ArrayList<>();

        for (String key : WsSessionManager.SESSION_POOL.keySet()) {
            WebSocketSession wsSession = WsSessionManager.SESSION_POOL.get(key);
            Map<String, Object> keyData = JSON.parseObject(key);
            String page = (String) keyData.get("page");
            if (wsSession.isOpen()) {
                Claims claims = (Claims) wsSession.getAttributes().get("claims");
                String sessionId = wsSession.getId();
                switch (page) {
                    case "待审批":
                        Integer role = (Integer) claims.get("role");
                        result = applyFormMapper.selectApplyFormInfoSp();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("result", result);
                        resultMap.put("role", role);
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(resultMap))));
                        break;
                    case "审批记录":
                        PageBean pageBean1 = applicationContext.getBean(ApplyFormService.class).submitApprovalSelectForm(AdminApprovalRecordHandler.pageSizes.get(sessionId), AdminApprovalRecordHandler.currentPages.get(sessionId), AdminApprovalRecordHandler.params.get(sessionId));
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean1))));
                        break;
                    case "申请中":
                        Integer teacherId1 = (Integer) claims.get("id");
                        List<ApplyFormInfoVO> applyForms1 = applyFormService.getApplyFormsBySubmitTeacherId(teacherId1);
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(applyForms1))));
                        break;
                    case "历史申请":
                        Integer teacherId2 = (Integer) claims.get("id");
                        PageBean pageBean2 = applyFormService.getApplyFormsAppliedBySubmitTeacherId(TeacherApplyHistoryApplyHandler.pageSizes.get(sessionId), TeacherApplyHistoryApplyHandler.currentPages.get(sessionId), teacherId2);
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean2))));
                        break;
                    case "课程表":
                        if(state == 2) {
                            LaboratoryService laboratoryService = applicationContext.getBean(LaboratoryService.class);

                            Map<String, Object> params = AdminClassScheduleHandler.params.get(sessionId);

                            ClassTimeService classTimeService = applicationContext.getBean(ClassTimeService.class);

                            ApplySearchConditionsDTO applySearchConditionsDTO = ApplySearchConditionsDTO.builder()
                                    .startWeek((Integer) params.get("startWeek"))
                                    .endWeek((Integer) params.get("endWeek"))
                                    .semesterId((Integer) params.get("semesterId"))
                                    .build();

                            Map<String, Object> resultMapTmp = new ConcurrentHashMap<>();
                            Map<String, Object> resultTmp = new HashMap<>();

                            List<LaboratorySource> sourceList = laboratoryService.searchLab(applySearchConditionsDTO);

                            List<ClassTime> classTimeIds = new ArrayList<>();
                            List<ApplyForm> applyFormIds = new ArrayList<>();
                            for (LaboratorySource source : sourceList) {
                                if(source.getClassTimeId() != null) {
                                    classTimeIds.add(ClassTime.builder().id(source.getClassTimeId()).build());
                                } else {
                                    applyFormIds.add(ApplyForm.builder().id(source.getApplyFormId()).build());
                                }
                            }

                            if(!classTimeIds.isEmpty()) {
                                resultTmp.put("classSchedule", classTimeService.getClassTimeInfo(classTimeIds));
                            } else {
                                resultTmp.put("classSchedule", new ArrayList<>());
                            }

                            if(!applyFormIds.isEmpty()) {
                                resultTmp.put("applyFormInfo", applyFormService.getApplyFormsDetail(applyFormIds));
                            } else {
                                resultTmp.put("applyFormInfo", new ArrayList<>());
                            }
                            resultMapTmp.put("result", resultTmp);
                            resultMapTmp.put("startWeek", params.get("startWeek"));
                            resultMapTmp.put("endWeek", params.get("endWeek"));

                            wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(resultMapTmp))));
                        }
                        break;
                    case "申请":
                        LaboratoryService laboratoryService = applicationContext.getBean(LaboratoryService.class);
                        var params = TeacherApplyHandler.params;
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(laboratoryService.searchLab(ApplySearchConditionsDTO.builder()
                                .labId((Integer) params.get(sessionId).get("labId"))
                                .startWeek((Integer) params.get(sessionId).get("startWeek"))
                                .endWeek((Integer) params.get(sessionId).get("endWeek"))
                                .startWeekday((Integer) params.get(sessionId).get("startWeekday"))
                                .endWeekday((Integer) params.get(sessionId).get("endWeekday"))
                                .startSection((String) params.get(sessionId).get("startSection"))
                                .endSection((String) params.get(sessionId).get("endSection"))
                                .state((Integer) params.get(sessionId).get("state"))
                                .semesterId((Integer) params.get(sessionId).get("semesterId"))
                                .build())))));
                        break;
                }
            }
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("AdminApprovalHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "待审批";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            WsSessionManager.removeAndClose(JSON.toJSONString(data));
        }
    }
}
