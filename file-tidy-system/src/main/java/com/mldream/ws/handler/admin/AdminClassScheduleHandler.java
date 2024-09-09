package com.mldream.ws.handler.admin;

import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.SemesterMapper;
import com.mldream.pojo.db.ApplyForm;
import com.mldream.pojo.db.ClassTime;
import com.mldream.pojo.db.LaboratorySource;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.dto.SearchClassScheduleDTO;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.service.ClassTimeService;
import com.mldream.service.LaboratoryService;
import com.mldream.ws.config.WsSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdminClassScheduleHandler extends TextWebSocketHandler {
    public static Map<String, Map<String, Object>> params = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "课程表";
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }

        String sessionId = session.getId();

        params.put(sessionId, new ConcurrentHashMap<>());

        LaboratoryService laboratoryService = applicationContext.getBean(LaboratoryService.class);
        ClassTimeService classTimeService = applicationContext.getBean(ClassTimeService.class);
        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);
        SemesterMapper semesterMapper = applicationContext.getBean(SemesterMapper.class);
        Integer semesterId = semesterMapper.selectLastSemesterId();

        Integer startWeek = semesterMapper.selectWeeksLeft(semesterId);

        params.get(sessionId).put("startWeek", startWeek == null? 1 : startWeek);
        params.get(sessionId).put("endWeek", 20);
        params.get(sessionId).put("semesterId", semesterId == null? 1 : semesterId);
        params.get(sessionId).put("place", "逸夫楼");

        SearchClassScheduleDTO searchClassScheduleDTO = SearchClassScheduleDTO.builder()
                .startWeek((Integer) params.get(sessionId).get("startWeek"))
                .endWeek((Integer) params.get(sessionId).get("endWeek"))
                .semesterId((Integer) params.get(sessionId).get("semesterId"))
                .place((String) params.get(sessionId).get("place"))
                .build();

        Map<String, Object> resultMap = new ConcurrentHashMap<>();
        Map<String, Object> result = new ConcurrentHashMap<>();

        List<LaboratorySource> sourceList = laboratoryService.searchLabClassSchedule(searchClassScheduleDTO);

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
            result.put("classSchedule", classTimeService.getClassTimeInfo(classTimeIds));
        } else {
            result.put("classSchedule", new ArrayList<>());
        }

        if(!applyFormIds.isEmpty()) {
            result.put("applyFormInfo", applyFormService.getApplyFormsDetail(applyFormIds));
        } else {
            result.put("applyFormInfo", new ArrayList<>());
        }
        resultMap.put("result", result);
        resultMap.put("startWeek", params.get(sessionId).get("startWeek"));
        resultMap.put("endWeek", params.get(sessionId).get("endWeek"));
        resultMap.put("place", params.get(sessionId).get("place"));

        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(resultMap))));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();

        if(payload.equals("heartbeatAsk")) {
            // 心跳包
            return;
        }

        String sessionId = session.getId();


        Map<String, Object> newParams = JSON.parseObject(payload);
        newParams.forEach((k, v) -> {
            if (v != null) {
                params.get(sessionId).put(k, v);
            } else {
                params.get(sessionId).remove(k);
            }
        });

        LaboratoryService laboratoryService = applicationContext.getBean(LaboratoryService.class);
        ClassTimeService classTimeService = applicationContext.getBean(ClassTimeService.class);
        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);

        Integer semesterId = (Integer) params.get(sessionId).get("semesterId");

        SearchClassScheduleDTO searchClassScheduleDTO = SearchClassScheduleDTO.builder()
                .startWeek((Integer) params.get(sessionId).get("startWeek"))
                .endWeek((Integer) params.get(sessionId).get("endWeek"))
                .semesterId(semesterId)
                .place((String) params.get(sessionId).get("place"))
                .build();

        Map<String, Object> resultMap = new ConcurrentHashMap<>();
        Map<String, Object> result = new ConcurrentHashMap<>();

        List<LaboratorySource> sourceList = laboratoryService.searchLabClassSchedule(searchClassScheduleDTO);

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
            result.put("classSchedule", classTimeService.getClassTimeInfo(classTimeIds));
        } else {
            result.put("classSchedule", new ArrayList<>());
        }

        if(!applyFormIds.isEmpty()) {
            result.put("applyFormInfo", applyFormService.getApplyFormsDetail(applyFormIds));
        } else {
            result.put("applyFormInfo", new ArrayList<>());
        }
        resultMap.put("result", result);
        resultMap.put("startWeek", params.get(sessionId).get("startWeek"));
        resultMap.put("endWeek", params.get(sessionId).get("endWeek"));
        resultMap.put("place", params.get(sessionId).get("place"));

        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(resultMap))));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("AdminApprovalHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "课程表";
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            params.remove(session.getId());
            WsSessionManager.removeAndClose(JSON.toJSONString(data));
        }
    }


}
