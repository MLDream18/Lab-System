package com.mldream.Interceptor;

import com.alibaba.fastjson.JSONObject;
import com.mldream.pojo.vo.Result;
import com.mldream.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpServletRequest req = request;
        HttpServletResponse resp = response;
        /* 设置编码 */
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json;charset=UTF-8");
        String url = req.getRequestURL().toString();
        if (url.contains("/login") || url.contains("/register") || url.contains("/download")) {
            log.info("url: {}", url);
            return true;
        }

        String Jwts = req.getHeader("token");
        if(!StringUtils.hasLength(Jwts)) {
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }
        try {
            Claims claims = JwtUtils.parseJwt(Jwts);
//            System.out.println(claims.toString());
//            System.out.println(url);
            if(!claims.containsKey("role") && url.contains("/admin")) {
                Result error = Result.error("ILLEGAL_ACCESS");
                String illegal = JSONObject.toJSONString(error);
                resp.getWriter().write(illegal);
                return false;
            }
        } catch (Exception e) {
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }
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
