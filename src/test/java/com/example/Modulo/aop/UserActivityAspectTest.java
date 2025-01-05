package com.example.Modulo.aop;

import com.example.Modulo.global.annotation.TrackUserActivity;
import com.example.Modulo.global.aop.UserActivityAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserActivityAspectTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private UserActivityAspect userActivityAspect;

    @Test
    @DisplayName("사용자 활동이 Redis에 정상적으로 기록되어야 한다")
    void trackUserActivity() throws Throwable {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null)
        );
        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        // when
        userActivityAspect.trackActivity(mock(ProceedingJoinPoint.class), mock(TrackUserActivity.class));

        // then
        verify(setOperations).add(
                eq("dau:" + LocalDate.now().toString()),
                eq("1")
        );
    }
}