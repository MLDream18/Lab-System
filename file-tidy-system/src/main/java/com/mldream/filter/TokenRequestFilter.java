package com.mldream.filter;


import com.alibaba.fastjson.JSONObject;
import com.mldream.pojo.vo.Result;
import com.mldream.service.impl.MyUserDetailsServiceImpl;
import com.mldream.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class TokenRequestFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /* 设置编码 */
        log.info("TokenRequestFilter doFilterInternal");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");

        String url = request.getRequestURL().toString();
        if (url.contains("/login") || url.contains("/register") || url.contains("/download")) {
            log.info("url: {}", url);
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;
        if(request.getHeader("token")!= null) {
            token = request.getHeader("token");
        } else {
            token = request.getHeader("Sec-WebSocket-Protocol");
        }
        response.addHeader("Sec-WebSocket-Protocol", token);
//        log.info("token: {}", token);

        if(!StringUtils.hasLength(token)) {
            Result error = Result.error("未登录，请先登录！");
            String notLoginJson = JSONObject.toJSONString(error);
            response.getWriter().write(notLoginJson);
            return;
        }
        try {
            Claims claims = JwtUtils.parseJwt(token);
            if(claims.containsKey("username") && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("username").toString());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            Result error = Result.error("登录已过期，请重新登录！");
            String expiredJson = JSONObject.toJSONString(error);
            response.getWriter().write(expiredJson);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
