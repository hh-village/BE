package com.sparta.village.global.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class VisitInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        redisTemplate.opsForValue().increment("visitor_count", 0);
        return true;
    }
}
