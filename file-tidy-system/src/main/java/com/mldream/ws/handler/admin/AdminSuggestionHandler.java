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
import com.mldream.pojo.vo.TeacherSuggestionVO;
import com.mldream.service.ApplyFormService;
import com.mldream.service.ClassTimeService;
import com.mldream.service.LaboratoryService;
import com.mldream.service.TeacherService;
import com.mldream.utils.JwtUtils;
import com.mldream.ws.config.WsSessionManager;
import com.mldream.ws.handler.teacher.TeacherApplyHistoryApplyHandler;
import com.mldream.ws.handler.teacher.TeacherSuggestionHandler;
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
public class AdminSuggestionHandler extends TextWebSocketHandler {

    public static Map<String, Integer> pageSizes = new ConcurrentHashMap<>(); // 每页显示的记录数
    public static Map<String, Integer> currentPages = new ConcurrentHashMap<>(); // 当前页码

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "教师建议";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }
        TeacherService teacherService = applicationContext.getBean(TeacherService.class);

        String sessionId = session.getId();

        pageSizes.put(sessionId, 10);
        currentPages.put(sessionId, 1);

        PageBean suggestions = teacherService.getNotReplyTeacherSuggestions(pageSizes.get(sessionId), currentPages.get(sessionId));

        session.sendMessage(new TextMessage(JSON.toJSONString(suggestions)));

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();

        if(payload.equals("heartbeatAsk")) {
            // 心跳包
            return;
        }
        Map<String, Object> payloadMap = JSON.parseObject(payload);

        String sessionId =  session.getId();
        pageSizes.put(sessionId, (Integer) payloadMap.get("pageSize"));
        currentPages.put(sessionId, (Integer) payloadMap.get("currentPage"));

        TeacherService teacherService = applicationContext.getBean(TeacherService.class);
//        if(payloadMap.containsKey("Reply")) {
//            teacherService.replyTeacherSuggestion((Integer) payloadMap.get("suggestionId"), (String) payloadMap.get("replyContent"));
//            for (String key : WsSessionManager.SESSION_POOL.keySet()) {
//                WebSocketSession wsSession = WsSessionManager.SESSION_POOL.get(key);
//                Map<String, Object> keyData = JSON.parseObject(key);
//                String page = (String) keyData.get("page");
//                if (wsSession.isOpen()) {
//                    String wsSessionId = wsSession.getId();
//                    String token = (String) keyData.get("token");
//                    if (page.equals("实验室环境建议")) {
//                        Claims claims = JwtUtils.parseJwt(token);
//                        Integer teacherId = (Integer) claims.get("id");
//
//                        PageBean pageBean = teacherService.getRepliedTeacherSuggestions(TeacherSuggestionHandler.pageSizes.get(wsSessionId), TeacherSuggestionHandler.currentPages.get(wsSessionId), teacherId);
//                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(pageBean)));
//                    }
//                }
//            }
//        }
        PageBean suggestions = teacherService.getNotReplyTeacherSuggestions(pageSizes.get(sessionId), currentPages.get(sessionId));

        session.sendMessage(new TextMessage(JSON.toJSONString(suggestions)));

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("AdminSuggestionHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "教师建议";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            pageSizes.remove(session.getId());
            currentPages.remove(session.getId());
            WsSessionManager.removeAndClose(JSON.toJSONString(data));
        }
    }

}
