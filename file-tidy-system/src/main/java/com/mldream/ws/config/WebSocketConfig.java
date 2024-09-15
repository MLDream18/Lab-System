package com.mldream.ws.config;

import com.mldream.ws.handler.admin.AdminApprovalHandler;
import com.mldream.ws.handler.admin.AdminApprovalRecordHandler;
import com.mldream.ws.handler.admin.AdminClassScheduleHandler;
import com.mldream.ws.handler.admin.AdminSuggestionHandler;
import com.mldream.ws.handler.teacher.*;
import com.mldream.ws.interceptor.MyInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AdminApprovalHandler adminApprovalHandler;

    @Autowired
    private AdminApprovalRecordHandler adminApprovalRecordHandler;

    @Autowired
    private AdminClassScheduleHandler adminClassScheduleHandler;

    @Autowired
    private AdminSuggestionHandler adminSuggestionHandler;

    @Autowired
    private TeacherApplyHandler teacherApplyHandler;

    @Autowired
    private TeacherApplyFillHandler teacherApplyFillHandler;

    @Autowired
    private TeacherApplyApplyingHandler teacherApplyApplyingHandler;

    @Autowired
    private TeacherApplyHistoryApplyHandler teacherApplyHistoryApplyHandler;

    @Autowired
    private TeacherSuggestionHandler teacherSuggestionHandler;

    @Autowired
    private MyInterceptor myInterceptor;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(adminApprovalHandler, "/admin/approval")
                .setAllowedOrigins("*")
                .addHandler(adminApprovalRecordHandler, "/admin/approval/record")
                .setAllowedOrigins("*")
                .addHandler(adminClassScheduleHandler, "/admin/class-schedule")
                .setAllowedOrigins("*")
                .addHandler(adminSuggestionHandler, "/admin/suggestions")
                .setAllowedOrigins("*")
                .addHandler(teacherApplyHandler, "/teacher/apply")
                .setAllowedOrigins("*")
                .addHandler(teacherApplyFillHandler, "/teacher/apply/fill")
                .setAllowedOrigins("*")
                .addHandler(teacherApplyApplyingHandler, "/teacher/apply/applying")
                .setAllowedOrigins("*")
                .addHandler(teacherApplyHistoryApplyHandler, "/teacher/apply/historyApply")
                .setAllowedOrigins("*")
                .addHandler(teacherSuggestionHandler, "/teacher/suggestions")
                .setAllowedOrigins("*")
                .addInterceptors(myInterceptor);
    }
}
