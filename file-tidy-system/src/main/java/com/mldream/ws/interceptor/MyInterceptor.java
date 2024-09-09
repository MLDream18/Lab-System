package com.mldream.ws.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.mldream.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MyInterceptor implements HandshakeInterceptor {

    /**
     * 握手前
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("握手开始");
        String token = request.getHeaders().getFirst("Sec-WebSocket-Protocol");
        String url = request.getURI().toString();
        log.info("url: {}", url);
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if(url.contains("/admin") && !claims.containsKey("role")) {
                response.setStatusCode(HttpStatus.FORBIDDEN); // 403
                return false;
            }
            attributes.put("claims", claims);
        } catch (Exception e) {
            log.info("用户 token {} 验证失败！", token);
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 401
//            response.setStatusCode(HttpStatus.PAYMENT_REQUIRED); // 402
            return false;
        }
        response.getHeaders().add("Sec-WebSocket-Protocol", token);
        attributes.put("token", token);
        return true;
    }

    /**
     * 握手后
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("握手完成");
    }

}
