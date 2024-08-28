package com.mldream.ws.handler.admin;

import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.SemesterMapper;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.ws.config.WsSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class AdminApprovalRecordHandler extends TextWebSocketHandler {
    public static Map<String, Integer> pageSizes = new ConcurrentHashMap<>(); // 每页显示的记录数
    public static Map<String, Integer> currentPages = new ConcurrentHashMap<>(); // 当前页码
    public static Map<String, Map<String, Object>> params = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "审批记录";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }

        String sessionId = session.getId();

        pageSizes.put(sessionId, 10);
        currentPages.put(sessionId, 1);
        params.put(sessionId, new ConcurrentHashMap<>());

        SemesterMapper semesterMapper = applicationContext.getBean(SemesterMapper.class);
        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);

        params.get(sessionId).put("semesterId", semesterMapper.selectLastSemesterId());

        PageBean pageBean = applyFormService.submitApprovalSelectForm(pageSizes.get(sessionId), currentPages.get(sessionId), params.get(sessionId));

        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean))));
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

        Integer newPageSize = (Integer) data.get("pageSize");
        Integer newCurrentPage = (Integer) data.get("currentPage");
        Map<String, Object> newParams = (Map<String, Object>) data.get("form");

        String sessionId = session.getId();

        pageSizes.put(sessionId, newPageSize);
        currentPages.put(sessionId, newCurrentPage);
        newParams.forEach((k, v) -> {
            if (v != null) {
                params.get(sessionId).put(k, v);
            } else {
                params.get(sessionId).remove(k);
            }
        });

        Integer pageSize = pageSizes.get(sessionId);
        Integer currentPage = currentPages.get(sessionId);

        PageBean pageBean = applicationContext.getBean(ApplyFormService.class).submitApprovalSelectForm(pageSize, currentPage, params.get(sessionId));

        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean))));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("AdminApprovalRecordHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "审批记录";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            String sessionId = session.getId();
            pageSizes.remove(sessionId);
            currentPages.remove(sessionId);
            params.remove(sessionId);
            WsSessionManager.remove(JSON.toJSONString(data));
        }
    }


}
