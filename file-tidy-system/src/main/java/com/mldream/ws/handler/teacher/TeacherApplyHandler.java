package com.mldream.ws.handler.teacher;


import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.SemesterMapper;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.vo.Result;
import com.mldream.service.LaboratoryService;
import com.mldream.ws.config.WsSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TeacherApplyHandler extends TextWebSocketHandler {
    public static Map<String, Map<String, Object>> params = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "申请";
        Map<String, Object> data = new CaseInsensitiveKeyMap<>();
        data.put("page", page);
        data.put("token", token);
        if(token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }

        String sessionId = session.getId();

        params.put(sessionId, new ConcurrentHashMap<>());

        SemesterMapper semesterMapper = applicationContext.getBean(SemesterMapper.class);
        LaboratoryService laboratoryService = applicationContext.getBean(LaboratoryService.class);

        params.get(sessionId).put("startWeek", 1);
        params.get(sessionId).put("endWeek", 20);
        params.get(sessionId).put("startWeekday", 1);
        params.get(sessionId).put("endWeekday", 7);
        params.get(sessionId).put("startSection", "0102");
        params.get(sessionId).put("endSection", "0910");
        params.get(sessionId).put("semesterId", semesterMapper.selectLastSemesterId());

        ApplySearchConditionsDTO applySearchConditionsDTO = ApplySearchConditionsDTO.builder()
                .startWeek(1)
                .endWeek(20)
                .startWeekday(1)
                .endWeekday(7)
                .startSection("0102")
                .endSection("0910")
                .semesterId(semesterMapper.selectLastSemesterId())
                .build();

        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(laboratoryService.searchLab(applySearchConditionsDTO)))));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 接收到客户端消息，处理业务逻辑
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

        ApplySearchConditionsDTO applySearchConditionsDTO = ApplySearchConditionsDTO.builder()
                .labId((Integer) params.get(sessionId).get("labId"))
                .startWeek((Integer) params.get(sessionId).get("startWeek"))
                .endWeek((Integer) params.get(sessionId).get("endWeek"))
                .startWeekday((Integer) params.get(sessionId).get("startWeekday"))
                .endWeekday((Integer) params.get(sessionId).get("endWeekday"))
                .startSection((String) params.get(sessionId).get("startSection"))
                .endSection((String) params.get(sessionId).get("endSection"))
                .state((Integer) params.get(sessionId).get("state"))
                .semesterId((Integer) params.get(sessionId).get("semesterId"))
                .build();
        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(laboratoryService.searchLab(applySearchConditionsDTO)))));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("TeacherApplyHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "申请";
        Map<String, Object> data = new CaseInsensitiveKeyMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            String sessionId = session.getId();
            params.remove(sessionId);
            WsSessionManager.remove(JSON.toJSONString(data));
        }
    }
}
