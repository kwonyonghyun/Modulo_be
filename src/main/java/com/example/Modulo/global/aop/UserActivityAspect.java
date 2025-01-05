package com.example.Modulo.global.aop;

import com.example.Modulo.global.annotation.TrackUserActivity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityAspect {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String DAU_KEY_PREFIX = "dau:";

    @Around("(@annotation(trackUserActivity) || @within(trackUserActivity)) && !@annotation(com.example.Modulo.global.annotation.TrackUserActivity)(ignore=true)")
    public Object trackActivity(ProceedingJoinPoint joinPoint, TrackUserActivity trackUserActivity) throws Throwable {
        try {
            recordUserActivity();
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("Failed to track user activity", e);
            return joinPoint.proceed();
        }
    }

    private void recordUserActivity() {
        String key = DAU_KEY_PREFIX + LocalDate.now().toString();
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        redisTemplate.opsForSet().add(key, memberId.toString());
    }
}