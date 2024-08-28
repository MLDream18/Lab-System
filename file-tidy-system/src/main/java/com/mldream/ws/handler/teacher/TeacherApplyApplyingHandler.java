package com.mldream.ws.handler.teacher;

import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.ApplyFormMapper;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.utils.JwtUtils;
import com.mldream.ws.config.WsSessionManager;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TeacherApplyApplyingHandler extends TextWebSocketHandler {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "申请中";
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }

        Claims claims = JwtUtils.parseJwt((String) token);
        Integer teacherId = (Integer) claims.get("id");
        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);
        List<ApplyFormInfoVO> applyForms = applyFormService.getApplyFormsBySubmitTeacherId(teacherId);
        session.sendMessage(new TextMessage(JSON.toJSONString(Result.success(applyForms))));
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

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("TeacherApplyApplyingHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "申请中";
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            WsSessionManager.remove(JSON.toJSONString(data));
        }
    }


}
