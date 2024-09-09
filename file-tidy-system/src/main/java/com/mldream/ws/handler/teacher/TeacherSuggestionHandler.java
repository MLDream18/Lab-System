package com.mldream.ws.handler.teacher;


import com.alibaba.fastjson2.JSON;
import com.mldream.pojo.db.TeacherSuggestion;
import com.mldream.pojo.vo.PageBean;
import com.mldream.service.TeacherService;
import com.mldream.utils.JwtUtils;
import com.mldream.ws.config.WsSessionManager;
import com.mldream.ws.handler.admin.AdminSuggestionHandler;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
public class TeacherSuggestionHandler extends AbstractWebSocketHandler {

//    public static final Map<String, Integer> pageSizes = new ConcurrentHashMap<>();
//    public static final Map<String, Integer> currentPages = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "实验室环境建议";
        Map<String, Object> data = new CaseInsensitiveKeyMap<>();
        data.put("page", page);
        data.put("token", token);
        if(token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }
        /* 上传文件大小默认限制：8kb */
//        System.out.println(session.getBinaryMessageSizeLimit());

        // 分页查询管理员回复的建议
//        String sessionId = session.getId();
//        pageSizes.put(sessionId, 10);
//        currentPages.put(sessionId, 1);
//
//        Claims claims = JwtUtils.parseJwt((String) token);
//        Integer teacherId = (Integer) claims.get("id");
//
//        TeacherService teacherService = applicationContext.getBean(TeacherService.class);
//        PageBean pageBean = teacherService.getRepliedTeacherSuggestions(pageSizes.get(sessionId), currentPages.get(sessionId), teacherId);
//        session.sendMessage(new TextMessage(JSON.toJSONString(pageBean)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 接收到客户端消息，处理业务逻辑
        String payload = message.getPayload();

        // 心跳
        if (payload.equals("heartbeatAsk")) {
            // 心跳包
            return;
        }

        TeacherService teacherService = applicationContext.getBean(TeacherService.class);

        String token = (String) session.getAttributes().get("token");

        Claims claims = (Claims) session.getAttributes().get("claims");
        Integer teacherId = (Integer) claims.get("id");

//        String sessionId = session.getId();
        Map<String, Object> data = JSON.parseObject(payload);

//        if (data.containsKey("pagination")) {
//            // 分页
//            Integer pageSize = (Integer) data.get("pageSize");
//            Integer currentPage = (Integer) data.get("currentPage");
//            pageSizes.put(sessionId, pageSize);
//            currentPages.put(sessionId, currentPage);
//
//            PageBean pageBean = teacherService.getRepliedTeacherSuggestions(pageSize, currentPage, teacherId);
//            session.sendMessage(new TextMessage(JSON.toJSONString(pageBean)));
//            return;
//        }

        TeacherSuggestion teacherSuggestion = TeacherSuggestion.builder()
                .courseNameId((Integer) data.get("courseNameId"))
                .teacherId((Integer) data.get("teacherId"))
                .labId((Integer) data.get("labId"))
                .environmentCommand((String) data.get("environmentCommand"))
                .semesterId((Integer) data.get("semesterId")).build();
        if (data.containsKey("appUrl")) {
            // 文件上传
            teacherSuggestion.setAppUrl((String) data.get("appUrl"));
        }
        teacherSuggestion.setSubmitDate(LocalDate.now());
        teacherService.addSuggestion(teacherSuggestion);
        // 通知管理员
        for (String key : WsSessionManager.SESSION_POOL.keySet()) {
            WebSocketSession wsSession = WsSessionManager.SESSION_POOL.get(key);
            Map<String, Object> keyData = JSON.parseObject(key);
            String page = (String) keyData.get("page");
            if (wsSession.isOpen()) {
                if (page.equals("教师建议")) {
                    PageBean suggestions = teacherService.getNotReplyTeacherSuggestions(AdminSuggestionHandler.pageSizes.get(wsSession.getId()), AdminSuggestionHandler.currentPages.get(wsSession.getId()));
                    wsSession.sendMessage(new TextMessage(JSON.toJSONString(suggestions)));
                }
            }
        }

        session.sendMessage(new TextMessage("提交成功"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("TeacherSuggestionHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "实验室环境建议";
        Map<String, Object> data = new CaseInsensitiveKeyMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
//            String sessionId = session.getId();
//            pageSizes.remove(sessionId);
//            currentPages.remove(sessionId);
            // 用户退出，移除缓存
            WsSessionManager.removeAndClose(JSON.toJSONString(data));
        }
    }
}
