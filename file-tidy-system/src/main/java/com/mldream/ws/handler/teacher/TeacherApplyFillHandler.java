package com.mldream.ws.handler.teacher;

import com.alibaba.fastjson2.JSON;
import com.mldream.mapper.ApplyFormMapper;
import com.mldream.pojo.dto.ApplyFormDTO;
import com.mldream.pojo.dto.ApplySearchConditionsDTO;
import com.mldream.pojo.vo.ApplyFormInfoVO;
import com.mldream.pojo.vo.PageBean;
import com.mldream.pojo.vo.Result;
import com.mldream.service.ApplyFormService;
import com.mldream.service.LaboratoryService;
import com.mldream.utils.JwtUtils;
import com.mldream.ws.config.WsSessionManager;
import com.mldream.ws.handler.admin.AdminApprovalRecordHandler;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TeacherApplyFillHandler extends TextWebSocketHandler {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object token = session.getAttributes().get("token");
        Object page = "申请填写";
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户连接成功，放入在线用户缓存
            WsSessionManager.add(JSON.toJSONString(data), session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        if(payload.equals("heartbeatAsk")) {
            // 心跳包
            return;
        }

        ApplyFormService applyFormService = applicationContext.getBean(ApplyFormService.class);
        ApplyFormDTO applyFormDTO = JSON.parseObject(payload, ApplyFormDTO.class);

        Object token = session.getAttributes().get("token");
        Claims claims = JwtUtils.parseJwt((String) token);
        applyFormDTO.setSubmitTeacherId((Integer) claims.get("id"));
        if(applyFormDTO.getApplicantCollege().equals("人工智能学院")) {
            applyFormDTO.setState(1);
            applyFormDTO.setUnitOpinion("同意");
            applyFormDTO.setUnitOpinionName("人工智能学院领导");
            applyFormDTO.setUnitOpinionDate(LocalDate.now());
        }

        session.sendMessage(new TextMessage(JSON.toJSONString(applyFormService.submitApplyForm(applyFormDTO))));


        ApplyFormMapper applyFormMapper = applicationContext.getBean(ApplyFormMapper.class);
        LaboratoryService laboratoryService = applicationContext.getBean(LaboratoryService.class);

        /* 广播消息 */
        for (String key : WsSessionManager.SESSION_POOL.keySet()) {
            WebSocketSession wsSession = WsSessionManager.SESSION_POOL.get(key);
            Map<String, Object> keyData = JSON.parseObject(key);
            String page = (String) keyData.get("page");
            if (wsSession.isOpen()) {
                claims = JwtUtils.parseJwt((String) wsSession.getAttributes().get("token"));
                String sessionId = wsSession.getId();
                switch (page) {
                    case "待审批":
                        // 发送消息
                        List<ApplyFormInfoVO> result = applyFormMapper.selectApplyFormInfoSp();
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("result", result);
                        resultMap.put("role", claims.get("role"));
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(resultMap))));
                        break;
                    case "申请中":
                        // 发送消息
                        Integer teacherId = (Integer) claims.get("id");
                        List<ApplyFormInfoVO> applyForms = applyFormService.getApplyFormsBySubmitTeacherId(teacherId);
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(applyForms))));
                        break;
                    case "申请":
                        // 发送消息
                        Map<String, Object> params = TeacherApplyHandler.params.get(sessionId);
                        ApplySearchConditionsDTO applySearchConditionsDTO = ApplySearchConditionsDTO.builder()
                                .labId((Integer) params.get("labId"))
                                .startWeek((Integer) params.get("startWeek"))
                                .endWeek((Integer) params.get("endWeek"))
                                .startWeekday((Integer) params.get("startWeekday"))
                                .endWeekday((Integer) params.get("endWeekday"))
                                .startSection((String) params.get("startSection"))
                                .endSection((String) params.get("endSection"))
                                .state((Integer) params.get("state"))
                                .semesterId((Integer) params.get("semesterId"))
                                .build();
                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(laboratoryService.searchLab(applySearchConditionsDTO)))));
                        break;
                    case "审批记录":
                        PageBean pageBean = applicationContext.getBean(ApplyFormService.class).submitApprovalSelectForm(AdminApprovalRecordHandler.pageSizes.get(sessionId), AdminApprovalRecordHandler.currentPages.get(sessionId), AdminApprovalRecordHandler.params.get(sessionId));

                        wsSession.sendMessage(new TextMessage(JSON.toJSONString(Result.success(pageBean))));
                    default:
                        break;
                }

            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("TeacherApplyFillHandler closed. close_code: {}", status.getCode());
        Object token = session.getAttributes().get("token");
        Object page = "申请填写";
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("page", page);
        data.put("token", token);
        if (token != null) {
            // 用户退出，移除缓存
            WsSessionManager.remove(JSON.toJSONString(data));
        }
    }


}
