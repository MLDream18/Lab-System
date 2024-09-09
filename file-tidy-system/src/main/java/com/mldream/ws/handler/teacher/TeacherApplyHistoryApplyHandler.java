package com.mldream.ws.handler.teacher;


import com.alibaba.fastjson2.JSON;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.utils.JwtUtils;
import com.mldream.ws.config.WsSessionManager;
import io.jsonwebtoken.Claims;
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
public class TeacherApplyHistoryApplyHandler extends TextWebSocketHandler {
    public static Map<String, Integer> pageSizes = new ConcurrentHashMap<>();
    public static Map<String, Integer> currentPages = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "历史申请";
        Map<String, Object> data = new CaseInsensitiveKeyMap<>();
        data.put("page", page);
        data.put("token", token);
        if(token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }

        String sessionId = session.getId();

        pageSizes.put(sessionId, 10);
        currentPages.put(sessionId, 1);

        Claims claims = (Claims) session.getAttributes().get("claims");
        Integer teacherId = (Integer) claims.get("id");
        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);
        PageBean pageBean = applyFormService.getApplyFormsAppliedBySubmitTeacherId(pageSizes.get(sessionId), currentPages.get(sessionId), teacherId);
        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean))));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 接收到客户端消息，处理业务逻辑
        String payload = message.getPayload();

        if(payload.equals("heartbeatAsk")) {
            // 心跳包
            return;
        }

        Map<String, Object> data = JSON.parseObject(payload);

        Object token = session.getAttributes().get("token");

        String sessionId = session.getId();

        pageSizes.put(sessionId, (Integer) data.get("pageSize"));
        currentPages.put(sessionId, (Integer) data.get("currentPage"));

        Claims claims = (Claims) session.getAttributes().get("claims");
        Integer teacherId = (Integer) claims.get("id");

        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);
        PageBean pageBean = applyFormService.getApplyFormsAppliedBySubmitTeacherId(pageSizes.get(sessionId), currentPages.get(sessionId), teacherId);
        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean))));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("TeacherApplyHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "历史申请";
        Map<String, Object> data = new CaseInsensitiveKeyMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            String sessionId = session.getId();
            pageSizes.remove(sessionId);
            currentPages.remove(sessionId);
            WsSessionManager.removeAndClose(JSON.toJSONString(data));
        }
    }
}
