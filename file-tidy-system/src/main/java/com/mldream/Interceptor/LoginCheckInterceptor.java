package com.mldream.Interceptor;

import com.alibaba.fastjson.JSONObject;
import com.mldream.pojo.vo.Result;
import com.mldream.service.impl.MyUserDetailsServiceImpl;
import com.mldream.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private MyUserDetailsServiceImpl userDetailsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /* 设置编码 */
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        String url = request.getRequestURL().toString();
        if (url.contains("/login") || url.contains("/register") || url.contains("/download")) {
            log.info("url: {}", url);
            return true;
        }

        String Jwts = request.getHeader("token");
//        if(!StringUtils.hasLength(Jwts)) {
//            Result error = Result.error("未登录，请先登录！");
//            String notLogin = JSONObject.toJSONString(error);
//            response.getWriter().write(notLogin);
//            return false;
//        }
//        try {
        Claims claims = JwtUtils.parseJwt(Jwts);
            System.out.println(claims.toString());
//            System.out.println(url);
        String role = (String) claims.get("role");
        if(role.equals("teacher") && url.contains("/admin")) {
            Result error = Result.error("权限不足，请联系管理员！");
            String illegal = JSONObject.toJSONString(error);
            response.getWriter().write(illegal);
            return false;
        }

//            if(claims.containsKey("username")) {
//                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.get("username").toString());
//                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }

//        } catch (Exception e) {
//            Result error = Result.error("未登录，请先登录！");
//            String notLogin = JSONObject.toJSONString(error);
//            response.getWriter().write(notLogin);
//            return false;
//        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
